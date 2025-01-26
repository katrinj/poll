package com.oa.poll.repository;

import com.oa.poll.entity.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDataRepo extends JpaRepository<PersonalData, Long> {
}
