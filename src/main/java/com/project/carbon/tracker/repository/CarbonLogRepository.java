package com.project.carbon.tracker.repository;

import com.project.carbon.tracker.model.CarbonLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface CarbonLogRepository extends JpaRepository<CarbonLog,Long> {

    List<CarbonLog> findByUserId(Long userId);
}
