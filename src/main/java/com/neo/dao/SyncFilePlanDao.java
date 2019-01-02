package com.neo.dao;
import com.neo.entity.SyncFilePlan;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncFilePlanDao extends JpaRepository<SyncFilePlan,Integer>,QueryByExampleExecutor<SyncFilePlan> {

    @Modifying
    @Query(value="update SyncFilePlan a set a.isConcurrent=:status where a.id=:id")
    int updateIsConcurrent(@Param("id") Integer id, @Param("status")Integer status);

}
