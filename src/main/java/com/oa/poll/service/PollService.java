package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import org.springframework.stereotype.Component;


@Component
public class PollService implements IPollService {

    private final DbService dbService;

    PollService(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public SubmitPollResponse handlePollSubmission(SubmitPollRequest submitPollRequest) {

        dbService.addSubmission(submitPollRequest);

        return SubmitPollResponse.builder()
                .mostPopularVeggies(dbService.findMostPopularVeggies())
                .leastPopularVeggies(dbService.findLeastPopularVeggies())
                .averagePercentage(dbService.findAverageFrequency())
                .build();
    }
}
