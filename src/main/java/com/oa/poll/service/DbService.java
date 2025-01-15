package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;

import java.util.List;

public interface DbService {
    List<Integer> findMostPopularVeggies();

    List<Integer> findLeastPopularVeggies();

    int findAverageFrequency();

    void addSubmission(SubmitPollRequest submitPollRequest);
}
