package com.softel.seaa.Controller;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.softel.seaa.Controller.Exception.BDExcepcion.NotExistException;
import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.ListEmptyException;
import com.softel.seaa.Controller.Exception.SeaaException.DuplicateSeaaInSpecialist;
import com.softel.seaa.Controller.Exception.SeaaException.NotEspecialistException;
import com.softel.seaa.Controller.Exception.SeaaException.SeaaNotFoundException;
import com.softel.seaa.Controller.Exception.UserException.ArgumentInvalidException;
import com.softel.seaa.Controller.SeaaManager.CreateXML;
import com.softel.seaa.Controller.SeaaManager.FileSystem;
import com.softel.seaa.Controller.SeaaManager.ReadXML;
import com.softel.seaa.Controller.SeaaManager.Structure.*;
import com.softel.seaa.Entity.*;
import com.softel.seaa.Entity.Extra.Method;
import com.softel.seaa.Entity.Extra.Roles;
import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.Log.LogUserContent;
import com.softel.seaa.Entity.Query;
import com.softel.seaa.Security.Token;
import com.softel.seaa.Services.Contract.QueryService;
import com.softel.seaa.Services.Contract.SeaaService;
import com.softel.seaa.Services.Contract.SpecialistService;
import com.softel.seaa.Services.Contract.UserService;
import com.softel.seaa.Services.Thread.LoggerThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Controller
@RequestMapping("api/v1/seaa")
public class SeaaController {
    @Autowired
    private SeaaService seaaService;
    @Autowired
    private UserService userService;
    @Autowired
    private QueryService queryService;

    @Autowired
    private LoggerThread loggerThread;

    private final String NOT = "not";

    /* Return all Seaa */
    @GetMapping("/user")
    public ResponseEntity<List<Seaa>> findAllSeaa() {
        List<Seaa> seaaList = seaaService.findAll();
        if (seaaList.isEmpty()) throw new ListEmptyException("getAllSeaa", "Search of all Seaa");
        return new ResponseEntity<>(seaaList, HttpStatus.OK);
    }

    @GetMapping("/specialist/findSeaaById/{idSeaa}")
    public ResponseEntity<Seaa> findSeaaById(@PathVariable long idSeaa) {
        Seaa seaa = seaaService.findById(idSeaa);
        if (seaa == null) throw new NotExistException("getfindSeaa", "Seaa - " + idSeaa);
        return new ResponseEntity<>(seaa, HttpStatus.OK);
    }

    @GetMapping("/specialist/graph/{idSeaa}")
    public ResponseEntity<List<Integer>> graph(@PathVariable Long idSeaa){
        Seaa seaa = seaaService.findById(idSeaa);

        List<Integer> dataset = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));

        seaa.getQueries().stream()
                .map(Query::getDate)
                .sorted(Comparator.comparing(LocalDateTime::getMonth))
                .forEachOrdered(localDateTime -> {
                    var month = localDateTime.getMonth().getValue();
                    dataset.set(month-1, dataset.get(month-1) + 1);
                });
        return new ResponseEntity<>(dataset, HttpStatus.OK);
    }

    @GetMapping("/specialist/relationUserSeaa/{idSeaa}")
    public ResponseEntity<List<Map<String, Object>>> userUsedSeaa(@PathVariable Long idSeaa) {
        Seaa seaa = seaaService.findById(idSeaa);
        List<Map<String, Object>> userList = new ArrayList<>();
        AtomicLong idCurrentUser = new AtomicLong(-1);
        AtomicInteger pos = new AtomicInteger(-1);

        seaa.getQueries().stream()
                .sorted(Comparator.comparing(Query::getUser, Comparator.comparing(User::getId)))
                .forEachOrdered(query -> {
                    User user = query.getUser();
                    Query queryAux = new Query();
                    queryAux.setUUID(query.getUUID());
                    queryAux.setDate(query.getDate());

                    if (user.getId() == idCurrentUser.get()){
                        ((List<Query>) userList.get(pos.get()).get("queries")).add(queryAux);
                    }else{
                        Map<String, Object> userMap = new HashMap<>();
                        List<Query> queryList = new ArrayList<>();
                        queryList.add(queryAux);

                        idCurrentUser.set(user.getId());
                        pos.getAndIncrement();

                        userMap.put("name", user.getName());
                        userMap.put("lastName", user.getLastName());
                        userMap.put("id", user.getId());
                        userMap.put("queries", queryList);

                        userList.add(userMap);
                    }
                });

        return ResponseEntity.ok(userList);
    }

    @GetMapping("/user/newQuery/{idSea}")
    public ResponseEntity<Question> newQuery(@CookieValue("Authorization") String token, @PathVariable Long idSea) throws Exception {
        User user = getUserById(Token.extractIdInsideToken(token));
        Seaa seaa = seaaService.findById(idSea);

        Answer answer = new Answer();
        answer.insertProblemAndOption(seaa.getProblem(), seaa.getOption());
        String answerUser = user.getId() + "answer.tmp";
        String questionUser = user.getId() + "question.tmp";
        String rootQueryAnswer = FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + answerUser;
        String rootQueryQuestion = FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + questionUser;

        deleteFile(rootQueryAnswer);
        deleteFile(rootQueryQuestion);

        CreateXML.initialTags(new File(rootQueryAnswer), answer, true);

        FileSystem.executeCMD(answerUser, questionUser, seaa.getName());

        waitForQuestionFile(rootQueryQuestion);

        return new ResponseEntity<>(ReadXML.readQuestion(rootQueryQuestion), HttpStatus.CREATED);
    }

    @GetMapping("/specialist/changeSeaa/{idSeaa}+{idUser}")
    public ResponseEntity<String> changeSeaa(@PathVariable Long idUser, @PathVariable Long idSeaa) {
        List<Seaa> seaaList = seaaService.findAll();
        User user = userService.findById(idUser);
        Seaa seaa = getSeaaById(idSeaa);

        if (seaaList.stream()
                .anyMatch(se -> se.getSpecialist().getId().equals(user.getSpecialist().getId())
                        && !seaa.equals(se))) {
            throw new DuplicateSeaaInSpecialist(user.getSpecialist(), seaa.getName());
        }

        seaa.setSpecialist(user.getSpecialist());
        user.getSpecialist().getSeaaList().add(seaa);

        seaaService.save(seaa);

        return new ResponseEntity<>("Successful change", HttpStatus.OK);
    }

    @GetMapping("/user/upload/{idUser}+{nameFile}")
    public ResponseEntity<Object> uploadFile(@PathVariable Long idUser, @PathVariable String nameFile)
            throws IOException, ClassNotFoundException {
        User user = getUserById(idUser);

        if (user.getQueries().stream().noneMatch(query -> query.getUUID().equals(nameFile)))
            throw new NotExistException(nameFile, nameFile);

        String pathName = getPathnameUser(idUser, nameFile);

        if (!new File(pathName).exists())
            throw new FileNotFoundException();

        FileInputStream fi = new FileInputStream(pathName);
        ObjectInputStream oi = new ObjectInputStream(fi);

        Object object = oi.readObject();
        oi.close();
        fi.close();

        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    @PostMapping("/specialist/create")
    public ResponseEntity<Seaa> createSeaa(@RequestBody Seaa seaa, @CookieValue("Authorization") String token) {
        User user = userService.findById(Token.extractIdInsideToken(token));

        if (user.getSpecialist() == null) {
            throw new NotEspecialistException(seaa);
        }
        if (user.getSpecialist().getSeaaList().stream().anyMatch(se -> se.getName().equals(seaa.getName())))
            throw new DuplicateSeaaInSpecialist(user.getSpecialist(), seaa);

        user.getSpecialist().getSeaaList().add(seaa);
        seaa.setSpecialist(user.getSpecialist());
        Seaa auxSeaa = seaaService.save(seaa);

        loggerThread.saveLog(LogUser.builder()
                .ci(NOT)
                .name("Seaa --- " + seaa.getName())
                .lastName(NOT)
                .method(Method.CREATE)
                .logUserContent(LogUserContent.builder()
                        .idFolder(NOT).build())
                .build());

        return new ResponseEntity<>(auxSeaa, HttpStatus.CREATED);
    }

    @PostMapping("/user/queryVar/{idSeaa}")
    public ResponseEntity<Question> query(@CookieValue("Authorization") String token, @PathVariable Long idSeaa, @RequestBody Var answer) throws Exception {
        String rootQueryQuestion = processingVarOrProp(Token.extractIdInsideToken(token), idSeaa, answer, -1);

        return new ResponseEntity<>(ReadXML.readQuestion(rootQueryQuestion), HttpStatus.CREATED);
    }

    @PostMapping("/user/queryProp/{idSeaa}")
    public ResponseEntity<Question> query(@CookieValue("Authorization") String token, @PathVariable Long idSeaa, @RequestBody Prop answer) throws Exception {
        String rootQueryQuestion = processingVarOrProp(Token.extractIdInsideToken(token), idSeaa, answer, -1);

        return new ResponseEntity<>(ReadXML.readQuestion(rootQueryQuestion), HttpStatus.CREATED);
    }

    @PostMapping("/user/updateAnswerVar/{numAnswerArr}+{idSeaa}")
    public ResponseEntity<Question> updateAnswerVar(@PathVariable int numAnswerArr, @RequestBody Var answer,
                                                    @CookieValue("Authorization") String token, @PathVariable Long idSeaa) throws Exception {
        validUpdateAnswer(numAnswerArr);

        String rootQuestion = processingVarOrProp(Token.extractIdInsideToken(token), idSeaa, answer, numAnswerArr);

        return new ResponseEntity<>(ReadXML.readQuestion(rootQuestion), HttpStatus.OK);
    }

    @PostMapping("/user/updateAnswerProp/{numAnswerArr}+{idSeaa}")
    public ResponseEntity<Question> updateAnswerProp(@PathVariable int numAnswerArr, @RequestBody Prop answer,
                                                     @CookieValue("Authorization") String token, @PathVariable Long idSeaa) throws Exception {
        validUpdateAnswer(numAnswerArr);

        String rootQuestion = processingVarOrProp(Token.extractIdInsideToken(token), idSeaa, answer, numAnswerArr);

        return new ResponseEntity<>(ReadXML.readQuestion(rootQuestion), HttpStatus.OK);
    }

    @PostMapping("/user/save/{idSeaa}")
    public ResponseEntity<Query> save(@RequestBody Object object, @PathVariable Long idSeaa, @CookieValue("Authorization") String token)
            throws IOException {

        long idUser = Token.extractIdInsideToken(token);
        User user = getUserById(idUser);
        Seaa seaa = getSeaaById(idSeaa);
        String nameFile = UUID.randomUUID().toString();
        String pathname = getPathnameUser(idUser, nameFile);
        Query query = new Query();

        query.setUUID(nameFile);
        query.setUser(user);

        seaa.getQueries().add(query);
        user.getQueries().add(query);

        createDirectory(pathname);

        writeFile(object, pathname);

        queryService.save(query);
        userService.save(user);
        seaaService.save(seaa);

        return new ResponseEntity<>(query, HttpStatus.OK);
    }

    private static void createDirectory(String pathname) throws IOException {
        File file = new File(pathname);

        if (!file.getParentFile().exists())
            file.getParentFile().mkdir();

        if (!file.exists()) {
            file.createNewFile();
        }
    }

    @PostMapping("/specialist/shareSeaa/{idSeaa}+{idUser}")
    public ResponseEntity<Seaa> shareSeaa(@RequestBody List<Long> idSharedUsers, @PathVariable Long idUser, @PathVariable Long idSeaa) throws Exception {
        User user = getUserById(idUser);
        Seaa seaa = getSeaaById(idSeaa);

        if (user.getSpecialist().getSeaaList().stream().noneMatch(s -> s.getId().equals(idSeaa)))
            throw new SeaaNotFoundException(seaa.getName());

        if (user.getRoles().contains(new Rol(Roles.ROLE_EXPERT)))
            throw new Exception("It's not specialist");

        List<User> usersSharedSeaa = new ArrayList<>();

        idSharedUsers.forEach(id -> {
            User u = getUserById(id);
            usersSharedSeaa.add(u);
            u.getSpecialist().getSeaaShared().add(idSeaa);
        });

        userService.saveAll(usersSharedSeaa);

        return new ResponseEntity<>(seaa, HttpStatus.OK);
    }

    @PostMapping("/specialist/uploadSeaaRarOrZip/{nameSeaa}")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String nameSeaa) throws Exception {
        String message;
        String extension = "";
        String originalFilename = file.getOriginalFilename();
        String rar = ".rar";
        String zip = ".zip";

        extension = getExtension(extension, originalFilename, rar, zip);

        try {
            copyAndExtractFileAndDelete(file, nameSeaa, extension);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = infoError(nameSeaa);

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
        }
    }

    private Map<String, String> infoError(String nameSeaa) {
        String message;
        message = "ERROR: Error to storage the file";

        Map<String, String> error = new HashMap<>();
        error.put("message", message);

        Optional<Seaa> seaaError = seaaService.findByNameIgnoreCase(nameSeaa);
        seaaError.ifPresent(seaa -> seaaService.deleteById(seaa.getId()));

        return error;
    }

    private void copyAndExtractFileAndDelete(MultipartFile file, String nameSeaa, String extension) throws IOException, RarException {
        Path rootLocation = Paths.get(FileSystem.getRoot()).resolve(file.getOriginalFilename());

        Files.copy(file.getInputStream(), rootLocation, StandardCopyOption.REPLACE_EXISTING);

        extractZipOrRar(FileSystem.getRoot(), nameSeaa, file.getOriginalFilename(), extension);

        deleteComprimed(rootLocation);
    }

    private void deleteComprimed(Path rootLocation) {
        new File(rootLocation.toUri()).delete();
    }

    private static String getExtension(String extension, String originalFilename, String rar, String zip) throws Exception {
        if (originalFilename != null)
            extension = originalFilename.contains(rar) ? rar : originalFilename.contains(zip) ? zip : "";

        if (extension.isEmpty())
            throw new Exception("Bad extension");
        return extension;
    }

    private void extractZipOrRar(String rootFile, String nameSeaa, String nameFile, String extension) throws RarException, IOException {
        if (extension.equals(".zip"))
            extractZip(rootFile, nameSeaa, nameFile);
        else
            extractRar(rootFile, nameSeaa, nameFile);
    }

    private void extractRar(String rootFile, String nameSeaa, String nameFile) throws RarException, IOException {
        File f = new File(rootFile + "/" + nameFile);
        Archive archive = new Archive(f);
        FileHeader fh = archive.nextFileHeader();

        while(fh!=null){
            File fileEntry = new File(rootFile + "/" + nameSeaa, fh.getFileName().trim());
            File fileRoot = fileEntry.getParentFile();

            if (!fileRoot.exists())
                fileRoot.mkdirs();

            if (!fileEntry.isDirectory()) {
                FileOutputStream os = new FileOutputStream(fileEntry);
                archive.extractFile(fh, os);
                os.close();
            }
            fh = archive.nextFileHeader();
        }
    }

    private static void extractZip(String rootFile, String nameSeaa, String nameFile) {
        byte[] buffer = new byte[16384];

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(rootFile + "/" + nameFile));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(rootFile + "/" + nameSeaa, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();

                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PutMapping("/specialist/updateQuestion/{idUser}")
    public ResponseEntity<Object> updateQuestion(@RequestBody List<AnswerAndQuestionsE> objects, @PathVariable long idUser) {
        AtomicReference<String> pathnameUser = new AtomicReference<>();
        AtomicReference<File> file = new AtomicReference<>();

        objects.forEach(o -> {

            pathnameUser.set(getPathnameUser(idUser, o.getUuid_queries()));
            file.set(new File(pathnameUser.get()));

            if (file.get().exists())
                file.get().delete();
            else try {
                throw new FileNotFoundException(o.getUuid_queries());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {

                writeFile(AnswerAndQuestions.builder()
                        .question(o.getQuestion())
                        .answer(o.getAnswer())
                        .build(),
                        pathnameUser.get());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(objects);
    }
    @DeleteMapping("/specialist/deleteSeaa/{idSeaa}")
    public ResponseEntity<Seaa> deleteSeaa(@PathVariable Long idSeaa) {
        Seaa seaa = getSeaaById(idSeaa);

        loggerThread.saveLog(LogUser.builder()
                        .ci(NOT)
                        .name("Seaa --- " + seaa.getName())
                        .lastName(NOT)
                        .method(Method.DELETE)
                        .logUserContent(LogUserContent.builder()
                                .idFolder(NOT).build())
                        .build());

        seaaService.deleteById(idSeaa);

        return new ResponseEntity<>(seaa, HttpStatus.OK);
    }

    //6-	Debe dar la posibilidad de exportar las preguntas

    // en PDF al Rol Experto.

    //@GetMapping("/generatePDF")

    //public ResponseEntity<Resource> generatePDF(@PathVariable )
    private static void deleteFile(String rootQueryAnswer) {
        File file = new File(rootQueryAnswer);

        if (file.exists())
            file.delete();
    }

    private static void writeFile(Object object, String pathname) throws IOException {
        FileOutputStream fo = new FileOutputStream(pathname);
        ObjectOutputStream oo = new ObjectOutputStream(fo);

        oo.writeObject(object);
        oo.flush();
        oo.close();
        fo.close();
    }

    private Seaa getSeaaById(Long idSeaa) {
        return seaaService.findById(idSeaa);
    }

    private User getUserById(Long idUser) {
        return userService.findById(idUser);
    }

    private static String getPathnameUser(Long idUser, String nameFile) {
        String extension = ".cas";
        if (!nameFile.contains(extension))
            nameFile += extension;

        return FileSystem.getRootUsers() + "/" + idUser + "/" + nameFile;
    }

    private static void validUpdateAnswer(int numAnswerArr) {
        if (numAnswerArr < 0)
            throw new ArgumentInvalidException("numAnswerArr", Integer.toString(numAnswerArr));
    }

    private String processingVarOrProp(Long idUser, Long idSea, Object object, int pos) throws Exception {
        User user = getUserById(idUser);
        Seaa seaa = getSeaaById(idSea);

        String answerUser = user.getId() + "answer.tmp";
        String questionUser = user.getId() + "question.tmp";

        String rootQueryAnswer = FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + answerUser;
        String rootQueryQuestion = FileSystem.getRoot() + "/" + seaa.getName() + "/BC/" + questionUser;

        if (pos == -1) {
            if (object instanceof Var var) {
                CreateXML.addAnswer(rootQueryAnswer, var);
            } else {
                CreateXML.addAnswer(rootQueryAnswer, (Prop) object);
            }
        }else{
            if (object instanceof Var var) {
                CreateXML.modifyAnswer(rootQueryAnswer, var, pos);
            } else {
                CreateXML.modifyAnswer(rootQueryAnswer, (Prop) object, pos);
            }
        }

        deleteToWaitNewQuestion(rootQueryQuestion);

        FileSystem.executeCMD(answerUser, questionUser, seaa.getName());

        waitForQuestionFile(rootQueryQuestion);

        return rootQueryQuestion;
    }

    private static void deleteToWaitNewQuestion(String rootQueryQuestion) {
        File file = new File(rootQueryQuestion);

        if (file.exists())
            file.delete();
    }

    private static void waitForQuestionFile(String rootQueryQuestion) throws Exception {
        File file = new File(rootQueryQuestion);

        long timeMillis = System.currentTimeMillis();

        while (!file.exists()){
            Thread.sleep(1000);

            if (System.currentTimeMillis() - timeMillis > 10000){
                System.out.println("[WARN]:::The question file not was created");
                throw new Exception("The question file not was created");
            }
        }
    }

}
