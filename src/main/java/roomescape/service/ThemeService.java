package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Theme;
import roomescape.exception.custom.ThemeNotExistsException;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Transactional
    public Theme create(Theme themeWithoutId) {
        return themeRepository.create(themeWithoutId);
    }

    public List<Theme> readAll() {
        return themeRepository.readAll();
    }

    @Transactional
    public void delete(Long id) {
        Theme theme = readTheme(id);
        themeRepository.delete(theme);
    }

    public Theme readTheme(Long themeId) {
        return themeRepository.readById(themeId)
                .orElseThrow(ThemeNotExistsException::new);
    }
}
