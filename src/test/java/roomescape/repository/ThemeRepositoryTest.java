package roomescape.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.Theme;
import roomescape.entity.ThemeEntity;

public class ThemeRepositoryTest extends RepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void createTest() {
        Theme theme = new Theme("방탈출", "설명", "url.jpg");
        ThemeEntity themeEntity = themeRepository.create(theme);

        assertThat(themeEntity.getId()).isEqualTo(1L);
    }

    @Test
    void readTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출1", "방탈출1 설명", "url.jpg");

        Optional<ThemeEntity> themeEntity = themeRepository.read(1L);

        assertThat(themeEntity.orElseThrow().getId()).isEqualTo(1L);
    }

    @Test
    void readAllTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출1", "방탈출1 설명", "url.jpg");
        jdbcTemplate.update(sql, "방탈출2", "방탈출2 설명", "url.jpg");

        List<ThemeEntity> themeEntities = themeRepository.readAll();
        assertThat(themeEntities.size()).isEqualTo(2);
    }

    @Test
    void deleteTest() {
        String sql = "INSERT INTO `theme` (`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, "방탈출", "설명", "url.jpg");

        themeRepository.delete(1L);

        String readAllThemeCountSql = "SELECT COUNT(*) FROM `theme`";
        int count = jdbcTemplate.queryForObject(readAllThemeCountSql, Integer.class);

        assertThat(count).isEqualTo(0);
    }
}
