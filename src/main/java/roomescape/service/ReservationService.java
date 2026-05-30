package roomescape.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservation;
import roomescape.domain.Reservations;
import roomescape.entity.ReservationEntity;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.exception.custom.CannotDeleteReservationTimeInUseException;
import roomescape.exception.custom.CannotDeleteThemeInUseException;
import roomescape.exception.custom.ReservationNotExistsException;
import roomescape.repository.ReservationRepository;
import roomescape.service.dto.request.ServiceReservationCreateRequest;
import roomescape.service.dto.request.ServiceReservationUpdateRequest;
import roomescape.service.dto.response.ServiceReservationResponse;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final Reservations reservations;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository, Clock clock) {
        this.reservationRepository = reservationRepository;
        this.reservations = new Reservations(
                reservationRepository.readAll().stream().map(ReservationEntity::toDomain).toList());
        this.clock = clock;
    }

    @Transactional
    public ServiceReservationResponse create(ServiceReservationCreateRequest request,
                                             ReservationTimeEntity reservationTimeEntity, ThemeEntity themeEntity) {
        Reservation reservation = request.toReservation(reservationTimeEntity.toDomain(), themeEntity.toDomain());
        reservations.create(reservation, LocalDateTime.now(clock));

        ReservationEntity reservationEntity = reservationRepository.create(reservation, reservationTimeEntity,
                themeEntity);

        return ServiceReservationResponse.from(reservationEntity);
    }

    public List<ServiceReservationResponse> readByName(String name) {
        return reservationRepository.readByName(name).stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    public List<ServiceReservationResponse> readAll() {
        return reservationRepository.readAll().stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    @Transactional
    public ServiceReservationResponse update(Long id, ServiceReservationUpdateRequest request,
                                             ReservationTimeEntity newReservationTimeEntity) {
        ReservationEntity beforeReservationEntity = readReservation(id);
        Reservation beforeReservation = beforeReservationEntity.toDomain();

        Reservation newReservation = request.toReservation(beforeReservation,
                newReservationTimeEntity.toDomain());

        reservations.update(beforeReservation, newReservation, LocalDateTime.now(clock));

        reservationRepository.update(id, request.date(), request.timeId());
        ReservationEntity reservationEntity = new ReservationEntity(id, beforeReservation.getName(), request.date(),
                newReservationTimeEntity, beforeReservationEntity.getTheme());

        return ServiceReservationResponse.from(reservationEntity);
    }

    public ReservationEntity readReservation(Long reservationId) {
        return reservationRepository.readById(reservationId)
                .orElseThrow(ReservationNotExistsException::new);
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = readReservation(id).toDomain();
        reservations.delete(reservation, LocalDateTime.now(clock));
        reservationRepository.delete(id);
    }

    public Reservations reservations() {
        return reservations;
    }

    public void validateReferencedTime(Long id) {
        if (reservationRepository.existByTimeId(id)) {
            throw new CannotDeleteReservationTimeInUseException();
        }
    }

    public void validateReferencedTheme(Long id) {
        if (reservationRepository.existByThemeId(id)) {
            throw new CannotDeleteThemeInUseException();
        }
    }
}
