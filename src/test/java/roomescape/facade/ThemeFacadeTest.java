package roomescape.facade;

import static org.assertj.core.api.Assertions.assertThat;

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
import roomescape.domain.Theme;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ReservationService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

public class ThemeFacadeTest {

    private ThemeFacade themeFacade;

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
        ThemeService themeService = new ThemeService(themeRepository);

        themeFacade = new ThemeFacade(themeService, reservationService, clock);
    }

    @Test
    void createTest() {
        ServiceThemeResponse response = themeFacade.create(
                new ServiceThemeCreateRequest("피즈의 모험", "모험 이야기", "url.jpg"));

        assertThat(response).isEqualTo(new ServiceThemeResponse(1L, "피즈의 모험", "모험 이야기", "url.jpg"));
    }

    @Test
    void readAllTest() {
        themeFacade.create(new ServiceThemeCreateRequest("피즈의 모험", "모험 이야기", "url.jpg"));
        themeFacade.create(new ServiceThemeCreateRequest("나무의 일대기", "모험 이야기", "url.jpg"));

        List<ServiceThemeResponse> responses = themeFacade.readAll();

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0)).isEqualTo(new ServiceThemeResponse(1L, "피즈의 모험", "모험 이야기", "url.jpg"));
        assertThat(responses.get(1)).isEqualTo(new ServiceThemeResponse(2L, "나무의 일대기", "모험 이야기", "url.jpg"));
    }

    @Test
    void readRankingTest() {
        ReservationTime reservationTime = reservationTimeRepository.create(new ReservationTime(LocalTime.of(10, 0)));
        Theme theme1 = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        Theme theme2 = themeRepository.create(new Theme("피즈의 모험2", "모험 이야기", "url.jpg"));

        reservationRepository.create(
                new Reservation("fizz", LocalDate.of(2026, 4, 29), reservationTime, theme1));
        reservationRepository.create(
                new Reservation("fizz", LocalDate.of(2026, 5, 1), reservationTime, theme1));
        reservationRepository.create(
                new Reservation("fizz", LocalDate.of(2026, 5, 1), reservationTime, theme2));

        List<ServiceThemeResponse> responses = themeFacade.readRanking(LocalDate.of(2026, 4, 29),
                LocalDate.of(2026, 5, 1));

        assertThat(responses.get(0).name()).isEqualTo("피즈의 모험");
        assertThat(responses.get(1).name()).isEqualTo("피즈의 모험2");
    }

    @Test
    void deleteTest() {
        themeFacade.create(new ServiceThemeCreateRequest("피즈의 모험", "모험 이야기", "url.jpg"));
        themeFacade.delete(1L);

        List<ServiceThemeResponse> responses = themeFacade.readAll();
        assertThat(responses.size()).isEqualTo(0);
    }
}
