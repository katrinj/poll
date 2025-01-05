package com.oa.poll.repository;

import com.oa.poll.entity.PercentageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PercentageStatsRepo extends JpaRepository<PercentageStats, Long> {
}
