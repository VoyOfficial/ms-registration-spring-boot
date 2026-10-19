package br.voy.infrastructure.model;

import br.voy.domain.entity.Sponsor;
import br.voy.infrastructure.sponsor.SponsorKeyNormalizer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "sponsor",
        schema = "registration",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_sponsor_name_city",
                        columnNames = {"name_key", "city_key"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SponsorModel extends AbstractModel<Sponsor> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String city;

    @Column(name = "name_key", nullable = false, length = 255)
    private String nameKey;

    @Column(name = "city_key", nullable = false, length = 255)
    private String cityKey;

    @Column(nullable = false)
    private boolean approved;

    @Column(name = "subscription_active", nullable = false)
    private boolean subscriptionActive;

    private Integer ranking;

    @Column(name = "google_place_id", length = 255)
    private String googlePlaceId;

    @Column(length = 500)
    private String notes;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void applyKeys() {
        this.nameKey = SponsorKeyNormalizer.normalize(name);
        this.cityKey = SponsorKeyNormalizer.normalize(city);
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public Sponsor toDomain() {
        return Sponsor.builder()
                .name(name)
                .city(city)
                .approved(approved)
                .subscriptionActive(subscriptionActive)
                .build();
    }
}
