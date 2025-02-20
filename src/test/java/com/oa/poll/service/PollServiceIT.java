package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import com.oa.poll.entity.Veggie;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PersonalDataRepo;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.BeforeEach;
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
    IndividualEntryRepo individualEntryRepo;
    @Autowired
    PersonalDataRepo personalDataRepo;
    @Autowired
    VeggieRepo veggieRepo;

    @Autowired
    public PollServiceIT(PollService testSubject) {
        this.testSubject = testSubject;
    }

    @BeforeEach
    void init() {
        individualEntryRepo.deleteAll();
        personalDataRepo.deleteAll();

        List<Veggie> veggies = veggieRepo.findAll();
        veggies.forEach(v -> {
            v.setLikeCount(0L);
            v.setDislikeCount(0L);
        });
        veggieRepo.saveAll(veggies);
    }

    @Test
    void testThatFirstSubmissionBecomesStats() {
        List<Integer> likedVeggies = List.of(4, 5, 6);
        List<Integer> dislikedVeggies = List.of(3, 8);
        int percentage = 55;
        SubmitPollRequest submitPollRequest =
                SubmitPollRequest.builder()
                        .email("smth@smth.ee")
                        .likedVeggies(likedVeggies)
                        .dislikedVeggies(dislikedVeggies)
                        .percentage(percentage)
                        .build();
        SubmitPollResponse submitPollResponse = testSubject.handlePollSubmission(submitPollRequest);
        assertEquals(percentage, submitPollResponse.averagePercentage());
        assertEquals(likedVeggies, submitPollResponse.mostPopularVeggies());
        assertEquals(dislikedVeggies, submitPollResponse.leastPopularVeggies());
    }
}
