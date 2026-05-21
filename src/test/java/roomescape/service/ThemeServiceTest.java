package roomescape.service;

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
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
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
        Reservation reservation3 = new Reservation("fizz", LocalDate.of(2026, 5, 1), reservationTimeEntity.toDomain(),
                themeEntity2.toDomain());

        reservationRepository.create(reservation1, reservationTimeEntity, themeEntity1);
        reservationRepository.create(reservation2, reservationTimeEntity, themeEntity1);
        reservationRepository.create(reservation3, reservationTimeEntity, themeEntity2);

        List<ServiceThemeResponse> responses = themeService.readRanking(LocalDate.of(2026, 4, 29),
                LocalDate.of(2026, 5, 1));

        assertThat(responses.get(0).name()).isEqualTo("피즈의 모험");
        assertThat(responses.get(1).name()).isEqualTo("피즈의 모험2");
    }
}
