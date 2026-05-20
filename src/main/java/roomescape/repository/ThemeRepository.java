package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Theme;
import roomescape.entity.ThemeEntity;

public interface ThemeRepository {

    ThemeEntity create(Theme theme);

    Optional<ThemeEntity> read(Long id);

    List<ThemeEntity> readAll();

    void delete(Long id);
}
