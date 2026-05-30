package roomescape.facade;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
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
        ReservationTimeEntity reservationTimeEntity = reservationTimeService.readReservationTime(request.timeId());
        ThemeEntity themeEntity = themeService.readTheme(request.themeId());

        return reservationService.create(request, reservationTimeEntity, themeEntity);
    }

    public List<ServiceReservationResponse> readByName(String name) {
        return reservationService.readByName(name);
    }

    public List<ServiceReservationResponse> readAll() {
        return reservationService.readAll();
    }

    @Transactional
    public ServiceReservationResponse update(Long id, ServiceReservationUpdateRequest request) {
        ReservationTimeEntity newReservationTimeEntity = reservationTimeService.readReservationTime(request.timeId());
        return reservationService.update(id, request, newReservationTimeEntity);
    }

    @Transactional
    public void delete(Long id) {
        reservationService.delete(id);
    }
}
