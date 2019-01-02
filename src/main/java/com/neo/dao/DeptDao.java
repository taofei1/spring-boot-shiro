package com.neo.dao;

import com.neo.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DeptDao extends JpaRepository<Dept,Integer>,QueryByExampleExecutor<Dept> {
 List<Dept> findByState(Integer state);
 }
