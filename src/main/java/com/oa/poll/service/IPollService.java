package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;

public interface IPollService {
    SubmitPollResponse handlePollSubmission(SubmitPollRequest submitPollRequest);
}
