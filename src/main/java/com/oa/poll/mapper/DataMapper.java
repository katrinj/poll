package com.oa.poll.mapper;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PollSubmission;
import org.springframework.stereotype.Component;

@Component
public class DataMapper implements IDataMapper {
    @Override
    public PollSubmission createPollSubmission(SubmitPollRequest submitPollRequest) {
        return PollSubmission.builder()
                .email(submitPollRequest.getEmail())
                .build();
    }

    @Override
    public IndividualEntry createIndividualEntry(SubmitPollRequest submitPollRequest, PollSubmission pollSubmission) {
        return IndividualEntry.builder()
                .percentage(submitPollRequest.getPercentage())
                .likeCount(submitPollRequest.getLikedVeggies().size())
                .dislikeCount(submitPollRequest.getDislikedVeggies().size())
                .pollSubmission(pollSubmission)
                .build();
    }

}