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
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceReservationCreateRequest;
import roomescape.service.dto.response.ServiceReservationResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;
import roomescape.service.dto.response.ServiceThemeResponse;

public class ReservationServiceTest {

    private ReservationService reservationService;

    private ReservationRepository reservationRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ThemeRepository themeRepository;
    private Reservations reservations;
    private Clock clock;

    @BeforeEach
    void beforeEach() {
        FakeDatabase fakeDatabase = new FakeDatabase();

        reservationRepository = new FakeReservationRepository(fakeDatabase);
        reservationTimeRepository = new FakeReservationTimeRepository(fakeDatabase);
        themeRepository = new FakeThemeRepository(fakeDatabase);
        clock = Clock.fixed(Instant.parse("2026-05-02T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        reservations = new Reservations(reservationRepository.readAll());

        reservationService = new ReservationService(reservationRepository, reservationTimeRepository, themeRepository,
                clock);
    }

    @Test
    void createTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        ServiceReservationTimeResponse serviceReservationTimeResponse = ServiceReservationTimeResponse.from(
                reservationTimeEntity);
        ThemeEntity themeEntity = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        ServiceThemeResponse serviceThemeResponse = ServiceThemeResponse.from(themeEntity);

        ServiceReservationResponse responseDto = reservationService.create(reservations,
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), 1L, 1L));

        assertThat(responseDto).isEqualTo(
                new ServiceReservationResponse(1L, "fizz", LocalDate.of(2026, 5, 3),
                        serviceReservationTimeResponse,
                        serviceThemeResponse));
    }

    @Test
    void readAllTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        ServiceReservationTimeResponse serviceReservationTimeResponse = ServiceReservationTimeResponse.from(
                reservationTimeEntity);
        ThemeEntity themeEntity = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        ServiceThemeResponse serviceThemeResponse = ServiceThemeResponse.from(themeEntity);

        reservationService.create(reservations,
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), 1L, 1L));
        reservationService.create(reservations,
                new ServiceReservationCreateRequest("fizz2", LocalDate.of(2026, 5, 3).plusDays(1), 1L, 1L));

        List<ServiceReservationResponse> response = reservationService.readAll();

        assertThat(response.getFirst()).isEqualTo(
                new ServiceReservationResponse(response.getFirst().id(), "fizz", LocalDate.of(2026, 5, 3),
                        serviceReservationTimeResponse,
                        serviceThemeResponse));
        assertThat(response.get(1)).isEqualTo(
                new ServiceReservationResponse(response.get(1).id(), "fizz2",
                        LocalDate.of(2026, 5, 3).plusDays(1),
                        serviceReservationTimeResponse,
                        serviceThemeResponse));
    }

    @Test
    void deleteTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        ThemeEntity themeEntity = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        Reservation reservation = new Reservation("fizz", LocalDate.of(2026, 5, 3), reservationTimeEntity.toDomain(),
                themeEntity.toDomain());
        reservationRepository.create(reservation, reservationTimeEntity, themeEntity);
        reservationService.delete(reservations, 1L);

        List<ServiceReservationResponse> response = reservationService.readAll();

        assertThat(response.size()).isEqualTo(0);
    }
}
