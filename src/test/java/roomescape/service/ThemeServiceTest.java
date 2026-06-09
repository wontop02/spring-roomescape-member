package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roomescape.domain.Theme;
import roomescape.repository.FakeDatabase;
import roomescape.repository.FakeReservationRepository;
import roomescape.repository.FakeReservationTimeRepository;
import roomescape.repository.FakeThemeRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

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

        themeService = new ThemeService(themeRepository);
    }

    @Test
    void createTest() {
        Theme theme = themeService.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));

        assertThat(theme.getId()).isEqualTo(1L);
        assertThat(theme.getName()).isEqualTo("피즈의 모험");
    }

    @Test
    void readAllTest() {
        themeService.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        themeService.create(new Theme("나무의 일대기", "모험 이야기", "url.jpg"));

        List<Theme> themes = themeService.readAll();

        assertThat(themes.size()).isEqualTo(2);
        assertThat(themes.get(0).getName()).isEqualTo("피즈의 모험");
        assertThat(themes.get(1).getName()).isEqualTo("나무의 일대기");
    }

    @Test
    void deleteTest() {
        themeService.create(new Theme("피즈의 모험", "모험 이야기", "url.jpg"));
        themeService.delete(1L);

        List<Theme> themes = themeService.readAll();
        assertThat(themes.size()).isEqualTo(0);
    }
}
