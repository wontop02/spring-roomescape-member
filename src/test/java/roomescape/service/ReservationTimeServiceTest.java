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
import roomescape.domain.ReservationTimes;
import roomescape.domain.Theme;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceReservationTimeCreateRequest;
import roomescape.service.dto.response.ServiceReservationTimeAvailabilityResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;

public class ReservationTimeServiceTest {

    private ReservationTimeService reservationTimeService;

    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ThemeRepository themeRepository;
    private ReservationTimes reservationTimes;
    private Clock clock;

    @BeforeEach
    void beforeEach() {
        FakeDatabase fakeDatabase = new FakeDatabase();

        reservationRepository = new FakeReservationRepository(fakeDatabase);
        reservationTimeRepository = new FakeReservationTimeRepository(fakeDatabase);
        themeRepository = new FakeThemeRepository(fakeDatabase);
        clock = Clock.fixed(Instant.parse("2026-05-02T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        reservationTimes = new ReservationTimes(reservationTimeRepository.readAll());

        reservationTimeService = new ReservationTimeService(reservationTimeRepository, themeRepository,
                reservationRepository, clock);
    }

    @Test
    void createTest() {
        ServiceReservationTimeResponse response = reservationTimeService.create(reservationTimes,
                new ServiceReservationTimeCreateRequest(LocalTime.of(10, 0)));

        assertThat(response).isEqualTo(new ServiceReservationTimeResponse(1L, LocalTime.of(10, 0)));
    }

    @Test
    void readAllTest() {
        reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        reservationTimeRepository.create(new ReservationTime(LocalTime.of(11, 0)));

        List<ServiceReservationTimeResponse> responses = reservationTimeService.readAll();

        assertThat(responses.getFirst()).isEqualTo(new ServiceReservationTimeResponse(1L, LocalTime.of(10, 0)));
        assertThat(responses.get(1)).isEqualTo(new ServiceReservationTimeResponse(2L, LocalTime.of(11, 0)));
    }

    @Test
    void readAvailabilityByDateAndThemeTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        reservationTimeRepository.create(new ReservationTime(LocalTime.of(11, 0)));
        ThemeEntity themeEntity = themeRepository.create(new Theme("방탈출1", "방탈출1 설명", "url.jpg"));

        Reservation reservation = new Reservation("fizz", LocalDate.now(clock), reservationTimeEntity.toDomain(),
                themeEntity.toDomain());
        reservationRepository.create(reservation, reservationTimeEntity, themeEntity);

        List<ServiceReservationTimeAvailabilityResponse> responses = reservationTimeService.readAvailabilityByDateAndTheme(
                LocalDate.now(clock), themeEntity.getId());

        assertThat(responses.get(0).available()).isFalse();
        assertThat(responses.get(1).available()).isTrue();
    }

    @Test
    void deleteTest() {
        reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        reservationTimeService.delete(1L);

        List<ReservationTimeEntity> reservationTimeEntities = reservationTimeRepository.readAll();

        assertThat(reservationTimeEntities.size()).isEqualTo(0);
    }
}
