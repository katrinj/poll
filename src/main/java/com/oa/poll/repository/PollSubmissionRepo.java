package com.oa.poll.repository;

import com.oa.poll.entity.PollSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollSubmissionRepo extends JpaRepository<PollSubmission, Long> {
}
