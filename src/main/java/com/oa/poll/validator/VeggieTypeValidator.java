package com.oa.poll.validator;

import com.oa.poll.service.IDbLoadConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VeggieTypeValidator implements ConstraintValidator<VeggieType, List<Integer>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(VeggieTypeValidator.class);

    private final IDbLoadConfig dbLoadConfig;

    public VeggieTypeValidator(IDbLoadConfig dbLoadConfig) {
        this.dbLoadConfig = dbLoadConfig;
    }

    @Override
    public boolean isValid(List<Integer> veggieKeys, ConstraintValidatorContext constraintValidatorContext) {
        for (Integer veggieKey : veggieKeys) {
            if (!dbLoadConfig.getVeggieKeys().contains(veggieKey)) {
                LOGGER.warn("Key {} could not be found in the key list loaded from db.", veggieKey);
                return false;
            }
        }
        return true;
    }
}
