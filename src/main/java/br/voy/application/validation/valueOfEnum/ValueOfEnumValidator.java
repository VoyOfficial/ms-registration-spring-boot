package br.voy.application.validation.valueOfEnum;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnum valueOfEnumAnnotation) {

        acceptedValues =
                Stream.of(valueOfEnumAnnotation.enumClass().getEnumConstants())
                        .map(Enum::name)
                        .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null) {
            return false;
        }

        return acceptedValues.contains(value.toUpperCase());
    }
}
