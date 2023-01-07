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
        var message = messageSource.getMessage("custom.annotation.value.of.enum.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Default Message of CpfAlreadyExist defined in message.properties")
    void mustToGetDefaultMessageOfCpfAlreadyExistAnnotation() {

        // cenary
        var expectedMessage = "The entered value already exists in the database, enter another one.";

        // action
        var message = messageSource.getMessage("custom.annotation.cpf.already.exists.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Marital Status Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserMaritalStatusInvalid() {

        // cenary
        var expectedMessage = "Invalid value, accepted values: SINGLE, MARRIED, DIVORCED, SEPARATED, WIDOWED .";

        // action
        var message = messageSource.getMessage("user.marital.status.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Sex Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserSexInvalid() {

        // cenary
        var expectedMessage = "Invalid value, accepted values: MALE, FEMALE, DO_NOT_INFORM .";

        // action
        var message = messageSource.getMessage("user.sex.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User CPF Already Exists defined in message.properties")
    void mustToGetCustomMessageOfUserCpfAlreadyExists() {

        // cenary
        var expectedMessage = "The CPF entered already exists.";

        // action
        var message = messageSource.getMessage("user.cpf.field.message.already.exists", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Invalid User Exception defined in message.properties")
    void mustToGetCustomMessageInvalidUserException() {

        // cenary
        var expectedMessage = "Invalid User found, it cannot be are null.";

        // action
        var message = messageSource.getMessage("invalid.user.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Not Found Exception defined in message.properties")
    void mustToGetCustomMessageUserNotFoundException() {

        // cenary
        var expectedMessage = "User not found.";

        // action
        var message = messageSource.getMessage("user.not.found.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

}