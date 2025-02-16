package com.oa.poll.controller;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.PercentageStats;
import com.oa.poll.entity.Veggie;
import com.oa.poll.repository.IndividualEntryRepo;
import com.oa.poll.repository.PercentageStatsRepo;
import com.oa.poll.repository.PersonalDataRepo;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class PollRestControllerIT {
    private final MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired IndividualEntryRepo individualEntryRepo;
    @Autowired PersonalDataRepo personalDataRepo;
    @Autowired PercentageStatsRepo percentageStatsRepo;
    @Autowired VeggieRepo veggieRepo;

    @Autowired
    public PollRestControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void init() {
        individualEntryRepo.deleteAll();
        personalDataRepo.deleteAll();
        PercentageStats stats = percentageStatsRepo.findAll().get(0);
        stats.setRunningTotal(0L);
        stats.setAverage(0);
        stats.setEntryCount(0L);
        percentageStatsRepo.save(stats);
        List<Veggie> veggies = veggieRepo.findAll();
        veggies.forEach(v -> {
            v.setLikeCount(0L);
            v.setDislikeCount(0L);
        });
        veggieRepo.saveAll(veggies);
    }

    @Test
    void testValidFirstEntryIsSuccessful() throws Exception {
        List<Integer> likedVeggies = List.of(1, 2, 5);
        List<Integer> dislikedVeggies = List.of(3, 4);
        int percentage = 45;
        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email("smth@smth.com")
                .likedVeggies(likedVeggies)
                .dislikedVeggies(dislikedVeggies)
                .percentage(percentage)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/poll/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitPollRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("mostPopularVeggies", is(likedVeggies)))
                .andExpect(jsonPath("leastPopularVeggies", is(dislikedVeggies)))
                .andExpect(jsonPath("averagePercentage", is(percentage)));
    }

    @Test
    void testValidSecondEntryGetsAggregated() throws Exception {
        List<Integer> likedVeggies1 = List.of(1, 2, 5);
        List<Integer> dislikedVeggies1 = List.of(3, 4);
        int percentage1 = 45;
        SubmitPollRequest submitPollRequest1 = SubmitPollRequest.builder()
                .email("smth@smth.com")
                .likedVeggies(likedVeggies1)
                .dislikedVeggies(dislikedVeggies1)
                .percentage(percentage1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/poll/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitPollRequest1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<Integer> likedVeggies2 = List.of(2, 3, 5, 7);
        List<Integer> dislikedVeggies2 = List.of(4, 8);
        int percentage2 = 55;
        SubmitPollRequest submitPollRequest2 = SubmitPollRequest.builder()
                .email("smth2@smth.com")
                .likedVeggies(likedVeggies2)
                .dislikedVeggies(dislikedVeggies2)
                .percentage(percentage2)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/poll/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitPollRequest2)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("mostPopularVeggies", is(List.of(2, 5))))
                .andExpect(jsonPath("leastPopularVeggies", is(List.of(4))))
                .andExpect(jsonPath("averagePercentage", is(50)));
    }

    @Test
    void testReentryFails() throws Exception {
        List<Integer> likedVeggies = List.of(1, 2, 5);
        List<Integer> dislikedVeggies = List.of(3, 4);
        int percentage = 25;
        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email("smth3@smth.com")
                .likedVeggies(likedVeggies)
                .dislikedVeggies(dislikedVeggies)
                .percentage(percentage)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/poll/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitPollRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/poll/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitPollRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
