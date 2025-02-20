package com.oa.poll.service;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import org.springframework.stereotype.Service;


@Service
public class PollService implements IPollService {
    private final IDbSubmitEntry dbSubmitEntry;
    private final IDbQueryStats dbQueryStats;

    PollService(IDbSubmitEntry dbService, IDbQueryStats dbQueryStats) {
        this.dbSubmitEntry = dbService;
        this.dbQueryStats = dbQueryStats;
    }

    @Override
    public SubmitPollResponse handlePollSubmission(SubmitPollRequest submitPollRequest) {
        dbSubmitEntry.addSubmission(submitPollRequest);
        return dbQueryStats.getSubmitPollResponse();
    }
}
