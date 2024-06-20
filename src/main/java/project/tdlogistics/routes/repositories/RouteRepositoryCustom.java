package project.tdlogistics.routes.repositories;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import project.tdlogistics.routes.entities.Route;

public interface RouteRepositoryCustom {
    public List<Route> findInDepartureTimeRangeAndOtherCriteria(Route criteria, LocalTime startDepartureTime,
            LocalTime endDepartureTime)
            throws Exception;
}