package src.application.validation.valueOfEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import src.infrastructure.model.enums.SexEnum;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueOfEnumValidatorTest {

    @Mock
    ConstraintValidatorContext context;
    ValueOfEnumValidator valueOfEnumValidator;

    @BeforeEach
    void setUp() {
        this.valueOfEnumValidator = new ValueOfEnumValidator();
        this.valueOfEnumValidator.initialize(createValueOfEnumAnnotation());
    }

    private ValueOfEnum createValueOfEnumAnnotation() {

        return new ValueOfEnum() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Class<? extends Enum<?>> enumClass() {
                return SexEnum.class;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };

    }

    @Test
    @DisplayName("Must check if the given strings exists in SexEnum and return True")
    void mustToCheckIfTheGivenStringsExistsInTheSexEnumAndTrue() {

        // scenario
        var maleString = "male";
        var femaleString = "female";
        var doNotInformString = "do_not_inform";

        // action
        var maleIsValid = valueOfEnumValidator.isValid(maleString, context);
        var femaleIsValid = valueOfEnumValidator.isValid(femaleString, context);
        var doNotInformIsValid = valueOfEnumValidator.isValid(doNotInformString, context);

        // validation
        assertTrue(maleIsValid);
        assertTrue(femaleIsValid);
        assertTrue(doNotInformIsValid);

    }

    @Test
    @DisplayName("Must check if the given wrong string exists in SexEnum and Return False")
    void mustToCheckIfTheGivenWrongStringExistsInTheSexEnumToReturnFalse() {

        // scenario
        var maleString = "malee";
        var femaleString = "ffemalee";
        var doNotInformString = "do not inform";

        // action
        var maleIsValid = valueOfEnumValidator.isValid(maleString, context);
        var femaleIsValid = valueOfEnumValidator.isValid(femaleString, context);
        var doNotInformIsValid = valueOfEnumValidator.isValid(doNotInformString, context);

        // validation
        assertFalse(maleIsValid);
        assertFalse(femaleIsValid);
        assertFalse(doNotInformIsValid);

    }

    @Test
    @DisplayName("Must check if the given Null Type in SexEnum should to Return False")
    void mustToCheckIfTheGivenNullTypeInTheSexEnumToReturnFalse() {

        // scenario
        String nullTypeString = null;

        // action
        var nullTypeStringIsValid = valueOfEnumValidator.isValid(nullTypeString, context);

        // validation
        assertFalse(nullTypeStringIsValid);

    }

}
