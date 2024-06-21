package com.softel.seaa.Services.Log;

import com.softel.seaa.Controller.Exception.UserException.IncorrectFieldException;
import com.softel.seaa.Entity.Log.LogUserContent;
import com.softel.seaa.Repository.Log.LogUserContentRepository;
import com.softel.seaa.Services.Implement.GenericImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogUserContentImpl extends GenericImpl<LogUserContent, LogUserContentRepository> implements LogUserContentServices{
    @Autowired
    public LogUserContentImpl(LogUserContentRepository dao) {
        super(dao);
    }

    @Transactional(readOnly = true)
    @Override
    public List<LogUserContent> findLogUserContentByIdFolderContains(String idFolder) {
        if (idFolder.matches("[0-9]*"))
            return dao.findLogUserContentByIdFolderContains(idFolder);
        else
            throw new IncorrectFieldException(idFolder, "idFolder");
    }
}
