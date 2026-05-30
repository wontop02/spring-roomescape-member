package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.exception.custom.ReservationTimeAlreadyExistsException;
import roomescape.exception.custom.ReservationTimeNotExistsException;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeService(ReservationTimeRepository reservationTimeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Transactional
    public ReservationTime create(ReservationTime reservationTimeWithoutId) {
        validateCreate(reservationTimeWithoutId);

        return reservationTimeRepository.create(reservationTimeWithoutId);
    }

    private void validateCreate(ReservationTime reservationTime) {
        validateUnique(reservationTime);
    }

    private void validateUnique(ReservationTime reservationTime) {
        boolean isDuplicated = reservationTimeRepository.existByStartAt(reservationTime.getStartAt());
        if (isDuplicated) {
            throw new ReservationTimeAlreadyExistsException();
        }
    }

    public List<ReservationTime> readAll() {
        return reservationTimeRepository.readAll();
    }

    @Transactional
    public void delete(Long id) {
        ReservationTime reservationTime = readReservationTime(id);
        reservationTimeRepository.delete(reservationTime);
    }

    public ReservationTime readReservationTime(Long timeId) {
        return reservationTimeRepository.readById(timeId)
                .orElseThrow(ReservationTimeNotExistsException::new);
    }
}
