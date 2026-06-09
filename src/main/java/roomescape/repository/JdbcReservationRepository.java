package roomescape.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;

@Primary
@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reservation create(Reservation reservation) {
        String sql = "INSERT INTO `reservation`(`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, reservation.getName());
            preparedStatement.setDate(2, Date.valueOf(reservation.getDate()));
            preparedStatement.setLong(3, reservation.getTime().getId());
            preparedStatement.setLong(4, reservation.getTheme().getId());

            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Reservation(id, reservation.getName(), reservation.getDate(), reservation.getTime(),
                reservation.getTheme());
    }

    @Override
    public Optional<Reservation> readById(Long id) {
        String sql =
                "SELECT r.id, r.name, r.date, t.id as time_id, t.start_at as time_value, th.id as theme_id, th.name as theme_name, th.description as theme_description, th.thumbnail_url as theme_thumbnail_url "
                        + "FROM `reservation` r "
                        + "INNER JOIN `reservation_time` t ON r.time_id = t.id "
                        + "INNER JOIN `theme` th ON r.theme_id = th.id "
                        + "WHERE r.id = (?)";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, reservationRowMapper(), id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Reservations readByName(String name) {
        String sql =
                "SELECT r.id, r.name, r.date, t.id as time_id, t.start_at as time_value, th.id as theme_id, th.name as theme_name, th.description as theme_description, th.thumbnail_url as theme_thumbnail_url "
                        + "FROM `reservation` r "
                        + "INNER JOIN `reservation_time` t ON r.time_id = t.id "
                        + "INNER JOIN `theme` th ON r.theme_id = th.id "
                        + "WHERE r.name = (?)";

        return new Reservations(jdbcTemplate.query(sql, reservationRowMapper(), name));
    }

    @Override
    public Reservations readAll() {
        String sql =
                "SELECT r.id, r.name, r.date, t.id as time_id, t.start_at as time_value, th.id as theme_id, th.name as theme_name, th.description as theme_description, th.thumbnail_url as theme_thumbnail_url "
                        + "FROM `reservation` r "
                        + "INNER JOIN `reservation_time` t ON r.time_id = t.id "
                        + "INNER JOIN `theme` th ON r.theme_id = th.id";

        return new Reservations(jdbcTemplate.query(sql, reservationRowMapper()));
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE `reservation` SET `date` = (?), `time_id` = (?) WHERE `id` = (?)";

        jdbcTemplate.update(sql, reservation.getDate(), reservation.getTime().getId(), reservation.getId());
    }

    private static RowMapper<Reservation> reservationRowMapper() {
        return (resultSet, rowNum) -> {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            LocalDate date = resultSet.getDate("date").toLocalDate();
            Long timeId = resultSet.getLong("time_id");
            LocalTime timeValue = resultSet.getTime("time_value").toLocalTime();
            Long themeId = resultSet.getLong("theme_id");
            String themeName = resultSet.getString("theme_name");
            String themeDescription = resultSet.getString("theme_description");
            String themeThumbnailUrl = resultSet.getString("theme_thumbnail_url");

            ReservationTime reservationTime = new ReservationTime(timeId, timeValue);
            Theme theme = new Theme(themeId, themeName, themeDescription, themeThumbnailUrl);
            return new Reservation(id, name, date, reservationTime, theme);
        };
    }

    @Override
    public void delete(Reservation reservation) {
        String sql = "DELETE FROM `reservation` WHERE `id` = (?)";

        jdbcTemplate.update(sql, reservation.getId());
    }

    @Override
    public boolean existByTimeId(Long timeId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM `reservation` WHERE `time_id` = (?)) AS exist";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, timeId));
    }

    @Override
    public boolean existByThemeId(Long themeId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM `reservation` WHERE `theme_id` = (?)) AS exist";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, themeId));
    }

    @Override
    public boolean existBySlot(LocalDate date, Long timeId, Long themeId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM `reservation` WHERE `date` = (?) AND `time_id` = (?) AND `theme_id` = (?)) AS exist";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, date, timeId, themeId));
    }
}
