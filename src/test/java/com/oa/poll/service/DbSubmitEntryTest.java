package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PersonalData;
import com.oa.poll.entity.Veggie;
import com.oa.poll.exceptions.DbUpdateException;
import com.oa.poll.exceptions.DoubleEntryException;
import com.oa.poll.mapper.IDataMapper;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PersonalDataRepo;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DbSubmitEntryTest {

    @Mock
    private VeggieRepo veggieRepo;
    @Mock
    private IndividualEntryRepo individualEntryRepo;
    @Mock
    private PersonalDataRepo personalDataRepo;
    @Mock
    private IDataMapper dataMapper;

    @InjectMocks
    private DbSubmitEntry dbSubmitEntry;

    @Test
    void personalDataIsAdded() {
        PersonalData personalData = PersonalData.builder().email("smth@smth.com").build();
        PersonalData personalDataExpected = PersonalData.builder().id(1).email("smth@smth.com").build();
        given(personalDataRepo.save(personalData)).willReturn(personalDataExpected);
        PersonalData personalDataCreated = dbSubmitEntry.addPersonalData(personalData);
        assertEquals(personalDataExpected, personalDataCreated);
    }

    @Test
    void addingPersonalDataFailsOnDuplicateEntry() {
        PersonalData personalData = PersonalData.builder().email("smth@smth.com").build();
        given(personalDataRepo.save(personalData)).willThrow(DataIntegrityViolationException.class);
        String exMessage =
                assertThrows(DoubleEntryException.class, () -> dbSubmitEntry.addPersonalData(personalData)).getMessage();
        assertEquals("Email address already used in a previous submission. Updates to submissions not allowed.",
                exMessage);
        verify(personalDataRepo, times(1)).save(personalData);
    }
    
    @Test
    void addingPersonalDataFailsOnOtherDbError() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        PersonalData personalData = PersonalData.builder().email("smth@smth.com").build();
        given(personalDataRepo.save(personalData)).willThrow(ex);
        String exMessage =
                assertThrows(DbUpdateException.class, () -> dbSubmitEntry.addPersonalData(personalData)).getMessage();
        assertEquals("Could not add PersonalData to db.", exMessage);
        verify(personalDataRepo, times(1)).save(personalData);
    }

    @Test
    void individualEntryIsAdded() {
        PersonalData personalData = PersonalData.builder().id(1).email("smth@smth.com").build();
        IndividualEntry individualEntry =
                IndividualEntry.builder()
                        .personalData(personalData)
                        .percentage(42)
                        .likeCount(5)
                        .dislikeCount(0)
                        .build();
        IndividualEntry individualEntryExpected =
                IndividualEntry.builder()
                        .id(1)
                        .personalData(personalData)
                        .percentage(42)
                        .likeCount(5)
                        .dislikeCount(0)
                        .build();

        given(individualEntryRepo.save(individualEntry)).willReturn(individualEntryExpected);
        dbSubmitEntry.addIndividualEntry(individualEntry);
        verify(individualEntryRepo, times(1)).save(individualEntry);
    }

    @Test
    void addingIndividualEntryFails() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        PersonalData personalData = PersonalData.builder().id(1).email("smth@smth.com").build();
        IndividualEntry individualEntry =
                IndividualEntry.builder()
                        .personalData(personalData)
                        .percentage(42)
                        .likeCount(5)
                        .dislikeCount(0)
                        .build();
        given(individualEntryRepo.save(individualEntry)).willThrow(ex);
        String exMessage =
                assertThrows(DbUpdateException.class, () -> dbSubmitEntry.addIndividualEntry(individualEntry)).getMessage();
        assertEquals("Could not add IndividualEntry to db.", exMessage);
        verify(individualEntryRepo, times(1)).save(individualEntry);
    }

    @Test
    void submissionIsAdded() {
        List<Integer> likedVeggies = List.of(2,3,7);
        List<Integer> dislikedVeggies = List.of(4,5);
        int percentage = 48;
        String email = "some@email.com";
        Veggie veggie1 =
                Veggie.builder().id(likedVeggies.get(0)).name_et("Tomat").name_en("Tomato").likeCount(1).build();
        Veggie veggie2 =
                Veggie.builder().id(likedVeggies.get(1)).name_et("Redis").name_en("Radish").likeCount(2).build();
        Veggie veggie3 =
                Veggie.builder().id(likedVeggies.get(2)).name_et("Kurk").name_en("Cucumber").likeCount(4).build();
        Veggie veggie4 =
                Veggie.builder().id(dislikedVeggies.get(0)).name_et("Murulauk").name_en("Chive").likeCount(3).build();
        Veggie veggie5 =
                Veggie.builder().id(dislikedVeggies.get(1)).name_et("Fenkol").name_en("Fennel").likeCount(2).build();

        PersonalData personalData = PersonalData.builder().email(email).build();
        PersonalData personalDataFromDb = PersonalData.builder().id(1).email(email).build();

        IndividualEntry individualEntry =
                IndividualEntry.builder()
                        .personalData(personalDataFromDb)
                        .percentage(percentage)
                        .likeCount(likedVeggies.size())
                        .dislikeCount(dislikedVeggies.size())
                        .build();
        IndividualEntry individualEntryFromDb =
                IndividualEntry.builder()
                        .personalData(personalDataFromDb)
                        .percentage(percentage)
                        .likeCount(likedVeggies.size())
                        .dislikeCount(dislikedVeggies.size())
                        .build();

        SubmitPollRequest submitPollRequest =
                SubmitPollRequest.builder()
                        .email(email)
                        .likedVeggies(likedVeggies)
                        .dislikedVeggies(dislikedVeggies)
                        .percentage(percentage)
                        .build();

        given(dataMapper.createPersonalData(submitPollRequest)).willReturn(personalData);
        given(personalDataRepo.save(personalData)).willReturn(personalDataFromDb);
        given(dataMapper.createIndividualEntry(submitPollRequest, personalDataFromDb)).willReturn(individualEntry);
        given(individualEntryRepo.save(individualEntry)).willReturn(individualEntryFromDb);

        given(veggieRepo.findById(likedVeggies.get(0))).willReturn(veggie1);
        given(veggieRepo.findById(likedVeggies.get(1))).willReturn(veggie2);
        given(veggieRepo.findById(likedVeggies.get(2))).willReturn(veggie3);
        given(veggieRepo.findById(dislikedVeggies.get(0))).willReturn(veggie4);
        given(veggieRepo.findById(dislikedVeggies.get(1))).willReturn(veggie5);

        dbSubmitEntry.addSubmission(submitPollRequest);
        verify(dataMapper, times(1)).createPersonalData(submitPollRequest);
        verify(personalDataRepo, times(1)).save(personalData);
        verify(dataMapper, times(1)).createIndividualEntry(submitPollRequest, personalDataFromDb);
        verify(individualEntryRepo, times(1)).save(individualEntry);

        verify(veggieRepo, times(5)).findById(anyInt());
        verify(veggieRepo, times(1)).findById(likedVeggies.get(0));
        verify(veggieRepo, times(1)).findById(likedVeggies.get(1));
        verify(veggieRepo, times(1)).findById(likedVeggies.get(2));
        verify(veggieRepo, times(1)).findById(dislikedVeggies.get(0));
        verify(veggieRepo, times(1)).findById(dislikedVeggies.get(1));
    }

    @Test
    void likedVeggieEmptyListIsIgnored() {
        dbSubmitEntry.updateVeggiePreferences(new ArrayList<Integer>(),true);
        verify(veggieRepo, never()).findById(anyInt());
    }

    @Test
    void dislikedVeggieEmptyListIsIgnored() {
        dbSubmitEntry.updateVeggiePreferences(new ArrayList<Integer>(),false);
        verify(veggieRepo, never()).findById(anyInt());
    }

    @Test
    void likedVeggieIsUpdated() {
        List<Integer> likedVeggieIndexes = List.of(3);
        Veggie veggie = Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").likeCount(4).build();
        given(veggieRepo.findById(likedVeggieIndexes.get(0))).willReturn(veggie);
        dbSubmitEntry.updateVeggiePreferences(likedVeggieIndexes,true);
        verify(veggieRepo, times(1)).findById(likedVeggieIndexes.get(0));
    }

    @Test
    void dislikedVeggieIsUpdated() {
        List<Integer> likedVeggieIndexes = List.of(2);
        Veggie veggie = Veggie.builder().id(2).name_et("Redis").name_en("Radish").likeCount(5).build();
        given(veggieRepo.findById(likedVeggieIndexes.get(0))).willReturn(veggie);
        dbSubmitEntry.updateVeggiePreferences(likedVeggieIndexes,true);
        verify(veggieRepo, times(1)).findById(likedVeggieIndexes.get(0));
    }

    @Test
    void likedVeggiesAreUpdated() {
        veggiePreferencesAreUpdated(true);
    }

    @Test
    void dislikedVeggiesAreUpdated() {
        veggiePreferencesAreUpdated(false);
    }

    private void veggiePreferencesAreUpdated(boolean liked) {
        List<Integer> veggieIndexes = List.of(1, 3, 6);
        Veggie veggie1 = Veggie.builder().id(1).name_et("Tomat").name_en("Tomato").likeCount(4).build();
        Veggie veggie2 = Veggie.builder().id(3).name_et("Redis").name_en("Radish").likeCount(4).build();
        Veggie veggie3 = Veggie.builder().id(6).name_et("Kurk").name_en("Cucumber").likeCount(4).build();
        given(veggieRepo.findById(veggieIndexes.get(0))).willReturn(veggie1);
        given(veggieRepo.findById(veggieIndexes.get(1))).willReturn(veggie2);
        given(veggieRepo.findById(veggieIndexes.get(2))).willReturn(veggie3);
        dbSubmitEntry.updateVeggiePreferences(veggieIndexes, liked);
        verify(veggieRepo, times(3)).findById(anyInt());
        verify(veggieRepo, times(1)).findById(veggieIndexes.get(0));
        verify(veggieRepo, times(1)).findById(veggieIndexes.get(1));
        verify(veggieRepo, times(1)).findById(veggieIndexes.get(2));
    }

    @Test
    void updatingVeggiePreferenceFails() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        given(veggieRepo.findById(anyInt())).willThrow(ex);
        String exMessage =
                assertThrows(DbUpdateException.class,
                        () -> dbSubmitEntry.updateVeggiePreferences(List.of(3, 6), anyBoolean())).getMessage();
        assertEquals("Could not update veggies.", exMessage);
        verify(veggieRepo, times(1)).findById(anyInt());
    }

}