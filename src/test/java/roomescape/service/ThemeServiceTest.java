package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static roomescape.exception.ErrorCode.FUTURE_RANKING_PERIOD;
import static roomescape.exception.ErrorCode.INVALID_RANKING_PERIOD;
import static roomescape.exception.ErrorCode.LONG_RANKING_PERIOD;
import static roomescape.exception.ErrorCode.REFERENCED_THEME;

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
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

public class ThemeServiceTest {

    private ThemeService themeService;

    private ThemeRepository themeRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ReservationRepository reservationRepository;
    private Clock clock;

    @BeforeEach
    void beforeEach() {
        FakeDatabase fakeDatabase = new FakeDatabase();

        reservationRepository = new FakeReservationRepository(fakeDatabase);
        reservationTimeRepository = new FakeReservationTimeRepository(fakeDatabase);
        themeRepository = new FakeThemeRepository(fakeDatabase);
        clock = Clock.fixed(Instant.parse("2026-05-02T00:00:00Z"), ZoneId.of("Asia/Seoul"));

        themeService = new ThemeService(themeRepository, reservationRepository, clock);
    }

    @Test
    void readFutureRankingPeriodExceptionTest() {
        LocalDate startDate = LocalDate.of(2026, 5, 3);
        LocalDate endDate = LocalDate.of(2026, 5, 4);

        assertThatThrownBy(() -> themeService.readRanking(startDate, endDate))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(FUTURE_RANKING_PERIOD.getMessage());
    }

    @Test
    void readInvalidRankingPeriodExceptionTest() {
        LocalDate startDate = LocalDate.of(2026, 5, 2);
        LocalDate endDate = LocalDate.of(2026, 5, 1);

        assertThatThrownBy(() -> themeService.readRanking(startDate, endDate))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(INVALID_RANKING_PERIOD.getMessage());
    }

    @Test
    void readLongRankingPeriodExceptionTest() {
        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 2);

        assertThatThrownBy(() -> themeService.readRanking(startDate, endDate))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(LONG_RANKING_PERIOD.getMessage());
    }

    @Test
    void deleteReferencedThemeExceptionTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        ThemeEntity themeEntity = themeRepository.create(new Theme("방탈출1", "방탈출1 설명", "url.jpg"));
        Reservation reservation = new Reservation("fizz", LocalDate.of(2026, 5, 3), reservationTimeEntity.toDomain(),
                themeEntity.toDomain());

        reservationRepository.create(reservation, reservationTimeEntity, themeEntity);

        assertThatThrownBy(() -> themeService.delete(1L))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(REFERENCED_THEME.getMessage());
    }

    @Test
    void createTest() {
        String name = "피즈의 모험";
        String description = "모험 이야기";
        String thumbnailUrl = "url.jpg";
        ServiceThemeCreateRequest request = new ServiceThemeCreateRequest(name, description, thumbnailUrl);

        ServiceThemeResponse response = themeService.create(request);

        assertThat(response).isEqualTo(
                new ServiceThemeResponse(
                        1L,
                        name,
                        description,
                        thumbnailUrl
                )
        );
    }

    @Test
    void readAllTest() {
        themeService.create(new ServiceThemeCreateRequest(
                "피즈의 모험",
                "모험 이야기",
                "url.jpg"
        ));
        themeService.create(new ServiceThemeCreateRequest(
                "나무의 일대기",
                "모험 이야기",
                "url.jpg"
        ));

        List<ServiceThemeResponse> responses = themeService.readAll();

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0)).isEqualTo(new ServiceThemeResponse(
                1L,
                "피즈의 모험",
                "모험 이야기",
                "url.jpg"
        ));
        assertThat(responses.get(1)).isEqualTo(new ServiceThemeResponse(
                2L,
                "나무의 일대기",
                "모험 이야기",
                "url.jpg"
        ));
    }

    @Test
    void deleteTest() {
        themeService.create(new ServiceThemeCreateRequest(
                "피즈의 모험",
                "모험 이야기",
                "url.jpg"
        ));

        themeService.delete(1L);

        List<ServiceThemeResponse> responses = themeService.readAll();
        assertThat(responses.size()).isEqualTo(0);
    }

    @Test
    void readRankingTest() {
        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(
                new ReservationTime(LocalTime.of(10, 0)));
        ThemeEntity themeEntity1 = themeRepository.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        ThemeEntity themeEntity2 = themeRepository.create(new Theme("피즈의 모험2", "모험 이야기", "url.jpg"));

        Reservation reservation1 = new Reservation("fizz", LocalDate.of(2026, 4, 29), reservationTimeEntity.toDomain(),
                themeEntity1.toDomain());
        Reservation reservation2 = new Reservation("fizz", LocalDate.of(2026, 5, 1), reservationTimeEntity.toDomain(),
                themeEntity1.toDomain());
        Reservation reservation3 = new Reservation("fizz", LocalDate.of(2026, 5, 2), reservationTimeEntity.toDomain(),
                themeEntity2.toDomain());

        reservationRepository.create(reservation1, reservationTimeEntity, themeEntity1);
        reservationRepository.create(reservation2, reservationTimeEntity, themeEntity1);
        reservationRepository.create(reservation3, reservationTimeEntity, themeEntity2);

        List<ServiceThemeResponse> responses = themeService.readRanking(LocalDate.of(2026, 4, 29),
                LocalDate.of(2026, 5, 2));

        assertThat(responses.get(0).name()).isEqualTo("피즈의 모험");
        assertThat(responses.get(1).name()).isEqualTo("피즈의 모험2");
    }
}
