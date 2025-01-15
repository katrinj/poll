package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PercentageStats;
import com.oa.poll.entity.PollSubmission;
import com.oa.poll.entity.Veggie;

import com.oa.poll.mapper.IDataMapper;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PercentageStatsRepo;
import com.oa.poll.repository.PollSubmissionRepo;
import com.oa.poll.repository.VeggieRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.oa.poll.dataconfig.PollDataConfig.*;

@Component
public class H2DbService implements DbService {
    private final VeggieRepo veggieRepo;
    private final PercentageStatsRepo percentageStatsRepo;
    private final PollSubmissionRepo pollSubmissionRepo;
    private final IndividualEntryRepo individualEntryRepo;
    private final IDataMapper dataMapper;



    H2DbService(VeggieRepo veggieRepo, PercentageStatsRepo percentageStatsRepo, PollSubmissionRepo pollSubmissionRepo,
                IndividualEntryRepo individualEntryRepo, IDataMapper dataMapper) {
        this.veggieRepo = veggieRepo;
        this.percentageStatsRepo = percentageStatsRepo;
        this.pollSubmissionRepo = pollSubmissionRepo;
        this.individualEntryRepo = individualEntryRepo;
        this.dataMapper = dataMapper;

        initDB();
    }

    void checkDBConfig(List<String> veggiesNamesEt, List<String> veggiesNamesEn) {
        if (veggiesNamesEt.size() != veggiesNamesEn.size()) {
            throw new IllegalStateException("Illegal db configuration: veggie lists should be of same size");
        }
    }

    private void initDB() {
        checkDBConfig(VEGGIE_NAMES_ET, VEGGIE_NAMES_EN);

        for (int i = 0; i < VEGGIE_NAMES_ET.size(); i++) {
            addVeggie(Veggie.builder().name_et(VEGGIE_NAMES_ET.get(i)).name_en(VEGGIE_NAMES_EN.get(i)).build());
        }

        addMinMax(PercentageStats.builder().min(PERCENTAGE_MIN).max(PERCENTAGE_MAX).build());
    }

    Veggie addVeggie(Veggie vegetable) {
        return veggieRepo.save(vegetable);
    }

    PercentageStats addMinMax(PercentageStats percentageStats) {
        return percentageStatsRepo.save(percentageStats);
    }

    @Override
    public List<Integer> findMostPopularVeggies() {
        List<Veggie> orderedVeggies = veggieRepo.findAllByOrderByLikeCountDesc();
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
        List<Veggie> orderedVeggies = veggieRepo.findAllByOrderByDislikeCountDesc();
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
        return percentageStatsRepo.findAll().get(0).getAverage();
    }

    @Transactional
    private void updateLikedVeggies(List<Integer> veggies) {
        veggies.forEach(this::updateLikedVeggie);
    }

    Veggie updateLikedVeggie(int index) {
        Veggie veggie = veggieRepo.findById(index);
        veggie.setLikeCount(veggie.getLikeCount() + 1);
        return veggie;
    }

    @Transactional
    private void updateDislikedVeggies(List<Integer> veggies) {
        veggies.forEach(this::updateDislikedVeggie);
    }

    Veggie updateDislikedVeggie(int index) {
        Veggie veggie = veggieRepo.findById(index);
        veggie.setDislikeCount(veggie.getDislikeCount() + 1);
        return veggie;
    }

    @Transactional
    PercentageStats updatePercentageStats(int percentage) {
        PercentageStats percentageStats = percentageStatsRepo.findAll().get(0);
        percentageStats.setEntryCount(percentageStats.getEntryCount() + 1);
        percentageStats.setRunningTotal(percentageStats.getRunningTotal() + percentage);
        percentageStats.setAverage(calculateAverage(percentageStats.getRunningTotal(),
                percentageStats.getEntryCount()));
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
        PollSubmission pollSubmission = addPollSubmission(dataMapper.createPollSubmission(submitPollRequest));
        addIndvidualEntry(dataMapper.createIndividualEntry(submitPollRequest, pollSubmission));

        updateLikedVeggies(submitPollRequest.getLikedVeggies());
        updateDislikedVeggies(submitPollRequest.getDislikedVeggies());
        updatePercentageStats(submitPollRequest.getPercentage());
    }

    PollSubmission addPollSubmission(PollSubmission pollSubmission) {
        return pollSubmissionRepo.save(pollSubmission);
    }

    IndividualEntry addIndvidualEntry (IndividualEntry individualEntry) {
        return individualEntryRepo.save(individualEntry);
    }
}
