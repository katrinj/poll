package com.oa.poll.service;

import com.oa.poll.entity.Veggie;
import com.oa.poll.exceptions.DbQueryException;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class DbQueryStatsTest {
    @Mock
    private VeggieRepo veggieRepo;
    @Mock
    private IndividualEntryRepo individualEntryRepo;

    @InjectMocks
    private DbQueryStats dbQueryStats;

    @Test
    void noMostPopularVeggiesReturned() {
        Veggie veggie1 = Veggie.builder().id(2).name_et("Tomat").name_en("Tomato").likeCount(0).build();
        Veggie veggie2 = Veggie.builder().id(3).name_et("Kartul").name_en("Potato").likeCount(0).build();
        Veggie veggie3 = Veggie.builder().id(6).name_et("Porrulauk").name_en("Leek").likeCount(0).build();
        Veggie veggie4 = Veggie.builder().id(5).name_et("Porgand").name_en("Carrot").likeCount(0).build();
        Veggie veggie5 = Veggie.builder().id(4).name_et("Pastinaak").name_en("Parsnip").likeCount(0).build();
        Veggie veggie6 = Veggie.builder().id(1).name_et("Fenkol").name_en("Fennel").likeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5, veggie6);
        given(veggieRepo.findAllByOrderByLikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(true);
        List<Integer> emptyList = new ArrayList<>();
        assertEquals(emptyList, topVeggies);
    }

    @Test
    void noLeastPopularVeggiesReturned() {
        Veggie veggie1 = Veggie.builder().id(2).name_et("Tomat").name_en("Tomato").dislikeCount(0).build();
        Veggie veggie2 = Veggie.builder().id(3).name_et("Kartul").name_en("Potato").dislikeCount(0).build();
        Veggie veggie3 = Veggie.builder().id(1).name_et("Porrulauk").name_en("Leek").dislikeCount(0).build();
        Veggie veggie4 = Veggie.builder().id(5).name_et("Porgand").name_en("Carrot").dislikeCount(0).build();
        Veggie veggie5 = Veggie.builder().id(4).name_et("Pastinaak").name_en("Parsnip").dislikeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5);
        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(false);
        List<Integer> emptyList = new ArrayList<>();
        assertEquals(emptyList, topVeggies);
    }

    @Test
    void oneMostPopularVeggieReturned() {
        Veggie veggie1 = Veggie.builder().id(4).name_et("Tomat").name_en("Tomato").likeCount(3).build();
        Veggie veggie2 = Veggie.builder().id(1).name_et("Kartul").name_en("Potato").likeCount(2).build();
        Veggie veggie3 = Veggie.builder().id(3).name_et("Porrulauk").name_en("Leek").likeCount(2).build();
        Veggie veggie4 = Veggie.builder().id(5).name_et("Porgand").name_en("Carrot").likeCount(1).build();
        Veggie veggie5 = Veggie.builder().id(2).name_et("Pastinaak").name_en("Parsnip").likeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5);
        given(veggieRepo.findAllByOrderByLikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(true);
        List<Integer> topVeggieIndex = List.of(4);
        assertEquals(topVeggieIndex, topVeggies);
    }

    @Test
    void oneLeastPopularVeggieReturned() {
        Veggie veggie1 = Veggie.builder().id(6).name_et("Tomat").name_en("Tomato").dislikeCount(3).build();
        Veggie veggie2 = Veggie.builder().id(1).name_et("Kartul").name_en("Potato").dislikeCount(2).build();
        Veggie veggie3 = Veggie.builder().id(3).name_et("Porrulauk").name_en("Leek").dislikeCount(2).build();
        Veggie veggie4 = Veggie.builder().id(5).name_et("Porgand").name_en("Carrot").dislikeCount(1).build();
        Veggie veggie5 = Veggie.builder().id(2).name_et("Pastinaak").name_en("Parsnip").dislikeCount(1).build();
        Veggie veggie6 = Veggie.builder().id(4).name_et("Fenkol").name_en("Fennel").dislikeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5, veggie6);
        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(false);
        List<Integer> topVeggieIndex = List.of(6);
        assertEquals(topVeggieIndex, topVeggies);
    }

    @Test
    void multipleMostPopularVeggiesReturned() {
        Veggie veggie1 = Veggie.builder().id(5).name_et("Tomat").name_en("Tomato").likeCount(4).build();
        Veggie veggie2 = Veggie.builder().id(2).name_et("Kartul").name_en("Potato").likeCount(4).build();
        Veggie veggie3 = Veggie.builder().id(3).name_et("Porrulauk").name_en("Leek").likeCount(4).build();
        Veggie veggie4 = Veggie.builder().id(1).name_et("Porgand").name_en("Carrot").likeCount(2).build();
        Veggie veggie5 = Veggie.builder().id(4).name_et("Pastinaak").name_en("Parsnip").likeCount(2).build();
        Veggie veggie6 = Veggie.builder().id(6).name_et("Fenkol").name_en("Fennel").likeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5, veggie6);
        given(veggieRepo.findAllByOrderByLikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(true);
        List<Integer> topVeggieIndexes = List.of(5, 2, 3);
        assertEquals(topVeggieIndexes, topVeggies);
    }

    @Test
    void multipleLeastPopularVeggiesReturned() {
        Veggie veggie1 = Veggie.builder().id(5).name_et("Tomat").name_en("Tomato").dislikeCount(4).build();
        Veggie veggie2 = Veggie.builder().id(2).name_et("Kartul").name_en("Potato").dislikeCount(4).build();
        Veggie veggie3 = Veggie.builder().id(7).name_et("Porrulauk").name_en("Leek").dislikeCount(4).build();
        Veggie veggie4 = Veggie.builder().id(1).name_et("Porgand").name_en("Carrot").dislikeCount(2).build();
        Veggie veggie5 = Veggie.builder().id(4).name_et("Pastinaak").name_en("Parsnip").dislikeCount(2).build();
        Veggie veggie6 = Veggie.builder().id(6).name_et("Fenkol").name_en("Fennel").dislikeCount(0).build();
        Veggie veggie7 = Veggie.builder().id(3).name_et("Murulauk").name_en("Chive").dislikeCount(0).build();
        List<Veggie> orderedVeggies = List.of(veggie1, veggie2, veggie3, veggie4, veggie5, veggie6, veggie7);
        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willReturn(orderedVeggies);
        List<Integer> topVeggies = dbQueryStats.findTopVeggies(false);
        List<Integer> topVeggieIndexes = List.of(5, 2, 7);
        assertEquals(topVeggieIndexes, topVeggies);
    }

    @Test
    void findingMostPopularVeggiesFails() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        given(veggieRepo.findAllByOrderByLikeCountDesc()).willThrow(ex);
        String exMessage =
                assertThrows(DbQueryException.class, () -> dbQueryStats.findTopVeggies(true)).getMessage();
        assertEquals("Could not retrieve most popular veggie list from db.", exMessage);
        verify(veggieRepo, times(1)).findAllByOrderByLikeCountDesc();
    }

    @Test
    void findingLeastPopularVeggiesFails() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willThrow(ex);
        String exMessage =
                assertThrows(DbQueryException.class, () -> dbQueryStats.findTopVeggies(false)).getMessage();
        assertEquals("Could not retrieve most popular veggie list from db.", exMessage);
        verify(veggieRepo, times(1)).findAllByOrderByDislikeCountDesc();
    }

    @Test
    void averagePercentageIsZero() {
        given(individualEntryRepo.getAveragePercentage()).willReturn(0);
        int average = dbQueryStats.findAveragePercentage();
        assertEquals(0, average);
    }

    @Test
    void averagePercentageIsNotZero() {
        given(individualEntryRepo.getAveragePercentage()).willReturn(54);
        int average = dbQueryStats.findAveragePercentage();
        assertEquals(54, average);
    }

    @Test
    void averagePercentageFails() {
        DataAccessException ex = new DataAccessResourceFailureException("");
        given(individualEntryRepo.getAveragePercentage()).willThrow(ex);
        String exMessage =
                assertThrows(DbQueryException.class, () -> dbQueryStats.findAveragePercentage()).getMessage();
        assertEquals("Could not retrieve average percentage from db.", exMessage);
        verify(individualEntryRepo, times(1)).getAveragePercentage();
    }
}
