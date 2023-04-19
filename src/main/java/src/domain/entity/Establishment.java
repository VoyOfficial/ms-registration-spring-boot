package src.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode
public class Establishment {

    private String name;
    private String Address;
    private Map<String, String> businessHours;
    private Map<String, String> contact;
    private Integer rating;
    private Boolean saved;
    private List<String> comments;
    private Set<String> imagesUrl;

}
