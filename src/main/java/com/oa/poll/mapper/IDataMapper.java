package com.oa.poll.mapper;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PollSubmission;

public interface IDataMapper {
    PollSubmission createPollSubmission(SubmitPollRequest submitPollRequest);
    IndividualEntry createIndividualEntry(SubmitPollRequest submitPollRequest, PollSubmission pollSubmission);
}
