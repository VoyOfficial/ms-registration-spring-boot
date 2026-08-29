package br.voy.testsupport;

import br.voy.infrastructure.model.PlaceModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import java.time.LocalDate;
import java.util.List;

/** Test seed data from GitHub issue #31 (recommended places in Gramado/Canela). */
public final class RecommendedPlacesTestData {

    public static final double GRAMADO_LATITUDE = -29.37855;
    public static final double GRAMADO_LONGITUDE = -50.8744;

    private static final LocalDate ACTIVE_START = LocalDate.of(2025, 2, 18);
    private static final LocalDate ACTIVE_END = LocalDate.of(2026, 12, 31);

    private RecommendedPlacesTestData() {}

    public static void seed(PlaceJpaRepository repository) {
        repository.deleteAll();
        repository.saveAll(buildIssue31Places());
    }

    public static List<PlaceModel> buildIssue31Places() {
        return List.of(
                expiredPlace(
                        "Hard Rock Cafe",
                        "ChIJPQmNhEMyGZURxuHk44vIaIw",
                        "(54) 3286-4040",
                        "R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil",
                        "Gramado",
                        2,
                        -29.3810171,
                        -50.8711053),
                activePlace(
                        "Fonte do Amor Eterno",
                        "ChIJsdUuyRc1GZURg7Hy1kfaAeU",
                        "(54) 3286-1055",
                        "Av. Borges de Medeiros, 2659 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        1,
                        -29.37855,
                        -50.8744),
                activePlace(
                        "Pórtico Gramado Estilo Bávaro",
                        "ChIJkT12uPczGZURcExGzF48AoI",
                        "(54) 3036-4050",
                        "Av. das Hortênsias - Portico, Gramado - RS, 95670-000",
                        "Gramado",
                        2,
                        -29.38969,
                        -50.88495),
                activePlace(
                        "Mini Mundo",
                        "ChIJoW5_xxs2GZURGyI9uVrtAeY",
                        "(54) 3286-4055",
                        "R. Horácio Cardoso, 291 - Planalto, Gramado - RS, 95675-062",
                        "Gramado",
                        3,
                        -29.38304,
                        -50.87568),
                activePlace(
                        "Garden Park Gramado",
                        "ChIJWzOwRXM1GZURgCqEqfAbAiE",
                        "(54) 3286-4051",
                        "Estr. Profa. Elvira Apolo Benetti, 1699 - Jardim Bela Vista, Gramado - RS, 95679-899",
                        "Gramado",
                        4,
                        -29.37264,
                        -50.85285),
                activePlace(
                        "Lago Negro",
                        "ChIJpQLdRlMzGZURHGysIJMbi3I",
                        "(54) 3286-4052",
                        "R. Vinte e Cinco de Julho, 439 - Casa Grande, Gramado - RS, 95670-000",
                        "Gramado",
                        5,
                        -29.39351,
                        -50.87833),
                activePlace(
                        "Expogramado",
                        "ChIJIYpuLR8xGZURIDT2A5S6Vog",
                        "(54) 3286-4053",
                        "Av. Borges de Medeiros, 4111 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        1,
                        -29.36883,
                        -50.88178),
                activePlace(
                        "Belvedere Vale do Quilombo",
                        "ChIJ-ZdZYBc1GZURaaRRs7WmIAg",
                        "(54) 3286-4054",
                        "Av. das Hortênsias, 2536 - Vila Suica, Gramado - RS, 95670-000",
                        "Gramado",
                        2,
                        -29.37812,
                        -50.86689),
                activePlace(
                        "NBA Park Gramado | Parque Temático",
                        "ChIJc9xQaBk1GZURAzZGz7paAeQ",
                        "(54) 3286-4056",
                        "Av. das Hortênsias, 4795 - Carniel, Gramado - RS, 95670-880",
                        "Gramado",
                        3,
                        -29.35966,
                        -50.85485),
                activePlace(
                        "Jardim do Amor",
                        "ChIJYZJhix41GZURPZQ1hcAfIIo",
                        "(54) 3286-4057",
                        "Av. das Hortênsias, 765 - Centro, Gramado - RS, 95670-000",
                        "Gramado",
                        4,
                        -29.38436,
                        -50.8815),
                activePlace(
                        "Chocolate Gramadense - Fábrica",
                        "ChIJCXYmbCY1GZURMMaJctqNAgQ",
                        "(54) 3286-4058",
                        "R. Pref. Waldemar Frederico Weber, 365 - Floresta, Gramado - RS, 95670-000",
                        "Gramado",
                        5,
                        -29.37138,
                        -50.88661),
                activePlace(
                        "Space Adventure Canela",
                        "ChIJSpaceAdvCanelaTest01",
                        "(54) 3286-1055",
                        "Av. Ernani Kroeff Fleck, 960 - Vila Suica, Canela - RS, 95684-180",
                        "Canela",
                        1,
                        -29.35963,
                        -50.83543));
    }

    private static PlaceModel expiredPlace(
            String name,
            String googlePlaceId,
            String contact,
            String address,
            String city,
            int ranking,
            double latitude,
            double longitude) {
        return basePlace(
                name,
                googlePlaceId,
                contact,
                address,
                city,
                ranking,
                latitude,
                longitude,
                LocalDate.of(2023, 11, 17),
                LocalDate.of(2023, 12, 17),
                true);
    }

    private static PlaceModel activePlace(
            String name,
            String googlePlaceId,
            String contact,
            String address,
            String city,
            int ranking,
            double latitude,
            double longitude) {
        return basePlace(
                name,
                googlePlaceId,
                contact,
                address,
                city,
                ranking,
                latitude,
                longitude,
                ACTIVE_START,
                ACTIVE_END,
                true);
    }

    private static PlaceModel basePlace(
            String name,
            String googlePlaceId,
            String contact,
            String address,
            String city,
            int ranking,
            double latitude,
            double longitude,
            LocalDate startRecommendation,
            LocalDate endRecommendation,
            boolean status) {
        return PlaceModel.builder()
                .name(name)
                .googlePlaceId(googlePlaceId)
                .about("")
                .contact(contact)
                .address(address)
                .city(city)
                .state("RS")
                .rating(4.5f)
                .userRatingsTotal(1000)
                .principalPhoto("")
                .latitude(latitude)
                .longitude(longitude)
                .ranking(ranking)
                .startRecommendation(startRecommendation)
                .endRecommendation(endRecommendation)
                .createdDate(startRecommendation)
                .status(status)
                .distanceOfLocal(0.0f)
                .build();
    }
}
