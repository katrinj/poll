package com.oa.poll.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VeggieTypeValidator implements ConstraintValidator<VeggieType, List<Integer>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(VeggieTypeValidator.class);
    @Override
    public boolean isValid(List<Integer> veggieKeys, ConstraintValidatorContext constraintValidatorContext) {
        LoadedData.checkLoadedData();

        for (Integer veggieKey : veggieKeys) {
            if (!LoadedData.VEGGIE_KEYS.contains(veggieKey)) {
                LOGGER.warn("Key " + veggieKey + " could not be found in the key list loaded from db.");
                return false;
            }
        }
        return true;
    }
}
