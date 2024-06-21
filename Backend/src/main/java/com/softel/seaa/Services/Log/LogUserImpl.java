package com.softel.seaa.Services.Log;

import com.softel.seaa.Controller.Exception.BDExcepcion.AnomalyFoundBDException;
import com.softel.seaa.Entity.Extra.Method;
import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.Log.LogUserContent;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Repository.Log.LogUserRepository;
import com.softel.seaa.Services.Implement.GenericImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogUserImpl extends GenericImpl<LogUser, LogUserRepository> implements LogUserServices{
    @Autowired
    private LogUserContentServices userContentServices;

    @Autowired
    public LogUserImpl(LogUserRepository dao) {
        super(dao);
    }

    @Override
    public void saveLog(User user, byte method) {
        LogUser logUser = new LogUser();
        String idUser = user.getId().toString();
        List<LogUserContent> logUserContent = userContentServices.findLogUserContentByIdFolderContains(idUser);

        logUser.setName(user.getName());
        logUser.setLastName(user.getLastName());
        logUser.setMethod(method);

        if (logUserContent.isEmpty())
            logUser.setLogUserContent(LogUserContent
                    .builder()
                    .idFolder(idUser)
                    .build());

        if (user.getSpecialist() != null) {
            logUser.setCi(user.getSpecialist().getCi());
        }else{
            logUser.setCi("noCI");
        }

        System.out.println("[TRYING_SAVE] :: "+ logUser);

        LogUser logUserSave = dao.save(logUser);

        System.out.println("[SAVE] :: "+logUserSave);
    }

    @Override
    public void saveLog(LogUser user) {
        System.out.println("[TRYING_SAVE] :: "+ user);
        String idFolder = user.getLogUserContent().getIdFolder();

        List<LogUserContent> logUserContent = userContentServices.findLogUserContentByIdFolderContains(idFolder);

        LogUser logUserSave;

        if (!logUserContent.isEmpty()) {
            user.setLogUserContent(logUserContent.get(0));
        }
        logUserSave = dao.save(user);

        System.out.println("[SAVE] :: "+logUserSave);
    }

    @Override
    public void saveLogAll(List<User> user) {
        user.stream().parallel().forEach(userFor -> saveLog(userFor, Method.CREATE));
    }

    @Transactional(readOnly = true)
    @Override
    public List<LogUser> findLogUsersByNameContains(String name) {
        return dao.findLogUsersByNameContains(name);
    }
}
