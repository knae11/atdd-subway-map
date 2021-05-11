package wooteco.subway.station.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Station save(Station station) {
        String sql = "INSERT INTO station (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);

        return Station.create(keyHolder.getKey().longValue(), station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return this.jdbcTemplate.query(sql, stationRowMapper());
    }

    private RowMapper<Station> stationRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return Station.create(id, name);
        };
    }

    @Override
    public Optional<Station> findStationById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        return this.jdbcTemplate.query(sql, stationRowMapper(), id).stream().findAny();
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        String sql = "SELECT * FROM station WHERE name = ?";
        return this.jdbcTemplate.query(sql, stationRowMapper(), name).stream().findAny();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        this.jdbcTemplate.update(sql, id);
    }
}