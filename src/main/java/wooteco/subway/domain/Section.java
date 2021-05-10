package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.exception.section.InvalidDistanceException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public static Section create(Station upStation, Station downStation, int distance) {
        return create(null, upStation, downStation, distance);
    }

    public static Section create(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, upStation, downStation, distance);
    }


    public boolean isUpStation(Station targetStation) {
        return upStation.isSameId(targetStation.getId());
    }

    public boolean isDownStation(Station targetStation) {
        return downStation.isSameId(targetStation.getId());
    }

    public void updateUpStation(Section section) {
        int difference = distance - section.distance;

        if (difference <= 0) {
            throw new InvalidDistanceException();
        }
        upStation = section.downStation;
        distance = difference;
    }

    public void updateDownStation(Section section) {
        int difference = distance - section.distance;

        if (difference <= 0) {
            throw new InvalidDistanceException();
        }
        downStation = section.upStation;
        distance = difference;
    }

    public boolean isSameSection(Section newSection) {
        return (isUpStation(newSection.upStation) && isDownStation(newSection.downStation)) ||
                (isUpStation(newSection.downStation) && isDownStation(newSection.upStation));
    }

    public boolean hasStation(Long stationId) {
        return upStation.isSameId(stationId) || downStation.isSameId(stationId);
    }

    public boolean isUpStationId(Long stationId) {
        return upStation.isSameId(stationId);
    }
}
