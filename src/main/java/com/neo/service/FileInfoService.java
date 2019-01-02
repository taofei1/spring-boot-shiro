package com.neo.service;

import com.neo.entity.FileInfo;
import com.neo.entity.SyncFilePlan;

import java.util.List;

public interface FileInfoService {
    List<FileInfo> findBySyncFilePlan(SyncFilePlan syncFilePlan);
    FileInfo save(FileInfo fileInfo);
    void deleteById(Integer id);
    FileInfo findByFilePathAndSyncFilePlan(String filePath,SyncFilePlan syncFilePlan);
    int deleteByPathAndPlan(String filePath,Integer id);
}
