package com.oa.poll.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SubmitPollResponse (
        List<Integer> mostPopularVeggies,
        List<Integer> leastPopularVeggies,
        int averagePercentage) {
}
