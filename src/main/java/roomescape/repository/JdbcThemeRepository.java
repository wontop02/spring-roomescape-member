package roomescape.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Theme;

@Primary
@Repository
public class JdbcThemeRepository implements ThemeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Theme create(Theme theme) {
        String sql = "INSERT INTO `theme`(`name`, `description`, `thumbnail_url`) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, theme.getName());
            preparedStatement.setString(2, theme.getDescription());
            preparedStatement.setString(3, theme.getThumbnailUrl());

            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new Theme(id, theme.getName(), theme.getDescription(), theme.getThumbnailUrl());
    }

    @Override
    public Optional<Theme> readById(Long id) {
        String sql = "SELECT * FROM `theme` WHERE `id` = (?)";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, (resultSet, rowNumber) -> {
                        String name = resultSet.getString("name");
                        String description = resultSet.getString("description");
                        String thumbnailUrl = resultSet.getString("thumbnail_url");
                        return new Theme(id, name, description, thumbnailUrl);
                    }, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Theme> readAll() {
        String sql = "SELECT * FROM `theme`";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> {
                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    String thumbnailUrl = resultSet.getString("thumbnail_url");
                    return new Theme(id, name, description, thumbnailUrl);
                }
        );
    }

    @Override
    public void delete(Theme theme) {
        String sql = "DELETE FROM theme WHERE id = (?)";

        jdbcTemplate.update(sql, theme.getId());
    }
}
