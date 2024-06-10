package project.tdlogistics.shipments.repositories;

import java.util.HashMap;
import java.util.Map;

public class ColumnNameMapper {
    private static final Map<String, String> columnNameMap = new HashMap<>();

    static {
        columnNameMap.put("shipmentId", "shipment_id");
        columnNameMap.put("agencyId", "agency_id");
        columnNameMap.put("agencyIdDest", "agency_id_dest");
        columnNameMap.put("longSource", "long_source");
        columnNameMap.put("latSource", "lat_source");
        columnNameMap.put("currentAgencyId", "current_agency_id");
        columnNameMap.put("currentLat", "current_lat");
        columnNameMap.put("currentLong", "current_long");
        columnNameMap.put("longDestination", "long_destination");
        columnNameMap.put("latDestination", "lat_destination");
        columnNameMap.put("transportPartnerId", "transport_partner_id");
        columnNameMap.put("staffId", "staff_id");
        columnNameMap.put("vehicleId", "vehicle_id");
        columnNameMap.put("mass", "mass");
        columnNameMap.put("orderIds", "order_ids");
        columnNameMap.put("parent", "parent");
        columnNameMap.put("status", "status");
        columnNameMap.put("createdAt", "created_at");
        columnNameMap.put("lastUpdate", "last_update");
        columnNameMap.put("journey", "journey");
    }

    public static String mappingColumn(String camelCase) {
        return columnNameMap.getOrDefault(camelCase, camelCase);
    }
}
