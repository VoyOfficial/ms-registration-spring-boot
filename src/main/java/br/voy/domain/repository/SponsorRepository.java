package br.voy.domain.repository;

import br.voy.domain.entity.Sponsor;
import java.util.Optional;

public interface SponsorRepository {

    Optional<Sponsor> findByNameAndCity(String name, String city);
}
