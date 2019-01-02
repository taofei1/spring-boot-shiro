package com.neo.dao;

import com.neo.entity.FileInfo;
import com.neo.entity.SyncFilePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
@Repository
public interface FileInfoDao extends JpaRepository<FileInfo,Integer>,QueryByExampleExecutor<FileInfo> {
    List<FileInfo> findBySyncFilePlan(SyncFilePlan syncFilePlan);
    List<FileInfo> findByFilePathAndSyncFilePlan(String filePath,SyncFilePlan syncFilePlan);
    @Modifying
    @Transactional
    @Query(value = "delete from file_info where file_path=?1 and sync_file_plan_id=?2",nativeQuery = true)
    int deleteByPathAndPlan(String filePath,Integer id);
}
