package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PercentageStats;
import com.oa.poll.entity.PersonalData;
import com.oa.poll.entity.Veggie;

import com.oa.poll.exceptions.DbConfigException;
import com.oa.poll.exceptions.DbUpdateException;
import com.oa.poll.exceptions.DoubleEntryException;
import com.oa.poll.mapper.IDataMapper;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PercentageStatsRepo;
import com.oa.poll.repository.PersonalDataRepo;
import com.oa.poll.repository.VeggieRepo;
import com.oa.poll.validator.LoadedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.oa.poll.dataconfig.PollDataConfig.*;

@Component
public class H2DbService implements DbService {
    private final VeggieRepo veggieRepo;
    private final PercentageStatsRepo percentageStatsRepo;
    private final PersonalDataRepo personalDataRepo;
    private final IndividualEntryRepo individualEntryRepo;
    private final IDataMapper dataMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(H2DbService.class);

    H2DbService(VeggieRepo veggieRepo, PercentageStatsRepo percentageStatsRepo, PersonalDataRepo personalDataRepo,
                IndividualEntryRepo individualEntryRepo, IDataMapper dataMapper) {
        this.veggieRepo = veggieRepo;
        this.percentageStatsRepo = percentageStatsRepo;
        this.personalDataRepo = personalDataRepo;
        this.individualEntryRepo = individualEntryRepo;
        this.dataMapper = dataMapper;

        try {
            LoadedData.VEGGIE_KEYS = initDB();
        } catch (DbConfigException ex) {
            LOGGER.error("Db initialisation failed. Check the db configuration.", ex);
        }
        LoadedData.checkLoadedData();
    }

    void checkDBConfig(List<String> veggiesNamesEt, List<String> veggiesNamesEn) {
        if (veggiesNamesEt.size() != veggiesNamesEn.size()) {
            throw new IllegalStateException("Illegal db configuration: veggie lists should be of same size");
        }
    }

    private List<Integer> initDB() {
        checkDBConfig(VEGGIE_NAMES_ET, VEGGIE_NAMES_EN);

        try {
            for (int i = 0; i < VEGGIE_NAMES_ET.size(); i++) {
                addVeggie(Veggie.builder().name_et(VEGGIE_NAMES_ET.get(i)).name_en(VEGGIE_NAMES_EN.get(i)).build());
            }
        } catch (DataAccessException ex) {
            throw new DbConfigException("Could not initialise veggie table.", ex);
        }

        try {
            addMinMax(PercentageStats.builder().min(PERCENTAGE_MIN).max(PERCENTAGE_MAX).build());
        } catch (DataAccessException ex) {
            throw new DbConfigException("Could not initialise percentage limits.", ex);
        }

        List<Integer> veggieKeys;
        try {
            veggieKeys = getAllVeggieKeys();
        } catch (DataAccessException ex) {
            throw new DbConfigException("Could not load veggie keys from db.", ex);
        }
        return veggieKeys;
    }

    Veggie addVeggie(Veggie vegetable) {
        return veggieRepo.save(vegetable);
    }

    PercentageStats addMinMax(PercentageStats percentageStats) {
        return percentageStatsRepo.save(percentageStats);
    }

    List<Integer> getAllVeggieKeys() {
        return veggieRepo.findAll().stream().map(Veggie::getId).toList();
    }

    @Override
    public List<Integer> findMostPopularVeggies() {
        List<Veggie> orderedVeggies;
        try {
            orderedVeggies = veggieRepo.findAllByOrderByLikeCountDesc();
        } catch (DataAccessException ex) {
            LOGGER.error("Could not retrieve most popular veggie list from db.");
            return new ArrayList<>();
        }
        List<Integer> topVeggies = new ArrayList<>();
        long highestCount = 0;
        for (Veggie veggie : orderedVeggies) {
            if (highestCount > veggie.getLikeCount()) {
                break;
            } else if (veggie.getLikeCount() > 0) {
                highestCount = veggie.getLikeCount();
                topVeggies.add(veggie.getId());
            }
        }
        return topVeggies;
    }

    @Override
    public List<Integer> findLeastPopularVeggies() {
        List<Veggie> orderedVeggies;
        try {
            orderedVeggies = veggieRepo.findAllByOrderByDislikeCountDesc();
        } catch (DataAccessException ex) {
            LOGGER.error("Could not retrieve least popular veggie list from db.");
            return new ArrayList<>();
        }
        List<Integer> topVeggies = new ArrayList<>();
        long highestCount = 0;
        for (Veggie veggie : orderedVeggies) {
            if (highestCount > veggie.getDislikeCount()) {
                break;
            } else if (veggie.getDislikeCount() > 0) {
                highestCount = veggie.getDislikeCount();
                topVeggies.add(veggie.getId());
            }
        }
        return topVeggies;
    }

    @Override
    public int findAverageFrequency() {
        int average;
        try {
            average = percentageStatsRepo.findAll().get(0).getAverage();
        } catch (DataAccessException ex) {
            LOGGER.error("Could not retrieve average percentage from db.");
            return -1;
        }
        return average;
    }

    private void updateLikedVeggies(List<Integer> veggies) {
        try {
            veggies.forEach(this::updateLikedVeggie);
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not update liked veggies.", ex);
        }
    }

    Veggie updateLikedVeggie(int index) {
        Veggie veggie = veggieRepo.findById(index);
        veggie.setLikeCount(veggie.getLikeCount() + 1);
        return veggie;
    }

    private void updateDislikedVeggies(List<Integer> veggies) {
        try {
            veggies.forEach(this::updateDislikedVeggie);
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not update disliked veggies.", ex);
        }
    }

    Veggie updateDislikedVeggie(int index) {
        Veggie veggie = veggieRepo.findById(index);
        veggie.setDislikeCount(veggie.getDislikeCount() + 1);
        return veggie;
    }

    PercentageStats updatePercentageStats(int percentage) {
        PercentageStats percentageStats;
        try {
            percentageStats = percentageStatsRepo.findAll().get(0);
            percentageStats.setEntryCount(percentageStats.getEntryCount() + 1);
            percentageStats.setRunningTotal(percentageStats.getRunningTotal() + percentage);
            percentageStats.setAverage(calculateAverage(percentageStats.getRunningTotal(),
                    percentageStats.getEntryCount()));
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not update percentageStats.", ex);
        }
        return percentageStats;
    }

    private int calculateAverage(long runningTotal, long entryCount) {
        if (entryCount == 0) {
            return 0;
        }
        return (int) (runningTotal / entryCount);
    }

    @Override
    @Transactional
    public void addSubmission(SubmitPollRequest submitPollRequest) {
        PersonalData personalData = addPersonalData(dataMapper.createPersonalData(submitPollRequest));
        addIndvidualEntry(dataMapper.createIndividualEntry(submitPollRequest, personalData));

        updateLikedVeggies(submitPollRequest.getLikedVeggies());
        updateDislikedVeggies(submitPollRequest.getDislikedVeggies());
        updatePercentageStats(submitPollRequest.getPercentage());
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

    IndividualEntry addIndvidualEntry (IndividualEntry individualEntry) {
        IndividualEntry newEntry;
        try {
            newEntry = individualEntryRepo.save(individualEntry);
        } catch (DataAccessException ex) {
            throw new DbUpdateException("Could not add individualEntry to db.", ex);
        }
        return newEntry;
    }
}
