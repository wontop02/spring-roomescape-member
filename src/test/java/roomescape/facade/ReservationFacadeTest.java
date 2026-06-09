package roomescape.facade;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceReservationCreateRequest;
import roomescape.service.dto.request.ServiceReservationUpdateRequest;
import roomescape.service.dto.response.ServiceReservationResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;
import roomescape.service.dto.response.ServiceThemeResponse;

public class ReservationFacadeTest {

    private ReservationFacade reservationFacade;

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

        ReservationService reservationService = new ReservationService(reservationRepository, clock);
        ReservationTimeService reservationTimeService = new ReservationTimeService(reservationTimeRepository);
        ThemeService themeService = new ThemeService(themeRepository);

        reservationFacade = new ReservationFacade(reservationService, reservationTimeService, themeService);
    }

    @Test
    void createTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        ServiceReservationResponse response = reservationFacade.create(
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), reservationTime.getId(),
                        theme.getId()));

        assertThat(response).isEqualTo(new ServiceReservationResponse(1L, "fizz", LocalDate.of(2026, 5, 3),
                ServiceReservationTimeResponse.from(reservationTime),
                ServiceThemeResponse.from(theme)));
    }

    @Test
    void readAllTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        reservationFacade.create(
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), reservationTime.getId(),
                        theme.getId()));
        reservationFacade.create(
                new ServiceReservationCreateRequest("fizz2", LocalDate.of(2026, 5, 4), reservationTime.getId(),
                        theme.getId()));

        List<ServiceReservationResponse> responses = reservationFacade.readAll();

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).name()).isEqualTo("fizz");
        assertThat(responses.get(1).name()).isEqualTo("fizz2");
    }

    @Test
    void readByNameTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        reservationFacade.create(
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), reservationTime.getId(),
                        theme.getId()));
        reservationFacade.create(
                new ServiceReservationCreateRequest("tree", LocalDate.of(2026, 5, 4), reservationTime.getId(),
                        theme.getId()));

        List<ServiceReservationResponse> responses = reservationFacade.readByName("fizz");

        assertThat(responses.size()).isEqualTo(1);
        assertThat(responses.get(0).name()).isEqualTo("fizz");
    }

    @Test
    void updateTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        ReservationTime newReservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        reservationFacade.create(
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), reservationTime.getId(),
                        theme.getId()));

        ServiceReservationResponse response = reservationFacade.update(1L,
                new ServiceReservationUpdateRequest(LocalDate.of(2026, 5, 4), newReservationTime.getId()));

        assertThat(response.date()).isEqualTo(LocalDate.of(2026, 5, 4));
        assertThat(response.time().id()).isEqualTo(newReservationTime.getId());
    }

    @Test
    void deleteTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        reservationFacade.create(
                new ServiceReservationCreateRequest("fizz", LocalDate.of(2026, 5, 3), reservationTime.getId(),
                        theme.getId()));

        reservationFacade.delete(1L);

        List<ServiceReservationResponse> responses = reservationFacade.readAll();
        assertThat(responses.size()).isEqualTo(0);
    }
}
