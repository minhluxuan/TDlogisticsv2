package project.tdlogistics.tasks.repositories;

import java.util.HashMap;
import java.util.Map;

public class ColumnNameMapper {
    private static final Map<String, String> columnNameMap = new HashMap<>();

    static {
        columnNameMap.put("orderId", "order_id");
        columnNameMap.put("staffId", "staff_id");
        columnNameMap.put("createdAt", "created_at");
        columnNameMap.put("completedAt", "completed_at");
        columnNameMap.put("completed", "completed");
    }

    public static String mappingColumn(String camelCase) {
        return columnNameMap.getOrDefault(camelCase, camelCase);
    }
}
