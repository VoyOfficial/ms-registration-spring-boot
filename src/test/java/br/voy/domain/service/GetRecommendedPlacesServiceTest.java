package br.voy.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.application.util.CurrentUserHelper;
import br.voy.domain.entity.BusinessHours;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.repository.UserSavedPlaceRepository;
import br.voy.domain.utils.BoundingBox;
import br.voy.domain.utils.PaginationTokenEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class GetRecommendedPlacesServiceTest {

    @Mock private PlaceRepository placeRepository;

    @Mock private UserSavedPlaceRepository userSavedPlaceRepository;

    @Mock private CurrentUserHelper currentUserHelper;

    @InjectMocks private GetRecommendedPlacesService placeService;

    @Value("${voy.services.places.limitMaxBoundingBox}")
    private double LIMIT_MAX_BOUNDING_BOX;

    @Value("${error.places.recommendation.status400.outOfRangeRequest.message}")
    private String OUT_OF_MAX_RANGE_MESSAGE;

    @Value("${error.places.recommendation.status400.outOfRangeRequest.km}")
    private String KM;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(placeService, "INITIAL_DEFAULT_BOUNDING_BOX_RADIUS_KM", 10.0);
        ReflectionTestUtils.setField(placeService, "INCREMENTAL_BOUNDING_BOX_RADIUS_KM", 5.0);
        ReflectionTestUtils.setField(placeService, "MAX_PLACE_SIZE_LIST", 5);
        ReflectionTestUtils.setField(placeService, "LIMIT_MAX_BOUNDING_BOX", 50);
        ReflectionTestUtils.setField(placeService, "EARTH_RADIUS_KM", 6371.0);
        ReflectionTestUtils.setField(
                placeService, "OUT_OF_MAX_RANGE_MESSAGE", "raio de busca máxima é de ");
        ReflectionTestUtils.setField(placeService, "KM", " km");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve retornar cinco lugares recomendados próximos da localização do usuário")
    void testGetRecommendedPlacesShouldReturnFiveNearbyRecommendedPlacesToUsersLocation() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
    }

    @Test
    @DisplayName("O método GetRecommendedPlaces deve remover lugares para além do raio de busca")
    void testFilterByHaversineShouldRemovePlacesLocatedFarFromRadius() {

        List<Place> placeList = new ArrayList<>();
        placeList.add(generateFarPlace());
        for (int i = 0; i < 5; i++) {
            placeList.add(generatePlaceList().get(i));
        }

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(placeList));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
        assertNotEquals(
                "Far Place", response.get(0).getName(), "esperado que esse lugar seja removido");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve lançar uma exceção quando o repositório retornar uma lista vazio")
    void testGetRecommendedPlacesShouldRemovePlacesWithoutEndRecommendationDate() {

        List<Place> placeList = new ArrayList<>();
        placeList.add(generateNonEndRecommendationPlace());
        for (int i = 0; i < 5; i++) {
            placeList.add(generatePlaceList().get(i));
        }

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(placeList));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
        assertNotEquals(
                "generateNonEndRecommendationPlace",
                response.get(0).getName(),
                "esperado que esse lugar seja removido");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve remover o place quando o end date for antes de hoje")
    void testGetRecommendedPlacesShouldRemovePlacesEndRecommendationDateBeforeToday() {

        List<Place> placeList = new ArrayList<>();
        placeList.add(generateEndRecommendationBeforeTodayPlace());
        for (int i = 0; i < 5; i++) {
            placeList.add(generatePlaceList().get(i));
        }

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(placeList));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
        assertNotEquals(
                "generateEndRecommendationBeforeTodayPlace",
                response.get(0).getName(),
                "esperado que esse lugar seja removido");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve retornar cinco lugares recomendados próximos da localização do usuário com um raio de busca providenciado")
    void testGetRecommendedPlacesShouldUseProvidedRadiusWhenProvidedRadiusIsPositive() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, 30.0);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve utilizar o raio de busca padrão quando o providenciado for negativo")
    void testGetRecommendedPlacesShouldUseDefaultRadiusWhenProvidedRadiusIsZeroOrNegative() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, -30.0);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.isEmpty(), "esperado que a response não seja vazio");
        assertTrue(response.size() <= 5, "esperado que a response tenha apenas 5 lugares");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve lançar uma exceção quando o raio de busca providenciado for maior que o limite")
    void testGetRecommendedPlacesThrowsExceptionWhenRadiusExceedsLimit() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> placeService.getRecommendedPlaces(-29.35995, -50.84805, 130.0));

        assertTrue(exception.getStatus() == HttpStatus.BAD_REQUEST);
        assertEquals("raio de busca máxima é de 50.0 km", exception.getReason());
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve lançar uma exceção quando o repositório retornar um optional vazio")
    void testGetRecommendedPlacesThrowsExceptionWhenRepositoryReturnEmpty() {

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> placeService.getRecommendedPlaces(-29.35995, -50.84805, null));

        assertTrue(exception.getStatus() == HttpStatus.NOT_FOUND);
        assertEquals("não encontrado", exception.getReason());
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces deve lançar uma exceção quando o repositório retornar uma lista vazio")
    void testGetRecommendedPlacesThrowsExceptionWhenRepositoryReturnEmptyList() {

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(new ArrayList<>()));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> placeService.getRecommendedPlaces(-29.35995, -50.84805, null));

        assertTrue(exception.getStatus() == HttpStatus.NOT_FOUND);
        assertEquals("não encontrado", exception.getReason());
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces com paginação deve retornar a primeira página de resultados")
    void testGetRecommendedPlacesWithPaginationShouldReturnFirstPage() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 3, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertNotNull(response.getPlaces(), "esperado que a lista de lugares não seja null");
        assertFalse(response.getPlaces().isEmpty(), "esperado que a response não seja vazio");
        assertEquals(3, response.getPlaces().size(), "esperado que a response tenha 3 lugares");
        assertNotNull(
                response.getNextTokenPage(), "esperado que tenha um token para a próxima página");

        assertTrue(
                response.getNextTokenPage().length() > 10,
                "esperado que o token seja maior que 10 caracteres");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces com paginação deve decodificar o token corretamente")
    void testGetRecommendedPlacesWithPaginationShouldDecodeTokenCorrectly() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        String token = PaginationTokenEncoder.encode(3);

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 3, token);

        assertNotNull(response, "esperado que a response não seja null");
        assertNotNull(response.getPlaces(), "esperado que a lista de lugares não seja null");
        assertFalse(response.getPlaces().isEmpty(), "esperado que a response não seja vazio");
        assertEquals(3, response.getPlaces().size(), "esperado que a response tenha 3 lugares");
    }

    @Test
    @DisplayName("O método GetRecommendedPlaces com paginação deve retornar token codificado")
    void testGetRecommendedPlacesWithPaginationShouldReturnEncodedToken() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 3, null);

        assertNotNull(
                response.getNextTokenPage(), "esperado que tenha um token para a próxima página");

        PaginationTokenEncoder.PaginationState decodedState =
                PaginationTokenEncoder.decode(response.getNextTokenPage());
        assertEquals(3, decodedState.getOffset(), "esperado que o token decodificado seja '3'");

        assertTrue(
                response.getNextTokenPage().length() > 10,
                "esperado que o token seja opaco e longo");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces com paginação não deve retornar token quando não houver mais páginas")
    void testGetRecommendedPlacesWithPaginationShouldNotReturnTokenWhenNoMorePages() {
        List<Place> fivePlaces = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fivePlaces.add(generatePlaceList().get(i));
        }

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(fivePlaces));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertEquals(5, response.getPlaces().size(), "esperado que a response tenha 5 lugares");
        assertNull(
                response.getNextTokenPage(),
                "esperado que não tenha token quando não há mais páginas");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces com paginação deve usar pageSize padrão quando não providenciado")
    void testGetRecommendedPlacesWithPaginationShouldUseDefaultPageSize() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, null, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertTrue(
                response.getPlaces().size() <= 5,
                "esperado que a response use o pageSize padrão de 5");
    }

    @Test
    @DisplayName("O método GetRecommendedPlaces com paginação deve lidar com token inválido")
    void testGetRecommendedPlacesWithPaginationShouldHandleInvalidToken() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var response =
                placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 3, "invalid-token");

        assertNotNull(response, "esperado que a response não seja null");
        assertNotNull(
                response.getPlaces(), "esperado que comece da primeira página com token inválido");
        assertEquals(3, response.getPlaces().size(), "esperado que a response tenha 3 lugares");
    }

    @Test
    @DisplayName("O método GetRecommendedPlaces com paginação deve navegar por múltiplas páginas")
    void testGetRecommendedPlacesWithPaginationShouldNavigateThroughMultiplePages() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));

        var page1 = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 3, null);
        assertNotNull(page1, "esperado que a primeira página não seja null");
        assertEquals(3, page1.getPlaces().size(), "esperado que a primeira página tenha 3 lugares");
        assertNotNull(page1.getNextTokenPage(), "esperado que tenha token para próxima página");

        var page2 =
                placeService.getRecommendedPlaces(
                        -29.35995, -50.84805, null, 3, page1.getNextTokenPage());
        assertNotNull(page2, "esperado que a segunda página não seja null");
        assertEquals(3, page2.getPlaces().size(), "esperado que a segunda página tenha 3 lugares");

        assertNotEquals(
                page1.getPlaces().get(0).getGooglePlaceId(),
                page2.getPlaces().get(0).getGooglePlaceId(),
                "esperado que as páginas tenham lugares diferentes");
    }

    @Test
    @DisplayName(
            "O método GetRecommendedPlaces com paginação deve lançar exceção quando raio exceder limite")
    void testGetRecommendedPlacesWithPaginationThrowsExceptionWhenRadiusExceedsLimit() {
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                placeService.getRecommendedPlaces(
                                        -29.35995, -50.84805, 130.0, 5, null));

        assertTrue(exception.getStatus() == HttpStatus.BAD_REQUEST);
        assertEquals("raio de busca máxima é de 50.0 km", exception.getReason());
    }

    @Test
    @DisplayName("O método GetRecommendedPlaces com paginação deve remover lugares expirados")
    void testGetRecommendedPlacesWithPaginationShouldRemoveExpiredPlaces() {
        List<Place> placeList = new ArrayList<>();
        placeList.add(generateEndRecommendationBeforeTodayPlace());
        for (int i = 0; i < 5; i++) {
            placeList.add(generatePlaceList().get(i));
        }

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(placeList));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response, "esperado que a response não seja null");
        assertFalse(response.getPlaces().isEmpty(), "esperado que a response não seja vazio");
        assertTrue(
                response.getPlaces().size() <= 5,
                "esperado que a response tenha no máximo 5 lugares");

        boolean hasExpiredPlace =
                response.getPlaces().stream()
                        .anyMatch(
                                place ->
                                        "generateEndRecommendationBeforeTodayPlace"
                                                .equals(place.getName()));
        assertFalse(hasExpiredPlace, "esperado que o lugar expirado seja removido");
    }

    @Test
    @DisplayName("Should mark place as saved when user has saved it")
    void shouldMarkPlaceAsSavedWhenUserHasSavedIt() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));
        when(currentUserHelper.getCurrentUserId()).thenReturn(42L);
        when(userSavedPlaceRepository.isPlaceSavedByUser(eq(42L), anyLong())).thenReturn(true);

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response.getPlaces());
        assertFalse(response.getPlaces().isEmpty());
        response.getPlaces()
                .forEach(
                        place ->
                                assertTrue(
                                        place.getIsSaved(), "esperado que o lugar esteja salvo"));
    }

    @Test
    @DisplayName("Should mark place as not saved when user has not saved it")
    void shouldMarkPlaceAsNotSavedWhenUserHasNotSavedIt() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));
        when(currentUserHelper.getCurrentUserId()).thenReturn(42L);
        when(userSavedPlaceRepository.isPlaceSavedByUser(eq(42L), anyLong())).thenReturn(false);

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response.getPlaces());
        assertFalse(response.getPlaces().isEmpty());
        response.getPlaces()
                .forEach(
                        place ->
                                assertFalse(
                                        place.getIsSaved(),
                                        "esperado que o lugar não esteja salvo"));
    }

    @Test
    @DisplayName("Should skip saved check when no authenticated user")
    void shouldSkipSavedCheckWhenNoAuthenticatedUser() {
        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(generatePlaceList()));
        when(currentUserHelper.getCurrentUserId()).thenReturn(null);

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response.getPlaces());
        assertFalse(response.getPlaces().isEmpty());
        verify(userSavedPlaceRepository, never()).isPlaceSavedByUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should expand bounding box when initial radius has insufficient places")
    void shouldExpandBoundingBoxWhenInitialRadiusHasInsufficientPlaces() {
        List<Place> singlePlace = generatePlaceList().subList(0, 1);
        List<Place> fullList = generatePlaceList();

        when(placeRepository.findPlacesWithinBoundingBox(any(BoundingBox.class)))
                .thenReturn(Optional.of(singlePlace))
                .thenReturn(Optional.of(fullList));

        var response = placeService.getRecommendedPlaces(-29.35995, -50.84805, null, 5, null);

        assertNotNull(response.getPlaces());
        verify(placeRepository, atLeast(2)).findPlacesWithinBoundingBox(any(BoundingBox.class));
    }

    private Place generateFarPlace() {
        return Place.builder()
                .id(123L)
                .googlePlaceId("123")
                .name("Far Place")
                .about("cafeteria do Hard Rock café")
                .contact("(54) 3286-4040")
                .businessHours(null)
                .rating(null)
                .userRatingsTotal(null)
                .principalPhoto("photoString")
                .photos(new ArrayList<>())
                .address("R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil")
                .city("Gramado")
                .status(false)
                .ranking(2)
                .startRecommendation(LocalDate.of(2023, 11, 17))
                .endRecommendation(LocalDate.of(2023, 12, 17))
                .createdAt(LocalDate.of(2023, 11, 17))
                .lastCancel(null)
                .distanceOfLocal(null)
                .latitude(29.3810171)
                .longitude(-50.8711053)
                .distanceFromUserLocation(null)
                .build();
    }

    private Place generateNonEndRecommendationPlace() {
        return Place.builder()
                .id(123L)
                .googlePlaceId("123")
                .name("generateNonEndRecommendationPlace")
                .about("cafeteria do Hard Rock café")
                .contact("(54) 3286-4040")
                .businessHours(null)
                .rating(null)
                .userRatingsTotal(null)
                .principalPhoto("photoString")
                .photos(new ArrayList<>())
                .address("R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil")
                .city("Gramado")
                .status(false)
                .ranking(2)
                .startRecommendation(LocalDate.of(2023, 11, 17))
                .endRecommendation(null)
                .createdAt(LocalDate.of(2023, 11, 17))
                .lastCancel(null)
                .distanceOfLocal(null)
                .latitude(29.3810171)
                .longitude(-50.8711053)
                .distanceFromUserLocation(null)
                .build();
    }

    private Place generateEndRecommendationBeforeTodayPlace() {
        return Place.builder()
                .id(123L)
                .googlePlaceId("123")
                .name("generateEndRecommendationBeforeTodayPlace")
                .about("cafeteria do Hard Rock café")
                .contact("(54) 3286-4040")
                .businessHours(null)
                .rating(null)
                .userRatingsTotal(null)
                .principalPhoto("photoString")
                .photos(new ArrayList<>())
                .address("R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil")
                .city("Gramado")
                .status(false)
                .ranking(2)
                .startRecommendation(LocalDate.of(2023, 11, 17))
                .endRecommendation(LocalDate.now().minusDays(1))
                .createdAt(LocalDate.of(2023, 11, 17))
                .lastCancel(null)
                .distanceOfLocal(null)
                .latitude(29.3810171)
                .longitude(-50.8711053)
                .distanceFromUserLocation(null)
                .build();
    }

    private static Place createPlace(
            Long id,
            String googlePlaceId,
            String name,
            String about,
            String contact,
            BusinessHours businessHours,
            Float rating,
            Integer userRatingsTotal,
            String principalPhoto,
            List<String> images,
            String address,
            String city,
            Boolean status,
            Integer ranking,
            LocalDate startRecommendation,
            LocalDate endRecommendation,
            LocalDate createdAt,
            LocalDate lastCancel,
            Float distanceOfLocal,
            Double latitude,
            Double longitude,
            String distanceFromUserLocation) {
        return Place.builder()
                .id(id)
                .googlePlaceId(googlePlaceId)
                .name(name)
                .about(about)
                .contact(contact)
                .businessHours(businessHours)
                .rating(rating)
                .userRatingsTotal(userRatingsTotal)
                .principalPhoto(principalPhoto)
                .photos(List.of(new PlacePhoto(), new PlacePhoto()))
                .address(address)
                .city(city)
                .status(status)
                .ranking(ranking)
                .startRecommendation(startRecommendation)
                .endRecommendation(endRecommendation)
                .createdAt(createdAt)
                .lastCancel(lastCancel)
                .distanceOfLocal(distanceOfLocal)
                .latitude(latitude)
                .longitude(longitude)
                .distanceFromUserLocation(distanceFromUserLocation)
                .build();
    }

    public static List<Place> generatePlaceList() {
        List<Place> places = new ArrayList<>();
        LocalDate startDateRecommendation = LocalDate.now().minusMonths(1);
        LocalDate endDateRecommendation = LocalDate.now();
        LocalDate createdDate = startDateRecommendation.minusDays(1);

        places.add(
                createPlace(
                        1L,
                        "ChIJPQmNhEMyGZURxuHk44vIaIw",
                        "Hard Rock Cafe",
                        "cafeteria do Hard Rock café",
                        "(54) 3286-4040",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil",
                        "Gramado",
                        false,
                        2,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.3810171,
                        -50.8711053,
                        null));

        places.add(
                createPlace(
                        2L,
                        "ChIJsdUuyRc1GZURg7Hy1kfaAeU",
                        "Fonte do Amor Eterno",
                        null,
                        "(54) 3286-1055",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. Borges de Medeiros, 2659 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        1,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.37855,
                        -50.8744,
                        null));

        places.add(
                createPlace(
                        3L,
                        "ChIJkT12uPczGZURcExGzF48AoI",
                        "Pórtico Gramado Estilo Bávaro",
                        null,
                        "(54) 3036-4050",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias - Portico, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        2,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.38969,
                        -50.88495,
                        null));

        places.add(
                createPlace(
                        4L,
                        "ChIJoW5_xxs2GZURGyI9uVrtAeY",
                        "Mini Mundo",
                        null,
                        "(54) 3286-4055",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Horácio Cardoso, 291 - Planalto, Gramado - RS, 95675-062",
                        "Gramado",
                        false,
                        3,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.38304,
                        -50.87568,
                        null));

        places.add(
                createPlace(
                        5L,
                        "ChIJWzOwRXM1GZURgCqEqfAbAiE",
                        "Garden Park Gramado",
                        null,
                        "(54) 3286-4051",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Estr. Profa. Elvira Apolo Benetti, 1699 - Jardim Bela Vista, Gramado - RS, 95679-899",
                        "Gramado",
                        false,
                        4,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.37264,
                        -50.85285,
                        null));

        places.add(
                createPlace(
                        6L,
                        "ChIJpQLdRlMzGZURHGysIJMbi3I",
                        "Lago Negro",
                        null,
                        "(54) 3286-4052",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Vinte e Cinco de Julho, 439 - Casa Grande, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        5,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.39351,
                        -50.87833,
                        null));

        places.add(
                createPlace(
                        7L,
                        "ChIJIYpuLR8xGZURIDT2A5S6Vog",
                        "Expogramado",
                        null,
                        "(54) 3286-4053",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. Borges de Medeiros, 4111 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        1,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.36883,
                        -50.88178,
                        null));

        places.add(
                createPlace(
                        8L,
                        "ChIJ-ZdZYBc1GZURaaRRs7WmIAg",
                        "Belvedere Vale do Quilombo",
                        null,
                        "(54) 3286-4054",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 2536 - Vila Suica, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        2,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.37812,
                        -50.86689,
                        null));

        places.add(
                createPlace(
                        9L,
                        "ChIJc9xQaBk1GZURAzZGz7paAeQ",
                        "NBA Park Gramado | Parque Temático",
                        null,
                        "(54) 3286-4056",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 4795 - Carniel, Gramado - RS, 95670-880",
                        "Gramado",
                        false,
                        3,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.35966,
                        -50.85485,
                        null));

        places.add(
                createPlace(
                        10L,
                        "ChIJYZJhix41GZURPZQ1hcAfIIo",
                        "Jardim do Amor",
                        null,
                        "(54) 3286-4057",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 765 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        4,
                        startDateRecommendation,
                        endDateRecommendation,
                        createdDate,
                        null,
                        null,
                        -29.38436,
                        -50.8815,
                        null));

        return places;
    }

    public static List<Place> generatePlaceListB() {
        List<Place> places = new ArrayList<>();

        places.add(
                createPlace(
                        1L,
                        "ChIJPQmNhEMyGZURxuHk44vIaIw",
                        "Hard Rock Cafe",
                        "cafeteria do Hard Rock café",
                        "(54) 3286-4040",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil",
                        "Gramado",
                        false,
                        2,
                        LocalDate.of(2023, 11, 17),
                        LocalDate.of(2023, 12, 17),
                        LocalDate.of(2023, 11, 17),
                        null,
                        null,
                        -29.3810171,
                        -50.8711053,
                        null));

        places.add(
                createPlace(
                        2L,
                        "ChIJsdUuyRc1GZURg7Hy1kfaAeU",
                        "Fonte do Amor Eterno",
                        null,
                        "(54) 3286-1055",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. Borges de Medeiros, 2659 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        1,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.37855,
                        -50.8744,
                        null));

        places.add(
                createPlace(
                        3L,
                        "ChIJkT12uPczGZURcExGzF48AoI",
                        "Pórtico Gramado Estilo Bávaro",
                        null,
                        "(54) 3036-4050",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias - Portico, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        2,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.38969,
                        -50.88495,
                        null));

        places.add(
                createPlace(
                        4L,
                        "ChIJoW5_xxs2GZURGyI9uVrtAeY",
                        "Mini Mundo",
                        null,
                        "(54) 3286-4055",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Horácio Cardoso, 291 - Planalto, Gramado - RS, 95675-062",
                        "Gramado",
                        false,
                        3,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.38304,
                        -50.87568,
                        null));

        places.add(
                createPlace(
                        5L,
                        "ChIJWzOwRXM1GZURgCqEqfAbAiE",
                        "Garden Park Gramado",
                        null,
                        "(54) 3286-4051",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Estr. Profa. Elvira Apolo Benetti, 1699 - Jardim Bela Vista, Gramado - RS, 95679-899",
                        "Gramado",
                        false,
                        4,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.37264,
                        -50.85285,
                        null));

        places.add(
                createPlace(
                        6L,
                        "ChIJpQLdRlMzGZURHGysIJMbi3I",
                        "Lago Negro",
                        null,
                        "(54) 3286-4052",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "R. Vinte e Cinco de Julho, 439 - Casa Grande, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        5,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.39351,
                        -50.87833,
                        null));

        places.add(
                createPlace(
                        7L,
                        "ChIJIYpuLR8xGZURIDT2A5S6Vog",
                        "Expogramado",
                        null,
                        "(54) 3286-4053",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. Borges de Medeiros, 4111 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        1,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.36883,
                        -50.88178,
                        null));

        places.add(
                createPlace(
                        8L,
                        "ChIJ-ZdZYBc1GZURaaRRs7WmIAg",
                        "Belvedere Vale do Quilombo",
                        null,
                        "(54) 3286-4054",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 2536 - Vila Suica, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        2,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.37812,
                        -50.86689,
                        null));

        places.add(
                createPlace(
                        9L,
                        "ChIJc9xQaBk1GZURAzZGz7paAeQ",
                        "NBA Park Gramado | Parque Temático",
                        null,
                        "(54) 3286-4056",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 4795 - Carniel, Gramado - RS, 95670-880",
                        "Gramado",
                        false,
                        3,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.35966,
                        -50.85485,
                        null));

        places.add(
                createPlace(
                        10L,
                        "ChIJYZJhix41GZURPZQ1hcAfIIo",
                        "Jardim do Amor",
                        null,
                        "(54) 3286-4057",
                        null,
                        null,
                        null,
                        "photoString",
                        new ArrayList<>(),
                        "Av. das Hortênsias, 765 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        false,
                        4,
                        LocalDate.of(2025, 2, 18),
                        LocalDate.of(2025, 3, 18),
                        LocalDate.of(2025, 2, 18),
                        null,
                        null,
                        -29.38436,
                        -50.8815,
                        null));

        return places;
    }
}
