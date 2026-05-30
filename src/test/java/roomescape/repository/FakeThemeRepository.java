package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Theme;

public class FakeThemeRepository implements ThemeRepository {

    public static final String THEME_TABLE = "theme";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeThemeRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public Theme create(Theme theme) {
        Theme themeWithId = new Theme(++currentId, theme.getName(), theme.getDescription(), theme.getThumbnailUrl());
        fakeDatabase.create(THEME_TABLE, themeWithId.getId(), themeWithId);
        return themeWithId;
    }

    @Override
    public Optional<Theme> readById(Long id) {
        return Optional.ofNullable(fakeDatabase.read(THEME_TABLE, id, Theme.class));
    }

    @Override
    public List<Theme> readAll() {
        return fakeDatabase.readAll(THEME_TABLE, Theme.class);
    }

    @Override
    public void delete(Theme theme) {
        fakeDatabase.delete(THEME_TABLE, theme.getId());
    }
}
