package roomescape.facade;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceReservationCreateRequest;
import roomescape.service.dto.request.ServiceReservationUpdateRequest;
import roomescape.service.dto.response.ServiceReservationResponse;

@Service
@Transactional(readOnly = true)
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public ReservationFacade(ReservationService reservationService, ReservationTimeService reservationTimeService,
                             ThemeService themeService) {
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @Transactional
    public ServiceReservationResponse create(ServiceReservationCreateRequest request) {
        ReservationTime reservationTime = reservationTimeService.readReservationTime(request.timeId());
        Theme theme = themeService.readTheme(request.themeId());

        Reservation reservationWithoutId = request.toReservation(reservationTime, theme);
        Reservation reservation = reservationService.create(reservationWithoutId);
        return ServiceReservationResponse.from(reservation);
    }

    public List<ServiceReservationResponse> readByName(String name) {
        return reservationService.readByName(name).stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    public List<ServiceReservationResponse> readAll() {
        return reservationService.readAll().stream()
                .map(ServiceReservationResponse::from)
                .toList();
    }

    @Transactional
    public ServiceReservationResponse update(Long id, ServiceReservationUpdateRequest request) {
        Reservation beforeReservation = reservationService.readReservation(id);

        ReservationTime newReservationTime = reservationTimeService.readReservationTime(request.timeId());
        Reservation newReservation = request.toReservation(beforeReservation, newReservationTime);

        reservationService.update(beforeReservation, newReservation);

        return ServiceReservationResponse.from(newReservation);
    }

    @Transactional
    public void delete(Long id) {
        reservationService.delete(id);
    }
}
