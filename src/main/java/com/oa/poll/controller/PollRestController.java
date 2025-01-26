package com.oa.poll.controller;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import com.oa.poll.service.IPollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

@RestController
@RequestMapping("/poll")
public class PollRestController {

    private final IPollService pollService;
    private final static Logger LOGGER = LoggerFactory.getLogger(PollRestController.class);

    PollRestController(IPollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitPollResponse> submitPoll (@Valid @RequestBody SubmitPollRequest submitPollRequest) {
        LOGGER.info(submitPollRequest.toString());
        SubmitPollResponse response = pollService.handlePollSubmission(submitPollRequest);
        LOGGER.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
