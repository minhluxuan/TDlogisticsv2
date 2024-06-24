package project.tdlogistics.schedule.repositories;

import java.util.HashMap;
import java.util.Map;

public class ScheduleColumnNameMapper {
    private static final Map<String, String> columnNameMap = new HashMap<>();

    static {
        columnNameMap.put("id", "id");
        columnNameMap.put("task", "task");
        columnNameMap.put("priority", "priority");
        columnNameMap.put("createdAt", "created_at");
        columnNameMap.put("completedAt", "completed_at");
        columnNameMap.put("deadline", "deadline");
        columnNameMap.put("completed", "completed");
        columnNameMap.put("lastUpdate", "last_update");

    }

    public static String mappingColumn(String camelCase) {
        return columnNameMap.getOrDefault(camelCase, camelCase);
    }
}