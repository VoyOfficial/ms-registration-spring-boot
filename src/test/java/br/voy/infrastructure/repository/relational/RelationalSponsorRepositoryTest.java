package br.voy.infrastructure.repository.relational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.infrastructure.model.SponsorModel;
import br.voy.infrastructure.repository.jpa.SponsorJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelationalSponsorRepositoryTest {

    @Mock private SponsorJpaRepository jpaRepository;

    @InjectMocks private RelationalSponsorRepository repository;

    @Test
    @DisplayName("Should find a sponsor by normalized name and city")
    void shouldFindASponsorByNormalizedNameAndCity() {
        var model =
                SponsorModel.builder()
                        .name("Hard Rock Cafe")
                        .city("Gramado")
                        .approved(true)
                        .subscriptionActive(true)
                        .build();
        when(jpaRepository.findByNameKeyAndCityKey("hard rock cafe", "gramado"))
                .thenReturn(Optional.of(model));

        var sponsor = repository.findByNameAndCity("  Hard   Rock Cafe ", " Gramado ");

        assertTrue(sponsor.isPresent());
        assertEquals("Hard Rock Cafe", sponsor.get().getName());
        assertTrue(sponsor.get().isApproved());
        assertTrue(sponsor.get().isSubscriptionActive());
    }

    @Test
    @DisplayName("Should skip the lookup when name or city is blank")
    void shouldSkipTheLookupWhenNameOrCityIsBlank() {
        assertTrue(repository.findByNameAndCity("  ", "Gramado").isEmpty());
        assertTrue(repository.findByNameAndCity("Hard Rock Cafe", null).isEmpty());

        verify(jpaRepository, never()).findByNameKeyAndCityKey(any(), any());
    }
}
