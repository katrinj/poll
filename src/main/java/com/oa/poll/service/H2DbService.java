package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
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

import static com.oa.poll.dataconfig.PollDataConfig.INITIAL_DATA;
import static com.oa.poll.dataconfig.PollDataConfig.PERCENTAGE_MIN;
import static com.oa.poll.dataconfig.PollDataConfig.PERCENTAGE_MAX;

@Component
public class H2DbService implements DbService {
    private final VeggieRepo veggieRepo;
    private final PercentageStatsRepo percentageStatsRepo;
    private final PollSubmissionRepo pollSubmissionRepo;
    private final IndividualEntryRepo individualEntryRepo;
    private final IDataMapper dataMapper;



    H2DbService(VeggieRepo veggieRepo, PercentageStatsRepo percentageStatsRepo, PollSubmissionRepo pollSubmissionRepo
            , IndividualEntryRepo individualEntryRepo, IDataMapper dataMapper) {
        this.veggieRepo = veggieRepo;
        this.percentageStatsRepo = percentageStatsRepo;
        this.pollSubmissionRepo = pollSubmissionRepo;
        this.individualEntryRepo = individualEntryRepo;
        this.dataMapper = dataMapper;

        initDB();
    }

    private void initDB() {
        INITIAL_DATA.forEach(veggie -> addVeggie(Veggie.builder().name(veggie).build()));
        addMinMax(PercentageStats.builder().min(PERCENTAGE_MIN).max(PERCENTAGE_MAX).build());
    }

    private void addVeggie(Veggie vegetable) {
        veggieRepo.save(vegetable);
    }

    private void addMinMax(PercentageStats percentageStats) {
        percentageStatsRepo.save(percentageStats);
    }

    @Override
    public List<String> findMostPopularVeggies() {
        List<Veggie> orderedVeggies = veggieRepo.findAllByOrderByLikeCountDesc();
        List<String> topVeggies = new ArrayList<>();
        long highestCount = 0;
        for (Veggie veggie : orderedVeggies) {
            if (highestCount > veggie.getLikeCount()) {
                break;
            } else if (veggie.getLikeCount() > 0) {
                highestCount = veggie.getLikeCount();
                topVeggies.add(veggie.getName());
            }
        }
        return topVeggies;
    }

    @Override
    public List<String> findLeastPopularVeggies() {
        List<Veggie> orderedVeggies = veggieRepo.findAllByOrderByDislikeCountDesc();
        List<String> topVeggies = new ArrayList<>();
        long highestCount = 0;
        for (Veggie veggie : orderedVeggies) {
            if (highestCount > veggie.getDislikeCount()) {
                break;
            } else if (veggie.getDislikeCount() > 0) {
                highestCount = veggie.getDislikeCount();
                topVeggies.add(veggie.getName());
            }
        }
        return topVeggies;
    }

    @Override
    public int findAverageFrequency() {
        return percentageStatsRepo.findAll().get(0).getAverage();
    }

    @Transactional
    private void updateLikedVeggies(List<String> veggies) {
        veggies.forEach(v -> {
                Veggie existingEntry = veggieRepo.findByName(v);
                existingEntry.setLikeCount(existingEntry.getLikeCount() + 1);
                veggieRepo.save(existingEntry);
        });
    }

    @Transactional
    private void updateDislikedVeggies(List<String> veggies) {
        veggies.forEach(v -> {
            Veggie existingEntry = veggieRepo.findByName(v);
            existingEntry.setDislikeCount(existingEntry.getDislikeCount() + 1);
            veggieRepo.save(existingEntry);
        });
    }

    @Transactional
    private void updatePercentageStats(int percentage) {
        PercentageStats percentageStats = percentageStatsRepo.findAll().get(0);
        percentageStats.setEntryCount(percentageStats.getEntryCount() + 1);
        percentageStats.setRunningTotal(percentageStats.getRunningTotal() + percentage);
        percentageStats.setAverage(calculateAverage(percentageStats.getRunningTotal(),
                percentageStats.getEntryCount()));
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
        PollSubmission pollSubmission = pollSubmissionRepo.save(dataMapper.createPollSubmission(submitPollRequest));
        individualEntryRepo.save(dataMapper.createIndividualEntry(submitPollRequest, pollSubmission));

        updateLikedVeggies(submitPollRequest.getLikedVeggies());
        updateDislikedVeggies(submitPollRequest.getDislikedVeggies());
        updatePercentageStats(submitPollRequest.getPercentage());
    }
}
