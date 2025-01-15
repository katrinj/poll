package com.oa.poll.mapper;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PollSubmission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DataMapperTest {

    @InjectMocks
    private DataMapper dataMapper;

    @Test
    void pollSubmissionRequestIsMappedToPollSubmission() {
        String email = "smth@smth.com";
        List<Integer> likedDislikedVeggies = List.of(1, 2, 3);
        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email(email)
                .likedVeggies(likedDislikedVeggies)
                .dislikedVeggies(likedDislikedVeggies)
                .percentage(23)
                .build();
        PollSubmission pollSubmissionExpected = PollSubmission.builder().email(email).build();
        PollSubmission pollSubmissionCreated = dataMapper.createPollSubmission(submitPollRequest);
        assertEquals(pollSubmissionExpected, pollSubmissionCreated);
    }

    @Test
    void pollSubmissionRequestAndPollSubmissionAreMappedToIndividualEntry() {
        String email = "smth@smth.com";
        List<Integer> likedVeggies = List.of(3, 4, 7, 9, 11);
        List<Integer> dislikedVeggies = List.of(2, 8);
        int percentage = 23;

        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email(email)
                .likedVeggies(likedVeggies)
                .dislikedVeggies(dislikedVeggies)
                .percentage(percentage)
                .build();
        PollSubmission pollSubmission = PollSubmission.builder()
                .id(1)
                .email(email)
                .build();

        IndividualEntry individualEntryExpected = IndividualEntry.builder()
                .likeCount(likedVeggies.size())
                .dislikeCount(dislikedVeggies.size())
                .percentage(percentage)
                .pollSubmission(pollSubmission)
                .build();
        IndividualEntry individualEntryCreated = dataMapper.createIndividualEntry(submitPollRequest, pollSubmission);
        assertEquals(individualEntryExpected, individualEntryCreated);
    }
}