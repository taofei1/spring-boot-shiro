package com.neo.service.impl;

import com.neo.dao.FileInfoDao;
import com.neo.entity.FileInfo;
import com.neo.entity.SyncFilePlan;
import com.neo.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService{
    @Autowired
    private FileInfoDao fileInfoDao;

    public static final String CACHE_KEY   = "fileInfo";
    @Override
    public List<FileInfo> findBySyncFilePlan(SyncFilePlan syncFilePlan) {
        return fileInfoDao.findBySyncFilePlan(syncFilePlan);
    }

    @Override
    @CachePut(value=CACHE_KEY,key = "'file_'+#fileInfo.filePath+'_'+#fileInfo.syncFilePlan.id")
    public FileInfo save(FileInfo fileInfo) {
        return fileInfoDao.save(fileInfo);
    }

    @Override
    public void deleteById(Integer id) {
        fileInfoDao.delete(id);
    }

    @Override
    @Cacheable(value=CACHE_KEY,key = "'file_'+#filePath+'_'+#syncFilePlan.id")
    public FileInfo findByFilePathAndSyncFilePlan ( String filePath, SyncFilePlan syncFilePlan) {
        List<FileInfo> list= fileInfoDao.findByFilePathAndSyncFilePlan(filePath,syncFilePlan);
        if(!list.isEmpty()){
            FileInfo lastest=list.get(0);
            if(list.size()>1) {
                for (int i = 1; i < list.size(); i++) {
                    if (list.get(i).getCreateTime().getTime() > lastest.getCreateTime().getTime()) {
                        lastest = list.get(i);
                    }
                }
            }
            return lastest;
        }
        return null;
    }

    @Override
    public int deleteByPathAndPlan(String filePath, Integer id) {
        return fileInfoDao.deleteByPathAndPlan(filePath,id);
    }


}
