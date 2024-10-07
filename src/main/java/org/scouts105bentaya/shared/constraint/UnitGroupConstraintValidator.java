package org.scouts105bentaya.shared.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.scouts105bentaya.shared.Group;

public class UnitGroupConstraintValidator implements ConstraintValidator<IsUnit, Group> {

    @Override
    public void initialize(IsUnit arg0) {
        //empty
    }

    @Override
    public boolean isValid(Group group, ConstraintValidatorContext context) {
        return group == null || group.getValue() >= 1 && group.getValue() <= 7;
    }
}
