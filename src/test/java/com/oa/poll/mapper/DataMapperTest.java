package com.oa.poll.mapper;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PersonalData;
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
    void submitPollRequestIsMappedToPersonalData() {
        String email = "smth@smth.com";
        List<Integer> likedDislikedVeggies = List.of(1, 2, 3);
        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email(email)
                .likedVeggies(likedDislikedVeggies)
                .dislikedVeggies(likedDislikedVeggies)
                .percentage(23)
                .build();
        PersonalData personalDataExpected = PersonalData.builder().email(email).build();
        PersonalData personalDataCreated = dataMapper.createPersonalData(submitPollRequest);
        assertEquals(personalDataExpected, personalDataCreated);
    }

    @Test
    void submitPollRequestAndPersonalDataAreMappedToIndividualEntry() {
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
        PersonalData personalData = PersonalData.builder()
                .id(1)
                .email(email)
                .build();

        IndividualEntry individualEntryExpected = IndividualEntry.builder()
                .likeCount(likedVeggies.size())
                .dislikeCount(dislikedVeggies.size())
                .percentage(percentage)
                .personalData(personalData)
                .build();
        IndividualEntry individualEntryCreated = dataMapper.createIndividualEntry(submitPollRequest, personalData);
        assertEquals(individualEntryExpected, individualEntryCreated);
    }
}