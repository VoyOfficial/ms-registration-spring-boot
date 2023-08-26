package src.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class NearbyPlaces {

    private List<Place> places;
    private String nextTokenPage;

    public NearbyPlaces(List<Place> places, String nextTokenPage) {

        this.places = places;
        this.nextTokenPage = nextTokenPage;

    }

}
