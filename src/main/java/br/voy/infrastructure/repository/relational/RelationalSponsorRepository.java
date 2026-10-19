package br.voy.infrastructure.repository.relational;

import br.voy.domain.entity.Sponsor;
import br.voy.domain.repository.SponsorRepository;
import br.voy.infrastructure.model.SponsorModel;
import br.voy.infrastructure.repository.jpa.SponsorJpaRepository;
import br.voy.infrastructure.sponsor.SponsorKeyNormalizer;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RelationalSponsorRepository implements SponsorRepository {

    private final Logger logger = LoggerFactory.getLogger(RelationalSponsorRepository.class);

    private final SponsorJpaRepository jpaRepository;

    public RelationalSponsorRepository(SponsorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Sponsor> findByNameAndCity(String name, String city) {
        String nameKey = SponsorKeyNormalizer.normalize(name);
        String cityKey = SponsorKeyNormalizer.normalize(city);
        if (nameKey.isEmpty() || cityKey.isEmpty()) {
            logger.info("RELATIONAL SPONSOR REPOSITORY - FIND BY NAME AND CITY - blank key");
            return Optional.empty();
        }

        logger.info("RELATIONAL SPONSOR REPOSITORY - FIND BY NAME AND CITY");
        return jpaRepository.findByNameKeyAndCityKey(nameKey, cityKey).map(SponsorModel::toDomain);
    }
}
