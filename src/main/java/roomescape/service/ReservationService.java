package roomescape.service;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservation;
import roomescape.domain.Reservations;
import roomescape.exception.custom.CannotDeleteReservationTimeInUseException;
import roomescape.exception.custom.CannotDeleteThemeInUseException;
import roomescape.exception.custom.ReservationAlreadyExistsException;
import roomescape.exception.custom.ReservationNotExistsException;
import roomescape.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository, Clock clock) {
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    @Transactional
    public Reservation create(Reservation reservationWithoutId) {
        validateCreate(reservationWithoutId);

        return reservationRepository.create(reservationWithoutId);
    }

    private void validateCreate(Reservation reservation) {
        reservation.validateNotPast(LocalDateTime.now(clock));
        validateUnique(reservation);
    }

    private void validateUnique(Reservation reservation) {
        boolean isDuplicated = reservationRepository.existBySlot(reservation.getDate(), reservation.getTime().getId(),
                reservation.getTheme().getId());
        if (isDuplicated) {
            throw new ReservationAlreadyExistsException();
        }
    }

    public Reservations readByName(String name) {
        return reservationRepository.readByName(name);
    }

    public Reservations readAll() {
        return reservationRepository.readAll();
    }

    @Transactional
    public void update(Reservation beforeReservation, Reservation newReservation) {
        validateUpdate(beforeReservation, newReservation);

        reservationRepository.update(newReservation);
    }

    private void validateUpdate(Reservation beforeReservation, Reservation newReservation) {
        LocalDateTime now = LocalDateTime.now(clock);

        newReservation.validateNotPast(now);
        beforeReservation.validateAvailableModify(now);

        // 이전 예약과 새 예약이 같은 슬롯일 때는, 중복 예외 발생하지 않도록
        if (!beforeReservation.isSameSlot(newReservation)) {
            validateUnique(newReservation);
        }
    }

    public Reservation readReservation(Long reservationId) {
        return reservationRepository.readById(reservationId)
                .orElseThrow(ReservationNotExistsException::new);
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = readReservation(id);
        validateDelete(reservation);
        reservationRepository.delete(reservation);
    }

    private void validateDelete(Reservation reservation) {
        reservation.validateNotPast(LocalDateTime.now(clock));
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
