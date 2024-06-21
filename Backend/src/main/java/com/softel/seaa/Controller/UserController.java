package com.softel.seaa.Controller;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.ListEmptyException;
import com.softel.seaa.Controller.Exception.UserException.EmptyFieldException;
import com.softel.seaa.Controller.Exception.UserException.IncorrectFieldException;
import com.softel.seaa.Controller.SeaaManager.FileSystem;
import com.softel.seaa.Entity.*;
import com.softel.seaa.Entity.Extra.Method;
import com.softel.seaa.Entity.Extra.NotificationType;
import com.softel.seaa.Entity.Extra.Roles;
import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.Log.LogUserContent;
import com.softel.seaa.Security.Token;
import com.softel.seaa.Services.Contract.RolService;
import com.softel.seaa.Services.Contract.SeaaService;
import com.softel.seaa.Services.Contract.UserService;
import com.softel.seaa.Services.Thread.LoggerThread;
import com.softel.seaa.Services.Thread.NotificationThread;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SeaaService seaaService;
    @Autowired
    private RolService rolService;
    @Autowired
    private LoggerThread loggerThread;

    @Autowired
    private NotificationThread notificationThread;

//-----------------------GetMappings-----------------------------------

    /* Return all user */
    /* Return a page's user */

    @GetMapping()
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) throw new ListEmptyException("getAllUsers", "Search of all users");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getAllRoles() {
        List<Rol> rolList = rolService.findAll();
        if (rolList.isEmpty()) throw new ListEmptyException("getAllRoles", "Search of all roles");
        return new ResponseEntity<>(rolList, HttpStatus.OK);
    }

    @GetMapping("/specialist/findAllUserSpecialist")
    public ResponseEntity<List<User>> findAllUserSpecialist(@CookieValue("Authorization") String token) {
        List<User> users = userService.findByRoles_NameLikeAndSpecialistNotNull(Roles.ROLE_EXPERT.name());
        long idUser = Token.extractIdInsideToken(token);

        users.removeIf(user -> user.getId().equals(idUser));

        if (users.isEmpty()) {
            throw new ListEmptyException("findAllUserSpecialist", "Search of all users specialists");
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/user/page/{page}")
    public ResponseEntity<Page<User>> findAllUserPage(@PathVariable int page) {
        Page<User> users = userService.findAll(PageRequest.of(page,10));
        if (users.isEmpty()) throw new ListEmptyException("Users", "Search of all users");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /* Return a specific user */
    @GetMapping("/searchID/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id){
        User user = userService.findById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /* Returns all users that contenting the specified name */
    @GetMapping("/searchPhoneNumber/{phoneNumber}")
    public ResponseEntity<User> findByPhoneNumber(@PathVariable String phoneNumber){
        User user = userService.findByPhoneNumberContains(phoneNumber).orElseThrow();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/graph")
    public ResponseEntity<List<Integer>> graph(@CookieValue("Authorization") String token){
        User user = userService.findById(Token.extractIdInsideToken(token));

        List<Integer> dataset =  new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));

        user.getQueries().stream()
                .map(Query::getDate)
                .sorted(Comparator.comparing(LocalDateTime::getMonth))
                .forEachOrdered(localDateTime -> {
                    var month  = localDateTime.getMonth().getValue();
                    dataset.set(month - 1, dataset.get(month - 1) + 1);
                });
        return new ResponseEntity<>(dataset, HttpStatus.OK);
    }

    @GetMapping("/enabledUser/{idUser}")
    public ResponseEntity<Boolean> enabledUser(@PathVariable long idUser){
        User user =  userService.findById(idUser);
        user.setEnabled(!user.isEnabled());

        User userAux = userService.save(user);

        return new ResponseEntity<>(userAux.isEnabled(), HttpStatus.OK);
    }

//-----------------------PostMappings-----------------------------------

    /* Create and return a User */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user){
        checkUser(user);

        User userAux;
        Rol rol = rolService.findAll().stream()
                .filter(nameRol -> nameRol.getName().equals(Roles.ROLE_USER.name()))
                .findFirst()
                .orElse(new Rol(Roles.ROLE_USER));

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.getRoles().add(rol);
        if (user.getSpecialist() != null) {
            user.getSpecialist().getSeaaList().forEach(seaa -> seaa.setSpecialist(user.getSpecialist()));
        }
        user.setEnabled(true);

        userAux = userService.save(user);

        loggerThread.saveLog(userAux, Method.CREATE);
        createNotification(
                "Nuevo usuario creado",
                "Se creó un nuevo usuario con el nombre: " + userAux.getName() + " " + userAux.getLastName(),
                NotificationType.CREATE,
                userAux.getId());

        return new ResponseEntity<>(userAux, HttpStatus.CREATED);
    }

    private static void checkUser(User user) throws IllegalArgumentException{
        if (user.getName().toLowerCase().contains("admin"))
            throw new IllegalArgumentException("Can't create the user");
        if (!user.getPassword().matches("[a-zA-Z0-9@#$%&*._+]+"))
            throw new IllegalArgumentException("The password does not meet the requirements");
    }

    private void createNotification(String title, String content, String type, Long idUser) {
        notificationThread.notificator(Notification.builder()
                        .title(title)
                        .content(content)
                        .view(false)
                        .type(type)
                        .iduser(idUser)
                        .build());
    }

    @PostMapping("/saveAllUser")
    public ResponseEntity<List<User>> saveAllUser(@RequestBody @Valid List<User> users){
        if (users.isEmpty())
            throw new ListEmptyException();

        users.stream().parallel()
                .filter(user -> user.getSpecialist() != null && user.getSpecialist().getSeaaList().size() > 0)
                .forEach(user -> {
                    for (Seaa seaa : user.getSpecialist().getSeaaList()) {
                        seaa.setSpecialist(user.getSpecialist());
                    }
                });

        List<User> usersAux = userService.saveAll(users);

        loggerThread.saveLogAll(usersAux);
        createNotification(
                "Creación de varios usuarios",
                "Fueron creados un total de " + users.size() + " usuarios",
                NotificationType.CREATE,
                -1L);

        return new ResponseEntity<>(usersAux, HttpStatus.OK);
    }
//-----------------------PutMappings-----------------------------------

    /* Update a User */
    @PutMapping("/update/{id}")
    public ResponseEntity<User> update(@RequestBody @Valid User user, @PathVariable long id) {
        User previousUser = userService.findById(id);

        insertSpecialist(user, previousUser);

        previousUser.setEnabled(user.isEnabled());
        previousUser.setName(user.getName());
        previousUser.setPassword(user.getPassword());
        previousUser.setLastName(user.getLastName());
        previousUser.getQueries().addAll(user.getQueries());
        previousUser.setRoles(user.getRoles());
        previousUser.setPhoneNumber(user.getPhoneNumber());
        addSpecialistToSeaaList(previousUser);

        User userSave = userService.save(previousUser);

        loggerThread.saveLog(userSave, Method.UPDATE);
        notificationByUpdate(userSave);

        return new ResponseEntity<>(userSave, HttpStatus.CREATED);
    }

    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user, @CookieValue("Authorization") String token) {
        User previousUser = userService.findById(Token.extractIdInsideToken(token));

        insertSpecialist(user, previousUser);

        previousUser.setName(user.getName());
        previousUser.setPassword(user.getPassword());
        previousUser.setLastName(user.getLastName());
        previousUser.setQueries(user.getQueries());
        previousUser.setPhoneNumber(user.getPhoneNumber());

        addSpecialistToSeaaList(previousUser);

        User userSave = userService.save(previousUser);

        loggerThread.saveLog(userSave, Method.UPDATE);
        notificationByUpdate(userSave);

        return new ResponseEntity<>(userSave, HttpStatus.CREATED);
    }

    private static void insertSpecialist(User user, User previousUser) {
        if (user.getSpecialist() != null) {
            Specialist previousSpecialist = previousUser.getSpecialist();
            Specialist specialist = user.getSpecialist();

            previousSpecialist.getSeaaList()
                    .forEach(seaa -> specialist.getSeaaList()
                            .forEach(s -> {

                                if (seaa.getId().equals(s.getId())) {
                                    seaa.setOption(s.getOption());
                                    seaa.setName(s.getName());
                                    seaa.setDescription(s.getDescription());
                                    seaa.setVersion(s.getVersion());
                                    seaa.setYear(s.getYear());
                                }

                            }));

            previousSpecialist.getSeaaList()
                    .addAll(specialist.getSeaaList()
                            .stream()
                            .filter(seaa -> seaa.getId() == null)
                            .collect(Collectors.toSet())
                    );

            previousSpecialist.setBiography(specialist.getBiography());
            previousSpecialist.setCi(specialist.getCi());
            previousSpecialist.setKnowledgeArea(specialist.getKnowledgeArea());
            previousSpecialist.setProfessionalRegister(specialist.getProfessionalRegister());
            previousSpecialist.setScientificCategory(specialist.getScientificCategory());
        }
    }

    private void addSpecialistToSeaaList(User previousUser) {
        if (previousUser.getSpecialist() != null && !previousUser.getSpecialist().getSeaaList().isEmpty()) {
            previousUser.getSpecialist().getSeaaList()
                    .forEach(seaa -> seaa.setSpecialist(previousUser.getSpecialist()));
        }
    }

    /* Add Seaa to user */
    @PutMapping("/specialist/addSeaa/{idSeaa}+{idUser}")
    public ResponseEntity<Specialist> addSeaa(@PathVariable Long idSeaa, @PathVariable Long idUser) {
        Seaa seaa = seaaService.findById(idSeaa);
        User user = userService.findById(idUser);

        seaa.setSpecialist(user.getSpecialist());
        user.getSpecialist().getSeaaList().add(seaa);

        Seaa auxSeaa = seaaService.save(seaa);
        User userAux = userService.save(user);

        loggerThread.saveLog(userAux, Method.UPDATE);
        notificationByUpdate(user);

        return new ResponseEntity<>(auxSeaa.getSpecialist(), HttpStatus.OK);
    }

    @GetMapping("/addRolSpecialist/{idUser}")
    public ResponseEntity<User> addRolSpecialist(@PathVariable Long idUser){
        User user = userService.findById(idUser);

        if (user.getSpecialist() == null){
            user.setSpecialist(new Specialist());
            user.getSpecialist().setSeaaList(new HashSet<>());
            user.getSpecialist().setSeaaShared(new HashSet<>());
        }

        List<Rol> rolList = rolService.findAll();
        user.getRoles().add(rolList.stream()
                .filter(rol -> rol.getName().equals(Roles.ROLE_EXPERT.name()))
                .findFirst()
                .orElseThrow());

        User userAux = userService.save(user);

        loggerThread.saveLog(LogUser.builder()
                        .ci("notCI")
                        .lastName(user.getLastName())
                        .name(user.getName())
                        .method(Method.UPDATE)
                        .logUserContent(LogUserContent.builder()
                                .idFolder(user.getId().toString())
                                .build())
                        .build());
        notificationThread.hiddenbtn(idUser);

        return ResponseEntity.ok(userAux);
    }

    private void notificationByUpdate(User user) {
        createNotification(
                "Usuario actualizado",
                "Se actualizaron datos del usuario " + user.getName() + " " + user.getLastName(),
                NotificationType.INFO,
                user.getId());
    }
//-----------------------DeleteMappings-----------------------------------

    /* Delete user by id */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws InterruptedException {
        User user = userService.findById(id);
        List<Seaa> seaaList = seaaService.findAll();

        seaaList.stream().parallel().forEach(seaa -> {
            int seaaQuerySize = seaa.getQueries().size();

            List<Query> queries = user.getQueries().stream()
                    .filter(query -> seaa.getQueries().contains(query))
                    .toList();

            queries.forEach(query -> {
                seaa.getQueries().remove(query);
                user.getQueries().remove(query);
            });

            if (seaaQuerySize != seaa.getQueries().size())
                seaaService.save(seaa);
        });

        try {
            userService.deleteById(id);
        } catch (IllegalStateException e) {
            Thread.sleep(200);
            deleteUser(id);
        }

        if (user.getSpecialist() != null) {
            user.getSpecialist().getSeaaList().forEach(seaa -> {
                File file = new File(FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + user.getId() + "answer.tmp");

                if (file.exists()) file.delete();

                file = new File(FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + user.getId() + "question.tmp");

                if (file.exists()) file.delete();
            });
        }

        loggerThread.saveLog(user, Method.DELETE);

        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    /* Delete all users */
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllUsers() throws IOException {
        List<User> users = userService.findAll();
        List<Seaa> seaaList = seaaService.findAll();

        seaaList.stream()
                .parallel()
                .forEach(seaa -> {
                    int seaaQuerySize = seaa.getQueries().size();

                    users.stream()
                            .parallel()
                            .forEach(user -> user.getQueries()
                                    .forEach(queries -> seaa.getQueries().remove(queries)));

                    if (seaaQuerySize != seaa.getQueries().size())
                        seaaService.save(seaa);
                });


        userService.deleteAll();
        File file = new File(FileSystem.getRootUsers());

        if (file.exists()) {
            if (file.delete()) {
                file.createNewFile();
            }
        }

        createAdminIfNotExist();

        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    private void createAdminIfNotExist() {
        boolean notFoundUser = userService.findByNameContains("Admin").isEmpty();

        if (notFoundUser){
            User admin = new User();
            admin.setName("Admin");
            admin.setEnabled(true);
            admin.setLastName("Admin Admin");
            admin.setPhoneNumber("11111111");
            admin.setPassword("Admin#*01");

            if (admin.getRoles().isEmpty()) {
                rolService.findAll().stream()
                        .filter(rol -> Roles.ROLE_ADMIN.name().equals(rol.getName()))
                        .findFirst()
                        .ifPresentOrElse(
                                rol -> admin.getRoles().add(rol),
                                () -> admin.getRoles().add(new Rol(Roles.ROLE_ADMIN))
                        );
                userService.save(admin);
            }
        }
    }
}
