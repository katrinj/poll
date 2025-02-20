package com.oa.poll.mapper;

import com.oa.poll.dto.PollResults;
import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.dto.SubmitPollResponse;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PersonalData;

public interface IDataMapper {
    PersonalData createPersonalData(SubmitPollRequest submitPollRequest);

    IndividualEntry createIndividualEntry(SubmitPollRequest submitPollRequest, PersonalData personalData);

    SubmitPollResponse createSubmitPollResponse(PollResults pollResults);
}
