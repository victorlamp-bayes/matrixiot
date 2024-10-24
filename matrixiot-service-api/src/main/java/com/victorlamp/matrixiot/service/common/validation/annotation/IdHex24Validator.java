package com.victorlamp.matrixiot.service.common.validation.annotation;

import com.victorlamp.matrixiot.service.common.validation.constant.RegExps;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IdHex24Validator implements ConstraintValidator<IdHex24, String> {
//    private boolean isRequired;

    @Override
    public void initialize(IdHex24 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
//        isRequired = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.trim().isEmpty()  || Pattern.matches(RegExps.ID, s);
    }
}
