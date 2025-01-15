package com.oa.poll.service;

import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PercentageStats;
import com.oa.poll.entity.PollSubmission;
import com.oa.poll.entity.Veggie;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PercentageStatsRepo;
import com.oa.poll.repository.PollSubmissionRepo;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class H2DbServiceTest {

    @Mock
    private VeggieRepo veggieRepo;
    @Mock
    private PercentageStatsRepo percentageStatsRepo;
    @Mock
    private IndividualEntryRepo individualEntryRepo;
    @Mock
    private PollSubmissionRepo pollSubmissionRepo;

    @InjectMocks
    private H2DbService h2DbService;

    @Test
    void dbConfigListsAreSameSizeNoExceptionThrown() {
        List<String> veggiesEt = List.of("Tomat", "Redis");
        List<String> veggiesEn = List.of("Tomato", "Radish");
        h2DbService.checkDBConfig(veggiesEt, veggiesEn);
    }

    @Test
    void firstDbConfigListIsLongerThanSecondIllegalStateThrown() {
        List<String> veggiesEt = List.of("Tomat", "Redis", "Kurk");
        List<String> veggiesEn = List.of("Tomato", "Radish");
        Exception exception = assertThrows(IllegalStateException.class, () -> h2DbService.checkDBConfig(veggiesEt,
                veggiesEn));
        assertEquals("Illegal db configuration: veggie lists should be of same size", exception.getMessage());
    }

    @Test
    void firstDbConfigListIsShorterThanSecondIllegalStateThrown() {
        List<String> veggiesEt = List.of("Tomat", "Redis");
        List<String> veggiesEn = List.of("Tomato", "Radish", "Cucumber");
        Exception exception = assertThrows(IllegalStateException.class, () -> h2DbService.checkDBConfig(veggiesEt,
                veggiesEn));
        assertEquals("Illegal db configuration: veggie lists should be of same size", exception.getMessage());
    }

    @Test
    void veggieGetsAdded() {
        Veggie v = Veggie.builder().name_et("Tomat").name_en("Tomato").build();
        Veggie vExpected = Veggie.builder()
                .name_et("Tomat")
                .name_en("Tomato")
                .id(1)
                .likeCount(0L)
                .dislikeCount(0L)
                .build();
        given(veggieRepo.save(v)).willReturn(vExpected);
        Veggie vCreated = h2DbService.addVeggie(v);
        assertEquals(vExpected, vCreated);
    }

    @Test
    void minMaxPercentagesGetAdded() {
        PercentageStats p = PercentageStats.builder().min(0).max(100).build();
        PercentageStats pExpected =
                PercentageStats.builder().id(1).min(0).max(0).entryCount(0L).runningTotal(0L).average(0).build();
        given(percentageStatsRepo.save(p)).willReturn(pExpected);
        PercentageStats pCreated = h2DbService.addMinMax(p);
        assertEquals(pExpected, pCreated);
    }

    @Test
    void mostPopularOneVeggieReturned() {
        List<Veggie> veggies = new ArrayList<>();
        veggies.add(Veggie.builder().id(2).name_et("Tomat").name_en("Tomato").likeCount(6).build());
        veggies.add(Veggie.builder().id(7).name_et("Kurk").name_en("Cucumber").likeCount(4).build());
        veggies.add(Veggie.builder().id(3).name_et("Redis").name_en("Redis").likeCount(3).build());
        veggies.add(Veggie.builder().id(4).name_et("Peakapsas").name_en("Cabbage").likeCount(3).build());
        veggies.add(Veggie.builder().id(6).name_et("Küüslauk").name_en("Garlic").likeCount(1).build());

        given(veggieRepo.findAllByOrderByLikeCountDesc()).willReturn(veggies);

        List<Integer> expectedMostPopularList = List.of(2);
        List<Integer> mostPopularList = h2DbService.findMostPopularVeggies();
        assertEquals(expectedMostPopularList, mostPopularList);
    }

    @Test
    void mostPopularVeggiesListReturned() {
        List<Veggie> veggies = new ArrayList<>();
        veggies.add(Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").likeCount(4).build());
        veggies.add(Veggie.builder().id(1).name_et("Kurk").name_en("Cucumber").likeCount(4).build());
        veggies.add(Veggie.builder().id(5).name_et("Redis").name_en("Redis").likeCount(3).build());
        veggies.add(Veggie.builder().id(2).name_et("Peakapsas").name_en("Cabbage").likeCount(3).build());
        veggies.add(Veggie.builder().id(4).name_et("Küüslauk").name_en("Garlic").likeCount(2).build());

        given(veggieRepo.findAllByOrderByLikeCountDesc()).willReturn(veggies);

        List<Integer> expectedMostPopularList = List.of(3, 1);
        List<Integer> mostPopularList = h2DbService.findMostPopularVeggies();
        assertEquals(expectedMostPopularList, mostPopularList);
    }

    @Test
    void leastPopularOneVeggieReturned() {
        List<Veggie> veggies = new ArrayList<>();
        veggies.add(Veggie.builder().id(2).name_et("Tomat").name_en("Tomato").dislikeCount(6).build());
        veggies.add(Veggie.builder().id(7).name_et("Kurk").name_en("Cucumber").dislikeCount(4).build());
        veggies.add(Veggie.builder().id(3).name_et("Redis").name_en("Redis").dislikeCount(3).build());
        veggies.add(Veggie.builder().id(4).name_et("Peakapsas").name_en("Cabbage").dislikeCount(3).build());
        veggies.add(Veggie.builder().id(6).name_et("Küüslauk").name_en("Garlic").dislikeCount(1).build());

        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willReturn(veggies);

        List<Integer> expectedLeastPopularList = List.of(2);
        List<Integer> leastPopularList = h2DbService.findLeastPopularVeggies();
        assertEquals(expectedLeastPopularList, leastPopularList);
    }

    @Test
    void leastPopularVeggiesListReturned() {
        List<Veggie> veggies = new ArrayList<>();
        veggies.add(Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").dislikeCount(4).build());
        veggies.add(Veggie.builder().id(1).name_et("Kurk").name_en("Cucumber").dislikeCount(4).build());
        veggies.add(Veggie.builder().id(5).name_et("Redis").name_en("Redis").dislikeCount(3).build());
        veggies.add(Veggie.builder().id(2).name_et("Peakapsas").name_en("Cabbage").dislikeCount(2).build());
        veggies.add(Veggie.builder().id(4).name_et("Küüslauk").name_en("Garlic").dislikeCount(2).build());

        given(veggieRepo.findAllByOrderByDislikeCountDesc()).willReturn(veggies);

        List<Integer> expectedLeastPopularList = List.of(3, 1);
        List<Integer> leastPopularList = h2DbService.findLeastPopularVeggies();
        assertEquals(expectedLeastPopularList, leastPopularList);
    }

    @Test
    void veggieLikedCountIncreased() {
        Veggie veggie = Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").likeCount(5).build();
        Veggie veggieIncreasedCount = Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").likeCount(6).build();
        given(veggieRepo.findById(3)).willReturn(veggie);
        Veggie veggieUpdated = h2DbService.updateLikedVeggie(3);
        assertEquals(veggieIncreasedCount, veggieUpdated);
    }

    @Test
    void veggieDislikedCountIncreased() {
        Veggie veggie = Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").dislikeCount(4).build();
        Veggie veggieIncreasedCount = Veggie.builder().id(3).name_et("Tomat").name_en("Tomato").dislikeCount(5).build();
        given(veggieRepo.findById(3)).willReturn(veggie);
        Veggie veggieUpdated = h2DbService.updateDislikedVeggie(3);
        assertEquals(veggieIncreasedCount, veggieUpdated);
    }

    @Test
    void percentageStatsAreUpdatedOnFirstEntry() {
        int percentage = 34;
        long entryCount = 0L;
        long runningTotal = 0L;
        PercentageStats percentageStats =
                PercentageStats.builder()
                        .id(1)
                        .min(0)
                        .max(0)
                        .entryCount(entryCount)
                        .runningTotal(runningTotal)
                        .average(0)
                        .build();
        List<PercentageStats> percentageStatsList = List.of(percentageStats);
        given(percentageStatsRepo.findAll()).willReturn(percentageStatsList);
        PercentageStats percentageStatsUpdated = h2DbService.updatePercentageStats(percentage);
        assertEquals(entryCount + 1, percentageStatsUpdated.getEntryCount());
        assertEquals(runningTotal + percentage, percentageStatsUpdated.getRunningTotal());
        assertEquals((runningTotal + percentage) / (entryCount + 1), percentageStatsUpdated.getAverage());
    }

    @Test
    void percentageStatsAreUpdatedOnNonFirstEntry() {
        int percentage = 34;
        long entryCount = 3L;
        long runningTotal = 124L;
        PercentageStats percentageStats =
                PercentageStats.builder()
                        .id(1)
                        .min(0)
                        .max(0)
                        .entryCount(entryCount)
                        .runningTotal(runningTotal)
                        .average(41)
                        .build();
        List<PercentageStats> percentageStatsList = List.of(percentageStats);
        given(percentageStatsRepo.findAll()).willReturn(percentageStatsList);
        PercentageStats percentageStatsUpdated = h2DbService.updatePercentageStats(percentage);
        assertEquals(entryCount + 1, percentageStatsUpdated.getEntryCount());
        assertEquals(runningTotal + percentage, percentageStatsUpdated.getRunningTotal());
        assertEquals((runningTotal + percentage) / (entryCount + 1), percentageStatsUpdated.getAverage());
    }

    @Test
    void pollSubmissionIsAdded() {
        PollSubmission pollSubmission = PollSubmission.builder().email("smth@smth.com").build();
        PollSubmission pollSubmissionExpected = PollSubmission.builder().id(1).email("smth@smth.com").build();
        given(pollSubmissionRepo.save(pollSubmission)).willReturn(pollSubmissionExpected);
        PollSubmission pollSubmissionCreated = h2DbService.addPollSubmission(pollSubmission);
        assertEquals(pollSubmissionExpected, pollSubmissionCreated);
    }

    @Test
    void individualEntryIsAdded() {
        PollSubmission pollSubmission = PollSubmission.builder().id(1).email("smth@smth.com").build();
        IndividualEntry individualEntry =
                IndividualEntry.builder()
                        .pollSubmission(pollSubmission)
                        .percentage(42)
                        .likeCount(5)
                        .dislikeCount(0)
                        .build();
        IndividualEntry individualEntryExpected =
                IndividualEntry.builder()
                        .id(1)
                        .pollSubmission(pollSubmission)
                        .percentage(42)
                        .likeCount(5)
                        .dislikeCount(0)
                        .build();

        given(individualEntryRepo.save(individualEntry)).willReturn(individualEntryExpected);
        IndividualEntry individualEntryCreated = h2DbService.addIndvidualEntry(individualEntry);
        assertEquals(individualEntryExpected, individualEntryCreated);
    }
}