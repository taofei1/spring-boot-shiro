package com.neo.dao;

import com.neo.entity.DateInterval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateIntervalDao extends JpaRepository<DateInterval,Integer> {
    List<DateInterval> findByStatus(int i);
}
