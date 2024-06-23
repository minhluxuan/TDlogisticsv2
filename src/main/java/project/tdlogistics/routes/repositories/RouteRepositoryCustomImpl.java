package project.tdlogistics.routes.repositories;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import project.tdlogistics.routes.entities.Route;

public class RouteRepositoryCustomImpl implements RouteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    // @Query khong ho tro tim cac truong khong co dinh (null),ex: các trường vehicleId, destination, source có thể null.
    @Override
    public List<Route> findInDepartureTimeRangeAndOtherCriteria(Route criteria, LocalTime startDepartureTime,
            LocalTime endDepartureTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);

        Root<Route> route = cq.from(Route.class);
        List<Predicate> predicates = new ArrayList<>();

        if (startDepartureTime != null && endDepartureTime != null) {
            predicates.add(cb.between(route.get("departureTime"), startDepartureTime, endDepartureTime));
        }

        if (criteria != null) {
            if (criteria.getId() != null) {
                predicates.add(cb.equal(route.get("id"), criteria.getId()));
            }
            if (criteria.getVehicleId() != null) {
                predicates.add(cb.equal(route.get("origin"), criteria.getVehicleId()));
            }
            if (criteria.getDestination() != null) {
                predicates.add(cb.equal(route.get("destination"), criteria.getDestination()));
            }
            if (criteria.getSource() != null) {
                predicates.add(cb.equal(route.get("source"), criteria.getSource()));
            }
            //thêm criteria
        
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }
}