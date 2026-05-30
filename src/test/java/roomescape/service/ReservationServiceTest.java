package roomescape.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

public class ReservationServiceTest {

    private ReservationService reservationService;

    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ThemeRepository themeRepository;
    private Clock clock;

    @BeforeEach
    void beforeEach() {
        FakeDatabase fakeDatabase = new FakeDatabase();

        reservationRepository = new FakeReservationRepository(fakeDatabase);
        reservationTimeRepository = new FakeReservationTimeRepository(fakeDatabase);
        themeRepository = new FakeThemeRepository(fakeDatabase);
        clock = Clock.fixed(Instant.parse("2026-05-02T00:00:00Z"), ZoneId.of("Asia/Seoul"));

        reservationService = new ReservationService(reservationRepository, clock);
    }

    @Test
    void createTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        Reservation reservationWithoutId = new Reservation("fizz", LocalDate.of(2026, 5, 3), reservationTime, theme);
        Reservation reservation = reservationService.create(reservationWithoutId);

        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getName()).isEqualTo("fizz");
        assertThat(reservation.getDate()).isEqualTo(LocalDate.of(2026, 5, 3));
    }

    @Test
    void readAllTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        reservationService.create(new Reservation("fizz", LocalDate.of(2026, 5, 3), reservationTime, theme));
        reservationService.create(new Reservation("fizz2", LocalDate.of(2026, 5, 4), reservationTime, theme));

        Reservations reservations = reservationService.readAll();

        List<Reservation> reservationList = reservations.stream().toList();
        assertThat(reservationList.size()).isEqualTo(2);
        assertThat(reservationList.get(0).getName()).isEqualTo("fizz");
        assertThat(reservationList.get(1).getName()).isEqualTo("fizz2");
    }

    @Test
    void deleteTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        reservationService.create(new Reservation("fizz", LocalDate.of(2026, 5, 3), reservationTime, theme));

        reservationService.delete(1L);

        Reservations reservations = reservationService.readAll();
        assertThat(reservations.stream().toList().size()).isEqualTo(0);
    }
}
