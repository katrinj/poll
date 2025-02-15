package com.oa.poll.dto;

import com.oa.poll.validator.VeggieType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.oa.poll.dataconfig.PollDataConfig.PERCENTAGE_MIN;
import static com.oa.poll.dataconfig.PollDataConfig.PERCENTAGE_MAX;

@Data
@AllArgsConstructor
@Builder
public class SubmitPollRequest {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be in valid format.")
    @Size(max = 80, message = "Email should be up to 80 characters long.")
    @Size(min = 10, message = "Email should be at least 10 characters long.")
    private String email;

    @NotNull(message = "List of liked veggies is required.")
    @VeggieType
    private List<Integer> likedVeggies;

    @NotNull(message = "List of disliked veggies is required.")
    @VeggieType
    private List<Integer> dislikedVeggies;

    @NotNull(message = "Percentage is required.")
    @Min(value = PERCENTAGE_MIN, message = "Percentage must not be lower than " + PERCENTAGE_MIN)
    @Max(value = PERCENTAGE_MAX, message = "Percentage must not be higher than " + PERCENTAGE_MAX)
    private Integer percentage;
}
