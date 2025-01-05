package com.oa.poll.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public record SubmitPollResponse (
        String message,
        List<String> mostPopularVeggie,
        List<String> leastPopularVeggie,
        int averageFrequency) {
}
