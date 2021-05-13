package wooteco.subway.section.web;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.web.LineRequest;
import wooteco.subway.station.StationRequest;
import wooteco.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DisplayName("[API] 구간관련 테스트")
class SectionApiController222Test extends AcceptanceTest {
    private static final StationRequest 잠실역 = new StationRequest("잠실역");
    private static final StationRequest 잠실새내역 = new StationRequest("잠실새내역");
    private static final StationRequest 강남역 = new StationRequest("강남역");
    private static final StationRequest 동탄역 = new StationRequest("동탄역");
    private static final StationRequest 수서역 = new StationRequest("수서역");
    private static final int ORIGINAL_DISTANCE = 10;

    @Test
    @DisplayName("구간 등록 - 성공(상행종점 등록)")
    void create_성공_상행종점추가() {
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 강남_잠실 = new SectionRequest(강남역_id, 잠실역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(강남_잠실, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());


        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("강남역", "잠실역", "잠실새내역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행종점 등록)")
    void create_성공_하행종점추가() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 잠실새내_강남 = new SectionRequest(잠실새내_id, 강남역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(잠실새내_강남, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "잠실새내역", "강남역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행기준 중간구간 구간 등록)")
    void create_성공_중간역상행기준() throws Exception {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);
        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 잠실_강남 = new SectionRequest(잠실역_id, 강남역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(잠실_강남, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "강남역", "잠실새내역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행기준 중간구간 구간 등록)")
    void create_성공_중간하행기준() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 강남_잠실새내 = new SectionRequest(강남역_id, 잠실새내_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(강남_잠실새내, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "강남역", "잠실새내역");
    }

    //todo: 실패
    @Test
    @DisplayName("구간 등록 - 성공(중간 구간 등록 a-b-c-d --(b-k)--> a-b-k-c-d)")
    void create_성공_중간앞() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        Long 수서역_id = postStationAndGetId(수서역);
        Long 동탄역_id = postStationAndGetId(동탄역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        postSection(new SectionRequest(강남역_id, 잠실역_id, 4), lineId);
        postSection(new SectionRequest(잠실새내_id, 동탄역_id, 4), lineId);

        // when
        ExtractableResponse<Response> result = postSection(new SectionRequest(잠실역_id, 수서역_id, 2), lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(5);
        assertThat(stationsResult).containsExactly("강남역", "잠실역", "수서역", "잠실새내역", "동탄역");
    }
}
