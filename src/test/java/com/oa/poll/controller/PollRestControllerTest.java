package com.oa.poll.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import com.oa.poll.service.PollService;
import com.oa.poll.validator.LoadedData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PollRestController.class)
class PollRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    PollService pollService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testSuccessfulSubmission() throws Exception {
        LoadedData.VEGGIE_KEYS = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> likedVeggies = List.of(1, 2, 5);
        List<Integer> dislikedVeggies = List.of(3, 4);
        int percentage = 45;
        SubmitPollRequest submitPollRequest = SubmitPollRequest.builder()
                .email("smth@smth.com")
                .likedVeggies(likedVeggies)
                .dislikedVeggies(dislikedVeggies)
                .percentage(percentage)
                .build();

        SubmitPollResponse submitPollResponse = SubmitPollResponse.builder()
                .mostPopularVeggies(likedVeggies)
                .leastPopularVeggies(dislikedVeggies)
                .averagePercentage(percentage)
                .build();
        given(pollService.handlePollSubmission(submitPollRequest)).willReturn(submitPollResponse);

        ResultActions resultActions = mockMvc.perform(post("/poll/submit")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(submitPollRequest)));

        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mostPopularVeggies", is(likedVeggies)))
                .andExpect(jsonPath("$.leastPopularVeggies", is(dislikedVeggies)))
                .andExpect(jsonPath("$.averagePercentage", is(percentage)));
    }
}