package com.neo.dao;

import com.neo.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
@Repository
public interface ServerDao extends JpaRepository<Server,Integer> {

}
