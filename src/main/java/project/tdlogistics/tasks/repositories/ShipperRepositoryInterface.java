package project.tdlogistics.tasks.repositories;

import java.util.List;
import java.util.Map;

import project.tdlogistics.tasks.entities.ShipperTask;

public interface ShipperRepositoryInterface {

    List<Map<String, Object>> getObjectsCanHandleTask(String agencyId);
    List<ShipperTask> getTasks(Map<String, Object> criteria, String postalCode);
    List<ShipperTask> getHistory(Map<String, Object> criteria, String postalCode);
}
