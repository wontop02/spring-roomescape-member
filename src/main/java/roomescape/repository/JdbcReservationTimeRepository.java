package roomescape.repository;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.ReservationTime;
import roomescape.entity.ReservationTimeEntity;

@Primary
@Repository
public class JdbcReservationTimeRepository implements ReservationTimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationTimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReservationTimeEntity create(ReservationTime reservationTime) {
        String sql = "INSERT INTO `reservation_time`(`start_at`) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setTime(1, Time.valueOf(reservationTime.getStartAt()));

            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new ReservationTimeEntity(id, reservationTime.getStartAt());
    }

    @Override
    public Optional<ReservationTimeEntity> read(Long id) {
        String sql = "SELECT * FROM `reservation_time` WHERE `id` = (?)";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
                        LocalTime startAt = resultSet.getTime("start_at").toLocalTime();
                        return new ReservationTimeEntity(id, startAt);
                    }, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReservationTimeEntity> readAll() {
        String sql = "SELECT * FROM `reservation_time` "
                + "ORDER BY start_at ASC";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Long id = resultSet.getLong("id");
            LocalTime startAt = resultSet.getTime("start_at").toLocalTime();
            return new ReservationTimeEntity(id, startAt);
        });
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM `reservation_time` WHERE `id` = (?)";
        jdbcTemplate.update(sql, id);
    }
}
