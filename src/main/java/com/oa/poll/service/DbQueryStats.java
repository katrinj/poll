package com.oa.poll.service;

import com.oa.poll.dto.PollResults;
import com.oa.poll.dto.SubmitPollResponse;
import com.oa.poll.entity.Veggie;
import com.oa.poll.exceptions.DbQueryException;
import com.oa.poll.mapper.IDataMapper;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.VeggieRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class DbQueryStats implements IDbQueryStats {
    private final VeggieRepo veggieRepo;
    private final IndividualEntryRepo individualEntryRepo;
    private final IDataMapper dataMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbQueryStats.class);

    public DbQueryStats(VeggieRepo veggieRepo, IndividualEntryRepo individualEntryRepo, IDataMapper dataMapper) {
        this.veggieRepo = veggieRepo;
        this.individualEntryRepo = individualEntryRepo;
        this.dataMapper = dataMapper;
    }

    @Override
    public SubmitPollResponse getSubmitPollResponse() {
        return dataMapper.createSubmitPollResponse(getPollResults());
    }

    private PollResults getPollResults() {
        return PollResults.builder()
                .mostPopularVeggies(findTopVeggies(true))
                .leastPopularVeggies(findTopVeggies(false))
                .averagePercentage(findAveragePercentage())
                .build();
    }

    List<Integer> findTopVeggies(boolean liked) {
        List<Veggie> orderedVeggies = getVeggiesOrderedByScore(liked);
        List<Integer> topVeggieIndexes = new ArrayList<>();
        Function<Veggie, Long> getVeggieScore = getVeggieScoreFunction(liked);
        long highestValue = getVeggieScore.apply(orderedVeggies.get(0));
        if (highestValue != 0) {
            addVeggiesWithHighestScore(topVeggieIndexes, orderedVeggies, highestValue, getVeggieScore);
        }
        return topVeggieIndexes;
    }

    private Function<Veggie, Long> getVeggieScoreFunction(boolean liked) {
        return liked? Veggie::getLikeCount : Veggie::getDislikeCount;
    }

    private Supplier<List<Veggie>> getVeggiesOrderedByScoreSupplier(boolean liked) {
        return liked? veggieRepo::findAllByOrderByLikeCountDesc : veggieRepo::findAllByOrderByDislikeCountDesc;
    }

    private List<Veggie> getVeggiesOrderedByScore(boolean liked) {
        Supplier<List<Veggie>> getVeggiesOrderedByScoreDescending = getVeggiesOrderedByScoreSupplier(liked);
        List<Veggie> orderedVeggies;
        try {
            orderedVeggies = getVeggiesOrderedByScoreDescending.get();
        } catch (DataAccessException ex) {
            throw new DbQueryException("Could not retrieve most popular veggie list from db.", ex);
        }
        return orderedVeggies;
    }

    private void addVeggiesWithHighestScore(List<Integer> topVeggieIndexes, List<Veggie> orderedVeggies,
                                            long highestValue, Function<Veggie, Long> getVeggieScore) {
        for (Veggie veggie : orderedVeggies) {
            long veggieScore = getVeggieScore.apply(veggie);
            if (veggieScore < highestValue){
                break;
            } else {
                topVeggieIndexes.add(veggie.getId());
            }
        }
    }

    int findAveragePercentage() {
        int average;
        try {
            average = individualEntryRepo.getAveragePercentage();
        } catch (DataAccessException ex) {
            throw new DbQueryException("Could not retrieve average percentage from db.", ex);
        }
        return average;
    }
}
