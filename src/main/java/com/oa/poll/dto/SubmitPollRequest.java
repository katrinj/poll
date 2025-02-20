package com.oa.poll.dto;

import com.oa.poll.validator.VeggieType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SubmitPollRequest {
    private static final int EMAIL_MIN_LENGTH = 10;
    private static final int EMAIL_MAX_LENGTH = 80;
    private static final int PERCENTAGE_MIN_VALUE = 0;
    private static final int PERCENTAGE_MAX_VALUE = 100;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be in valid format.")
    @Size(min = EMAIL_MIN_LENGTH, message = "Email should be at least " + EMAIL_MIN_LENGTH + " characters long.")
    @Size(max = EMAIL_MAX_LENGTH, message = "Email should be up to " + EMAIL_MAX_LENGTH + " characters long.")
    private String email;

    @NotNull(message = "List of liked veggies is required.")
    @VeggieType
    private List<Integer> likedVeggies;

    @NotNull(message = "List of disliked veggies is required.")
    @VeggieType
    private List<Integer> dislikedVeggies;

    @NotNull(message = "Percentage is required.")
    @Min(value = PERCENTAGE_MIN_VALUE, message = "Percentage must not be lower than " + PERCENTAGE_MIN_VALUE)
    @Max(value = PERCENTAGE_MAX_VALUE, message = "Percentage must not be higher than " + PERCENTAGE_MAX_VALUE)
    private int percentage;
}
