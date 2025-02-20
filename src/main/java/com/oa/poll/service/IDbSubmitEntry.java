package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;

public interface IDbSubmitEntry {
    void addSubmission(SubmitPollRequest submitPollRequest);
}
