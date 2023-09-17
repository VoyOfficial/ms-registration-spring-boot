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

        // scenario
        var expectedMessage = "must be any of enum {enumClass}";

        // action
        var message = messageSource.getMessage("custom.annotation.value.of.enum.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Default Message of CpfAlreadyExist defined in message.properties")
    void mustToGetDefaultMessageOfCpfAlreadyExistAnnotation() {

        // scenario
        var expectedMessage = "The entered value already exists in the database, enter another one.";

        // action
        var message = messageSource.getMessage("custom.annotation.cpf.already.exists.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Marital Status Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserMaritalStatusInvalid() {

        // scenario
        var expectedMessage = "Invalid value, accepted values: SINGLE, MARRIED, DIVORCED, SEPARATED, WIDOWED .";

        // action
        var message = messageSource.getMessage("user.marital.status.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Sex Invalid defined in message.properties")
    void mustToGetCustomMessageOfUserSexInvalid() {

        // scenario
        var expectedMessage = "Invalid value, accepted values: MALE, FEMALE, DO_NOT_INFORM .";

        // action
        var message = messageSource.getMessage("user.sex.message.invalid", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User CPF Already Exists defined in message.properties")
    void mustToGetCustomMessageOfUserCpfAlreadyExists() {

        // scenario
        var expectedMessage = "The CPF entered already exists.";

        // action
        var message = messageSource.getMessage("user.cpf.field.message.already.exists", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Invalid User Exception defined in message.properties")
    void mustToGetCustomMessageInvalidUserException() {

        // scenario
        var expectedMessage = "Invalid User found, it cannot be been null.";

        // action
        var message = messageSource.getMessage("invalid.user.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of User Not Found Exception defined in message.properties")
    void mustToGetCustomMessageUserNotFoundException() {

        // scenario
        var expectedMessage = "User not found.";

        // action
        var message = messageSource.getMessage("user.not.found.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Places API Client Exception defined in message.properties")
    void mustToGetCustomMessagePlacesApiClientException() {

        // scenario
        var expectedMessage = "Occurred an error while connecting to the Places API.";

        // action
        var message = messageSource.getMessage("error.places.api.default.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Place Not Found Api Client Exception defined in message.properties")
    void mustToGetCustomMessagePlaceNotFoundApiClientException() {

        // scenario
        var expectedMessage = "Place informed not found in Google Places.";

        // action
        var message = messageSource.getMessage("error.places.api.not.found.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Place Invalid Request Api Client Exception defined in message.properties")
    void mustToGetCustomMessagePlaceInvalidRequestApiClientException() {

        // scenario
        var expectedMessage = "Invalid Request to Google Places, maybe PlaceId is not a valid.";

        // action
        var message = messageSource.getMessage("error.places.api.details.invalid.request.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Nearby Places Invalid Request Api Client Exception defined in message.properties")
    void mustToGetCustomMessageNearbyPlacesInvalidRequestApiClientException() {

        // scenario
        var expectedMessage = "Invalid Request to Google Places, maybe coordinates or radius are invalid.";

        // action
        var message = messageSource.getMessage("error.places.api.nearby.place.invalid.request.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Request Denied Api Client Exception defined in message.properties")
    void mustToGetCustomMessageRequestDeniedApiClientException() {

        // scenario
        var expectedMessage = "Request Denied to Google Places, maybe your API Key is invalid.";

        // action
        var message = messageSource.getMessage("error.places.api.request.denied.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Unknown Error Api Client Exception defined in message.properties")
    void mustToGetCustomMessageUnknownErrorApiClientException() {

        // scenario
        var expectedMessage = "Unknown error while connecting to the Places API.";

        // action
        var message = messageSource.getMessage("error.places.api.unknown.error.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Over Query Limit Api Client Exception defined in message.properties")
    void mustToGetCustomMessageOverQueryLimitApiClientException() {

        // scenario
        var expectedMessage = "You have exceeded your daily request quota for Google Places API: Billing has not been enabled on your account, The monthly $200 credit, or a self-imposed usage cap, has been exceeded or The provided method of payment is no longer valid (for example, a credit card has expired).";

        // action
        var message = messageSource.getMessage("error.places.api.over.query.limit.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Nearby Places Zero Results Api Client Exception defined in message.properties")
    void mustToGetCustomMessageNearbyPlacesZeroResultsApiClientException() {

        // scenario
        var expectedMessage = "The search was successful but returned no results. This may occur if the search was passed a latlng in a remote location.";

        // action
        var message = messageSource.getMessage("error.places.api.nearby.places.zero.results.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

    @Test
    @DisplayName("Must to get a Custom Message of Place Details Zero Results Api Client Exception defined in message.properties")
    void mustToGetCustomMessagePlaceDetailsZeroResultsApiClientException() {

        // scenario
        var expectedMessage = "PlaceId was valid but no longer refers to a valid result. This maybe occur if the establishment is no longer in business.";

        // action
        var message = messageSource.getMessage("error.places.api.details.zero.results.message", null, Locale.getDefault());

        // validation
        assertEquals(expectedMessage, message);

    }

}