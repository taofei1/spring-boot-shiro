package com.neo.dao;

import com.neo.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskEntityDao extends JpaRepository<TaskEntity,Integer>,QueryByExampleExecutor<TaskEntity> {
}
