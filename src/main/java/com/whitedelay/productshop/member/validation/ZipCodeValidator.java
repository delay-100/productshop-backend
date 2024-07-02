package com.whitedelay.productshop.member.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ZipCodeValidator implements ConstraintValidator<ZipCode, Integer> {

    @Override
    public void initialize(ZipCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer zipCode, ConstraintValidatorContext context) {
        if (zipCode == null) {
            return false;
        }
        String zipCodeString = String.valueOf(zipCode);
        return zipCodeString.matches("\\d{5}");
    }
}
