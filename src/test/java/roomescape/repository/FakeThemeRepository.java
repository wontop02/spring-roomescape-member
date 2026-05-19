package roomescape.repository;

import static roomescape.repository.FakeReservationRepository.RESERVATION_TABLE;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import roomescape.domain.Theme;
import roomescape.entity.ReservationEntity;
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

    @Override
    public List<ThemeEntity> readRanking(LocalDate startDate, LocalDate endDate, int limit) {
        List<ReservationEntity> reservations = fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class);

        Map<ThemeEntity, Long> reservationCount = reservations.stream()
                .collect(Collectors.groupingBy(ReservationEntity::getTheme, Collectors.counting()));

        List<ThemeEntity> ranking = reservationCount.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .map(Entry::getKey)
                .toList()
                .reversed();

        if (ranking.size() < limit) {
            return ranking;
        }
        return ranking.subList(0, limit);
    }

    @Override
    public boolean existById(Long id) {
        return fakeDatabase.readAll(THEME_TABLE, ThemeEntity.class).stream()
                .anyMatch(theme -> theme.getId().equals(id));
    }
}
