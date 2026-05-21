package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.ReservationTime;
import roomescape.entity.ReservationTimeEntity;

public class ReservationTimeEntityRepositoryTest extends RepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Test
    void createTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(reservationTime);

        assertThat(reservationTimeEntity.getId()).isEqualTo(1L);
    }

    @Test
    void readTest() {
        String sql = "INSERT INTO `reservation_time` (`start_at`) VALUES (?)";
        jdbcTemplate.update(sql, "10:00");

        Optional<ReservationTimeEntity> reservationTime = reservationTimeRepository.read(1L);

        assertThat(reservationTime.orElseThrow().getId()).isEqualTo(1L);
    }

    @Test
    void readAllTest() {
        String sql = "INSERT INTO `reservation_time` (`start_at`) VALUES (?)";
        jdbcTemplate.update(sql, "10:00");
        jdbcTemplate.update(sql, "11:00");

        List<ReservationTimeEntity> reservationTimeEntities = reservationTimeRepository.readAll();
        assertThat(reservationTimeEntities.size()).isEqualTo(2);
    }

    @Test
    void deleteTest() {
        String insertReservationTimeSql = "INSERT INTO `reservation_time` (`start_at`) VALUES (?)";
        jdbcTemplate.update(insertReservationTimeSql, "10:00");

        reservationTimeRepository.delete(1L);

        String readAllReservationTimeCountSql = "SELECT COUNT(*) FROM `reservation_time`";
        int count = jdbcTemplate.queryForObject(readAllReservationTimeCountSql, Integer.class);

        assertThat(count).isEqualTo(0);
    }
}
