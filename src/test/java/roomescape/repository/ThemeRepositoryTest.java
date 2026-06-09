package roomescape.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.Theme;

public class ThemeRepositoryTest extends RepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void createTest() {
        Theme theme = themeRepository.create(new Theme("방탈출", "설명", "url.jpg"));

        assertThat(theme.getId()).isEqualTo(1L);
    }

    @Test
    void readByIdTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출1", "방탈출1 설명", "url.jpg");

        Optional<Theme> theme = themeRepository.readById(1L);

        assertThat(theme.orElseThrow().getId()).isEqualTo(1L);
    }

    @Test
    void readByIdAllTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출1", "방탈출1 설명", "url.jpg");
        jdbcTemplate.update(sql, "방탈출2", "방탈출2 설명", "url.jpg");

        List<Theme> themes = themeRepository.readAll();
        assertThat(themes.size()).isEqualTo(2);
    }

    @Test
    void deleteTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출", "설명", "url.jpg");

        Theme theme = themeRepository.readById(1L).orElseThrow();
        themeRepository.delete(theme);

        String readAllThemeCountSql = "SELECT COUNT(*) FROM `theme`";
        int count = jdbcTemplate.queryForObject(readAllThemeCountSql, Integer.class);

        assertThat(count).isEqualTo(0);
    }
}
