package com.softel.seaa.Services.Log;

import com.softel.seaa.Entity.Log.LogUserContent;
import com.softel.seaa.Services.Contract.GenericService;

import java.util.List;

public interface LogUserContentServices extends GenericService<LogUserContent> {
    List<LogUserContent> findLogUserContentByIdFolderContains(String idFolder);
}
