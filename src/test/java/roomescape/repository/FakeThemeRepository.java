package roomescape.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Theme;
import roomescape.entity.ThemeEntity;

public class FakeThemeRepository implements ThemeRepository {

    public static final String THEME_TABLE = "theme";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeThemeRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public ThemeEntity create(Theme theme) {
        ThemeEntity themeEntity = new ThemeEntity(++currentId, theme.getName(), theme.getDescription(),
                theme.getThumbnailUrl());
        fakeDatabase.create(THEME_TABLE, themeEntity.getId(), themeEntity);

        return themeEntity;
    }

    @Override
    public Optional<ThemeEntity> read(Long id) {
        return Optional.ofNullable(fakeDatabase.read(THEME_TABLE, id, ThemeEntity.class));
    }

    @Override
    public List<ThemeEntity> readAll() {
        return fakeDatabase.readAll(THEME_TABLE, ThemeEntity.class);
    }

    @Override
    public void delete(Long id) {
        fakeDatabase.delete(THEME_TABLE, id);
    }
}
