package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private DbService dbService;

    @InjectMocks
    private PollService pollService;

    @Test
    void responseIsCreated() {
        List<Integer> likedVeggies = List.of(2,4,5);
        List<Integer> dislikedVeggies = List.of(1,7);
        int percentage = 54;
        SubmitPollRequest submitPollRequest =
                SubmitPollRequest.builder()
                        .email("smth@smth.com")
                        .likedVeggies(likedVeggies)
                        .dislikedVeggies(dislikedVeggies)
                        .percentage(percentage)
                        .build();

        willDoNothing().given(dbService).addSubmission(submitPollRequest);

        given(dbService.findMostPopularVeggies()).willReturn(likedVeggies);
        given(dbService.findLeastPopularVeggies()).willReturn(dislikedVeggies);
        given(dbService.findAverageFrequency()).willReturn(percentage);

        SubmitPollResponse submitPollResponseExpected =
                SubmitPollResponse.builder()
                        .mostPopularVeggies(likedVeggies)
                        .leastPopularVeggies(dislikedVeggies)
                        .averagePercentage(percentage).build();

        SubmitPollResponse submitPollResponse = pollService.handlePollSubmission(submitPollRequest);
        assertEquals(submitPollResponseExpected, submitPollResponse);
    }
}
