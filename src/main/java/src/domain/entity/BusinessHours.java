package src.domain.entity;

import com.google.maps.model.OpeningHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Builder
@AllArgsConstructor
public class BusinessHours {

    public static final int QTD_DAYS = 7;
    public static final String REGEX_TO_FILTER_DAYS_AND_HOURS = "([A-Za-z]+):\\s*((\\d{1,2}:\\d{2}\\s*[AP]M)\\s*–\\s*(\\d{1,2}:\\d{2}\\s*[AP]M)|Open 24 hours|Closed)";

    private String day;
    private Interval interval;

    public static List<BusinessHours> toBusinessHoursList(OpeningHours openingHours) {

        if (Objects.isNull(openingHours) || Objects.isNull(openingHours.weekdayText) || openingHours.weekdayText.length != QTD_DAYS)
            return List.of();

        Map<String, Interval> dayAndIntervals = parseOpeningHours(openingHours);
        List<BusinessHours> businessHoursList = new ArrayList<>();

        dayAndIntervals.forEach((day, interval) -> {
            businessHoursList.add(
                    BusinessHours
                            .builder()
                            .day(day)
                            .interval(interval)
                            .build());
        });

        return businessHoursList;

    }

    public static Map<String, Interval> parseOpeningHours(OpeningHours openingHours) {

        Map<String, Interval> daysAndIntervals = new HashMap<>();

        Pattern pattern = Pattern.compile(REGEX_TO_FILTER_DAYS_AND_HOURS);

        Arrays.stream(openingHours.weekdayText).forEach(day -> {

            Matcher matcher = pattern.matcher(day.replace(" ", " ").replace(" ", " "));

            if (matcher.find()) {
                String dayOfWeek = matcher.group(1);
                Interval interval = parseInterval(matcher.group(2));
                daysAndIntervals.put(dayOfWeek, interval);
            }

        });

        return daysAndIntervals;
    }

    private static Interval parseInterval(String openingHoursText) {

        switch (openingHoursText) {
            case "Open 24 hours":
                return new Interval("12:00 AM", "11:59 PM");
            case "Closed":
                return new Interval("Closed", "Closed");
            default:
                String[] startAndEnd = openingHoursText.split("–");
                String start = startAndEnd[0].trim();
                String end = startAndEnd[1].trim();

                return new Interval(start, end);
        }

    }

}
