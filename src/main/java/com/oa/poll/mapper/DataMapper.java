package com.oa.poll.mapper;

import com.oa.poll.dto.SubmitPollRequest;
import com.oa.poll.entity.IndividualEntry;
import com.oa.poll.entity.PersonalData;
import org.springframework.stereotype.Component;

@Component
public class DataMapper implements IDataMapper {
    @Override
    public PersonalData createPersonalData(SubmitPollRequest submitPollRequest) {
        return PersonalData.builder()
                .email(submitPollRequest.getEmail())
                .build();
    }

    @Override
    public IndividualEntry createIndividualEntry(SubmitPollRequest submitPollRequest, PersonalData personalData) {
        return IndividualEntry.builder()
                .percentage(submitPollRequest.getPercentage())
                .likeCount(submitPollRequest.getLikedVeggies().size())
                .dislikeCount(submitPollRequest.getDislikedVeggies().size())
                .personalData(personalData)
                .build();
    }

}