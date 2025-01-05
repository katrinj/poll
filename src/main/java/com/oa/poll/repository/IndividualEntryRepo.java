package com.oa.poll.repository;

import com.oa.poll.entity.IndividualEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualEntryRepo extends JpaRepository<IndividualEntry, Long> {

}
