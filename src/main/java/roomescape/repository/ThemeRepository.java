package roomescape.domain;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository {

    Theme create(Theme theme);

    Optional<Theme> readById(Long id);

    List<Theme> readAll();

    void delete(Theme theme);
}
