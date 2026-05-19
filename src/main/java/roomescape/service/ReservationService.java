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
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceReservationCreateRequest;
import roomescape.service.dto.request.ServiceReservationUpdateRequest;
import roomescape.service.dto.response.ServiceReservationResponse;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
                              Clock clock) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.clock = clock;
    }

    @Transactional
    public ServiceReservationResponse create(ServiceReservationCreateRequest request) {
        Reservations reservations = new Reservations(readReservations());

        ReservationTimeEntity reservationTimeEntity = readReservationTime(request.timeId());
        ThemeEntity themeEntity = readTheme(request.themeId());

        Reservation reservation = request.toReservation(reservationTimeEntity.toDomain(), themeEntity.toDomain());
        reservations.validateCreate(reservation, LocalDateTime.now(clock));

        ReservationEntity reservationEntity = reservationRepository.create(reservation, reservationTimeEntity,
                themeEntity);

        return ServiceReservationResponse.from(reservationEntity);
    }

    private List<ReservationEntity> readReservations() {
        return reservationRepository.readAll();
    }

    private ReservationTimeEntity readReservationTime(Long timeId) {
        return reservationTimeRepository.read(timeId)
                .orElseThrow(() -> new CustomInvalidRequestException(ErrorCode.NOT_FOUND_RESERVATION_TIME));
    }

    private ThemeEntity readTheme(Long themeId) {
        return themeRepository.read(themeId)
                .orElseThrow(() -> new CustomInvalidRequestException(ErrorCode.NOT_FOUND_THEME));
    }

    public List<ServiceReservationResponse> readByName(String name) {
        List<ReservationEntity> reservations = reservationRepository.readByName(name);

        return reservations.stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    public List<ServiceReservationResponse> readAll() {
        return readReservations().stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    @Transactional
    public ServiceReservationResponse update(Long id, ServiceReservationUpdateRequest request) {
        ReservationTimeEntity newReservationTimeEntity = readReservationTime(request.timeId());

        ReservationEntity beforeReservationEntity = readReservation(id);
        Reservation beforeReservation = beforeReservationEntity.toDomain();

        Reservation newReservation = request.toReservation(beforeReservation,
                newReservationTimeEntity.toDomain());

        Reservations reservations = new Reservations(readReservations());
        reservations.validateUpdate(beforeReservation, newReservation, LocalDateTime.now(clock));

        reservationRepository.update(id, request.date(), request.timeId());
        ReservationEntity reservationEntity = new ReservationEntity(id, beforeReservation.getName(), request.date(),
                newReservationTimeEntity, beforeReservationEntity.getTheme());

        return ServiceReservationResponse.from(reservationEntity);
    }

    private ReservationEntity readReservation(Long reservationId) {
        return reservationRepository.readById(reservationId)
                .orElseThrow(() -> new CustomInvalidRequestException(ErrorCode.NOT_FOUND_RESERVATION));
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = readReservation(id).toDomain();
        reservation.validateAvailableModify(LocalDateTime.now(clock));
        reservationRepository.delete(id);
    }
}
