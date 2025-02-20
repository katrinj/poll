package com.oa.poll.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PollResults (
    List<Integer> mostPopularVeggies,
    List<Integer> leastPopularVeggies,
    int averagePercentage) {

}
