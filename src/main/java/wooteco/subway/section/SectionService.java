package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.section.SectionLastRemainedException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dao.SectionTable;
import wooteco.subway.section.web.SectionRequest;
import wooteco.subway.section.web.SectionResponse;
import wooteco.subway.station.StationService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;

    @Transactional
    public void createInitial(Section section, Long lineId) {
        sectionDao.create(section, lineId);
    }

    @Transactional
    public SectionResponse create(SectionRequest sectionRequest, Long lineId) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Section newSection = Section.create(upStation, downStation, sectionRequest.getDistance());

        Sections sections = findAllByLineId(lineId);

        Section modified = sections.modifyRelatedToAdd(newSection);
        sections.add(newSection);

        sectionDao.updateModified(modified);
        Section section = sectionDao.create(newSection, lineId);

        return SectionResponse.create(section);
    }

    @Transactional
    public void removeByLineAndStationIds(Long lineId, Long stationId) {
        validateRemovable(lineId, stationId);

        Station station = stationService.findById(stationId);
        Sections sections = findAllByLineId(lineId);

        List<Section> removed = sections.removeRelated(station);
        Section modified = sections.reflectRemoved(removed, station);

        for (Section section : removed) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();
            sectionDao.remove(lineId, upStationId, downStationId);
        }
        sectionDao.create(modified, lineId);
    }

    private void validateRemovable(Long lineId, Long stationId) {
        stationService.validateExistStation(stationId);
        validateIsLastRemainedSection(lineId);
    }

    private void validateIsLastRemainedSection(Long lineId) {
        if (findAllByLineId(lineId).hasSizeOf(1)) {
            throw new SectionLastRemainedException();
        }
    }

    public Sections findAllByLineId(Long lineId) {
        List<SectionTable> sectionTables = sectionDao.findAllByLineId(lineId);
        List<Section> sections = convertToSections(sectionTables);
        return Sections.create(sections);
    }

    private List<Section> convertToSections(List<SectionTable> sectionTables) {
        List<Section> sections = new ArrayList<>();
        for (SectionTable sectionTable : sectionTables) {
            Station upStation = stationService.findById(sectionTable.getUpStationId());
            Station downStation = stationService.findById(sectionTable.getDownStationId());
            sections.add(Section.create(sectionTable.getId(), upStation, downStation, sectionTable.getDistance()));
        }
        return sections;
    }

}
