package project.tdlogistics.tasks.repositories;

import java.util.List;
import java.util.Map;

import project.tdlogistics.tasks.entities.DriverTask;

interface DriverRepositoryInterface {
    List<Map<String, Object>> getObjectsCanHandleTaskByAdmin();
    List<DriverTask> getTasks(Map<String, Object> criteria, String postalCode);
}
