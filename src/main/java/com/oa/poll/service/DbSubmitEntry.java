package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PersonalData;
import com.oa.poll.entity.Veggie;

import com.oa.poll.exceptions.DbUpdateException;
import com.oa.poll.exceptions.DoubleEntryException;
import com.oa.poll.mapper.IDataMapper;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PersonalDataRepo;
import com.oa.poll.repository.VeggieRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DbSubmitEntry implements IDbSubmitEntry {
    private final VeggieRepo veggieRepo;
    private final PersonalDataRepo personalDataRepo;
    private final IndividualEntryRepo individualEntryRepo;
    private final IDataMapper dataMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbSubmitEntry.class);

    DbSubmitEntry(VeggieRepo veggieRepo, PersonalDataRepo personalDataRepo,
                  IndividualEntryRepo individualEntryRepo, IDataMapper dataMapper) {
        this.veggieRepo = veggieRepo;
        this.personalDataRepo = personalDataRepo;
        this.individualEntryRepo = individualEntryRepo;
        this.dataMapper = dataMapper;
    }

    @Override
    @Transactional
    public void addSubmission(SubmitPollRequest submitPollRequest) {
        PersonalData personalData = addPersonalData(dataMapper.createPersonalData(submitPollRequest));
        addIndividualEntry(dataMapper.createIndividualEntry(submitPollRequest, personalData));

        updateVeggiePreferences(submitPollRequest.getLikedVeggies(), true);
        updateVeggiePreferences(submitPollRequest.getDislikedVeggies(), false);
    }

    PersonalData addPersonalData(PersonalData personalData) {
        PersonalData newEntry;
        try {
            newEntry = personalDataRepo.save(personalData);
        } catch (DataIntegrityViolationException ex) {
            throw new DoubleEntryException("Email address already used in a previous submission. Updates to " +
                    "submissions not allowed.", ex);
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not add PersonalData to db.", ex);
        }
        return newEntry;
    }

    void addIndividualEntry (IndividualEntry individualEntry) {
        try {
            individualEntryRepo.save(individualEntry);
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not add IndividualEntry to db.", ex);
        }
    }

    void updateVeggiePreferences(List<Integer> veggies, boolean liked) {
        if (veggies.isEmpty()) {
            return;
        }
        try {
            veggies.forEach(veggieId -> {
                Veggie veggie = veggieRepo.findById(veggieId);
                if (liked) {
                    veggie.setLikeCount(veggie.getLikeCount() + 1);
                } else {
                    veggie.setDislikeCount(veggie.getDislikeCount() + 1);
                }
            });
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not update veggies.", ex);
        }
    }
}
