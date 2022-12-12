package src.infrastructure.config.internalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InternalizationConfig.class)
class InternalizationConfigTest {

    @Autowired
    MessageSource messageSource;

    @Test
    @DisplayName("Must to get a Default Message of ValueOfEnumAnnotation defined in message.properties")
    void mustToGetDefaultMessageOfValueOfEnumAnnotation() {

        // cenary
        var expectedMessage = "must be any of enum {enumClass}";

        // action
        var message = messageSource.getMessage("custom_annotation.value_of_enum.message.default", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Default Message of CpfAlreadyExist defined in message.properties")
    void mustToGetDefaultMessageOfCpfAlreadyExistAnnotation() {

        // cenary
        var expectedMessage = "The entered value already exists in the database, enter another one.";

        // action
        var message = messageSource.getMessage("custom_annotation.cpf.already.exists.default", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Marital Status Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserMaritalStatusInvalid() {

        // cenary
        var expectedMessage = "Invalid value, accepted values: SINGLE, MARRIED, DIVORCED, SEPARATED, WIDOWED .";

        // action
        var message = messageSource.getMessage("user_marital_status.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Sex Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserSexInvalid() {

        // cenary
        var expectedMessage = "Invalid value, accepted values: MALE, FEMALE, DO_NOT_INFORM .";

        // action
        var message = messageSource.getMessage("user_sex.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User CPF Already Exists defined in message.properties")
    void mustToGetCustomMessageOfUserCpfAlreadyExists() {

        // cenary
        var expectedMessage = "The CPF entered already exists.";

        // action
        var message = messageSource.getMessage("user_cpf.field.message.already.exists", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }
}