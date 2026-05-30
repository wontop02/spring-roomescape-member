package roomescape.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;

public class ReservationRepositoryTest extends RepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private ReservationTime reservationTime;
    private Theme theme;

    @BeforeEach
    void beforeEach() {
        String insertReservationTimeSql = "INSERT INTO `reservation_time` (`start_at`) VALUES (?)";
        jdbcTemplate.update(insertReservationTimeSql, "10:00");
        jdbcTemplate.update(insertReservationTimeSql, "11:00");
        jdbcTemplate.update(insertReservationTimeSql, "12:00");

        String insertThemeSql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertThemeSql, "방탈출1", "방탈출1 설명", "url.jpg");

        reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        theme = new Theme(1L, "방탈출1", "방탈출1 설명", "url.jpg");
    }

    @Test
    void createTest() {
        Reservation reservation = new Reservation("fizz", LocalDate.of(2026, 5, 2), reservationTime, theme);

        Reservation createdReservation = reservationRepository.create(reservation);

        assertThat(createdReservation.getId()).isEqualTo(1L);
    }

    @Test
    void readByIdTest() {
        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);

        Reservation reservation = reservationRepository.readById(1L).orElseThrow();

        assertThat(reservation.getName()).isEqualTo("fizz");
        assertThat(reservation.getDate()).isEqualTo(LocalDate.of(2026, 5, 2));
        assertThat(reservation.getTime().getId()).isEqualTo(1L);
        assertThat(reservation.getTheme().getId()).isEqualTo(1L);
    }

    @Test
    void readByNameTest() {
        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);
        jdbcTemplate.update(sql, "tree", "2026-05-02", 2L, 1L);
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 3L, 1L);

        Reservations reservations = reservationRepository.readByName("fizz");

        List<Reservation> reservationList = reservations.stream().toList();
        assertThat(reservationList.size()).isEqualTo(2);
        assertThat(reservationList.get(0).getName()).isEqualTo("fizz");
        assertThat(reservationList.get(1).getName()).isEqualTo("fizz");

        assertThat(reservationRepository.readByName("user").stream().toList().size()).isEqualTo(0);
    }

    @Test
    void readAllTest() {
        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 2L, 1L);

        Reservations reservations = reservationRepository.readAll();

        assertThat(reservations.stream().toList().size()).isEqualTo(2);
    }

    @Test
    void updateTest() {
        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);

        LocalDate newDate = LocalDate.now().plusDays(1);
        ReservationTime newReservationTime = new ReservationTime(2L, LocalTime.of(11, 0));
        Reservation updatedReservation = new Reservation(1L, "fizz", newDate, newReservationTime, theme);

        reservationRepository.update(updatedReservation);

        Reservation reservation = reservationRepository.readById(1L).orElseThrow();

        assertThat(reservation.getName()).isEqualTo("fizz");
        assertThat(reservation.getDate()).isEqualTo(newDate);
        assertThat(reservation.getTime().getId()).isEqualTo(2L);
        assertThat(reservation.getTheme().getId()).isEqualTo(1L);
    }

    @Test
    void deleteTest() {
        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);

        Reservation reservation = new Reservation(1L, "fizz", LocalDate.of(2026, 5, 2), reservationTime, theme);
        reservationRepository.delete(reservation);

        String readReservationCountSql = "SELECT COUNT(*) FROM `reservation`";
        int count = jdbcTemplate.queryForObject(readReservationCountSql, Integer.class);

        Assertions.assertThat(count).isEqualTo(0);
    }

    @Test
    void existByTimeIdTest() {
        boolean exist = reservationRepository.existByTimeId(1L);
        assertThat(exist).isFalse();

        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);

        exist = reservationRepository.existByTimeId(1L);
        assertThat(exist).isTrue();
    }

    @Test
    void existByThemeIdTest() {
        boolean exist = reservationRepository.existByThemeId(1L);
        assertThat(exist).isFalse();

        String sql = "INSERT INTO `reservation` (`name`, `date`, `time_id`, `theme_id`) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "fizz", "2026-05-02", 1L, 1L);

        exist = reservationRepository.existByThemeId(1L);
        assertThat(exist).isTrue();
    }
}
