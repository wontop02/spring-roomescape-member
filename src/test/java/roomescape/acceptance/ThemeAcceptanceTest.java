package roomescape.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import roomescape.acceptance.step.ThemeSteps;

public class ThemeAcceptanceTest extends AcceptanceTest {

    @Test
    void reservationTimeApiSuccessTest() {
        // 1. 테마 추가
        ThemeSteps.createTheme("방탈출1", "방탈출1 설명", "theme/url.png");

        // 2. 전체 테마 조회 사이즈로 테마 추가 확인
        ThemeSteps.checkAllThemeSize(1);

        // 3. 테마 삭제
        ThemeSteps.deleteTheme(1L);

        // 4. 전체 테마 조회 사이즈로 테마 삭제 확인
        ThemeSteps.checkAllThemeSize(0);
    }

    @Test
    @Sql(scripts = "/ranking-test-data.sql")
    void themeRankingLoadTest() {
        ThemeSteps.checkThemeRanking("2026-04-29", "2026-05-01", 1);
    }
}
