package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PollServiceIT {
    private final PollService testSubject;

    @Autowired
    public PollServiceIT(PollService testSubject) {
        this.testSubject = testSubject;
    }

    @Test
    void testThatFirstSubmissionBecomesStats() {
        List<Integer> likedVeggies = List.of(4, 5, 6);
        List<Integer> dislikedVeggies = List.of(3, 8);
        SubmitPollRequest submitPollRequest =
                SubmitPollRequest.builder()
                        .email("smth@smth.ee")
                        .likedVeggies(likedVeggies)
                        .dislikedVeggies(dislikedVeggies)
                        .percentage(55)
                        .build();
        SubmitPollResponse submitPollResponse = testSubject.handlePollSubmission(submitPollRequest);
        assertEquals(submitPollResponse.averagePercentage(), 55);
        assertEquals(submitPollResponse.mostPopularVeggies(), likedVeggies);
        assertEquals(submitPollResponse.leastPopularVeggies(), dislikedVeggies);
    }
}
