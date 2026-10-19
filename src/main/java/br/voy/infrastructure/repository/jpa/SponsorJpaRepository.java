package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.SponsorModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorJpaRepository extends JpaRepository<SponsorModel, Long> {

    Optional<SponsorModel> findByNameKeyAndCityKey(String nameKey, String cityKey);
}
