package com.softel.seaa.Repository.Log;

import com.softel.seaa.Entity.Log.LogUserContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LogUserContentRepository extends JpaRepository<LogUserContent, Long>, PagingAndSortingRepository<LogUserContent, Long> {
    List<LogUserContent> findLogUserContentByIdFolderContains(String idFolder);
}