package project.tdlogistics.agency_company.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.placeholder.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.html.Option;

@Repository
public class AgencyRepositoryImpl implements CustomAgencyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean checkExistTableWithPostalCode(String postalCode) {
        String sql = "SHOW TABLES LIKE :postalCode";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("postalCode", postalCode);
        return !query.getResultList().isEmpty();
    }

    @Transactional
    @Override
    public boolean checkTableExists(String tableName) {
        String sql = "SHOW TABLES LIKE :tableName";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("tableName", tableName);
        return !query.getResultList().isEmpty();
    }

    @Override
    @Transactional
    public String createTablesForAgency(String postalCode) throws Exception {

        String ordersTable = postalCode + "_orders";
        String shipmentTable = postalCode + "_shipment";
        String shipperTasksTable = postalCode + "_shipper_tasks";
        String driverTasksTable = postalCode + "_driver_tasks";
        String scheduleTable = postalCode + "_schedule";

        final String cmdCreateOrdersTable = "CREATE TABLE " + ordersTable + " AS SELECT * FROM orders WHERE 1 = 0";

        final String cmdCreateShipmentTable = "CREATE TABLE " + shipmentTable
                + " AS SELECT * FROM shipment WHERE 1 = 0";

        final String createShipperTasksTable = String.format("CREATE TABLE %s (" +
                "id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "order_id varchar(30) NOT NULL," +
                "staff_id varchar(25) NOT NULL," +
                "created_at datetime DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "completed_at datetime DEFAULT NULL," +
                "completed tinyint(1) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;", shipperTasksTable);

        final String createDriverTasksTable = "CREATE TABLE " + driverTasksTable
                + " AS SELECT * FROM driver_tasks WHERE 1 = 0";

        final String createScheduleTable = "CREATE TABLE " + scheduleTable
                + " AS SELECT * FROM schedule WHERE 1 = 0";

        entityManager.createNativeQuery(cmdCreateOrdersTable).executeUpdate();
        entityManager.createNativeQuery(cmdCreateShipmentTable).executeUpdate();
        entityManager.createNativeQuery(createShipperTasksTable).executeUpdate();
        entityManager.createNativeQuery(createDriverTasksTable).executeUpdate();
        entityManager.createNativeQuery(createScheduleTable).executeUpdate();

        List<String> necessaryTables = Arrays.asList(ordersTable, shipmentTable, shipperTasksTable,
                driverTasksTable, scheduleTable);
        List<String> successCreatedTable = new ArrayList<>();

        if (checkTableExists(ordersTable)) {
            successCreatedTable.add(ordersTable);
        }
        if (checkTableExists(shipmentTable)) {
            successCreatedTable.add(shipmentTable);
        }
        if (checkTableExists(shipperTasksTable)) {
            successCreatedTable.add(shipperTasksTable);
        }
        if (checkTableExists(driverTasksTable)) {
            successCreatedTable.add(driverTasksTable);
        }
        if (checkTableExists(scheduleTable)) {
            successCreatedTable.add(scheduleTable);
        }

        String addPrimaryKeyForOrdersTableQuery = String.format("ALTER TABLE %s ADD PRIMARY KEY (order_id)",
                ordersTable);
        String addForeignKeyForOrdersTableQuery = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_order_id FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE",
                ordersTable, ordersTable);
        String addPrimaryKeyForShipperTasksTableQuery = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_order_id_ FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE ON UPDATE CASCADE",
                shipperTasksTable, shipperTasksTable);
        String addPrimaryKeyForScheduleTableQuery = String.format("ALTER TABLE %s ADD PRIMARY KEY (id)",
                scheduleTable);
        String addPrimaryKeyForShipmentTableQuery = String.format("ALTER TABLE %s ADD PRIMARY KEY (shipment_id)",
                shipmentTable);
        String addPrimaryKeyForDriverTasksTableQuery = String.format("ALTER TABLE %s ADD PRIMARY KEY (id)",
                driverTasksTable);

        entityManager.createNativeQuery(addPrimaryKeyForOrdersTableQuery).executeUpdate();
        entityManager.createNativeQuery(addForeignKeyForOrdersTableQuery).executeUpdate();
        entityManager.createNativeQuery(addPrimaryKeyForShipperTasksTableQuery).executeUpdate();
        entityManager.createNativeQuery(addPrimaryKeyForScheduleTableQuery).executeUpdate();
        entityManager.createNativeQuery(addPrimaryKeyForShipmentTableQuery).executeUpdate();
        entityManager.createNativeQuery(addPrimaryKeyForDriverTasksTableQuery).executeUpdate();

        if (successCreatedTable.size() < 5) {
            necessaryTables.removeAll(successCreatedTable);
            String message = String.format(
                    "Failed to create all necessary tables for an agency. The created tables are: %s.\n Please manually create the following tables in the database: %s.",
                    String.join(", ", successCreatedTable), String.join(", ", necessaryTables));
            return message;
        }
        return String.format("Tạo tất cả bảng cần thiết cho một bưu cục/đại lý thành công. Các bảng đã được tạo là: %s",
                String.join(", ", successCreatedTable));

    }

    @Override
    @Transactional
    public Response dropTablesForAgency(String postalCode) {
        try {
            String ordersTable = postalCode + "_orders";
            String shipmentTable = postalCode + "_shipment";
            String shipperTasksTable = postalCode + "_shipper_tasks";
            String driverTasksTable = postalCode + "_driver_tasks";
            String scheduleTable = postalCode + "_schedule";

            List<String> necessaryTables = Arrays.asList(ordersTable, shipmentTable, shipperTasksTable,
                    driverTasksTable, scheduleTable);
            List<String> successDroppedTable = new ArrayList<>();

            for (String table : necessaryTables) {
                String dropTableQuery = "DROP TABLE IF EXISTS " + table;
                entityManager.createNativeQuery(dropTableQuery).executeUpdate();
                if (!checkTableExists(table)) {
                    successDroppedTable.add(table);
                }
            }

            if (successDroppedTable.size() < 5) {
                necessaryTables.removeAll(successDroppedTable);
                String message = String.format(
                        "Không thể xóa tất cả các bảng của bưu cục/đại lý. Các bảng đã được xóa là %s.\n Vui lòng xóa thủ công các bảng %s.",
                        String.join(", ", successDroppedTable), String.join(", ", necessaryTables));
                return new Response(false, message, successDroppedTable);
            }

            String message = String.format(
                    "Xóa tất cả các bảng của một bưu cục thành công. Các bảng đã được xóa là %s.",
                    String.join(", ", successDroppedTable));
            return new Response(true, message, successDroppedTable);

        } catch (Exception e) {
            return new Response(true, e.getMessage(), null);
        }
    }

    // @Override
    // @Transactional
    // public Response locateAgencyInArea(int choice, String province, String
    // district, List<String> wards,
    // String agencyId, String postalCode) {
    // try {
    // if (choice == 0) {

    // Optional<List<String>> resultGetProvince = province.
    // String provinceSelectQuery = "SELECT agency_ids FROM province WHERE province
    // = ? LIMIT 1";
    // Query provinceResultSelect =
    // entityManager.createNativeQuery(provinceSelectQuery);
    // provinceResultSelect.setParameter(1, province);
    // List<String> agenciesOfProvince = (List<String>)
    // provinceResultSelect.getSingleResult();
    // if (agenciesOfProvince == null) {
    // agenciesOfProvince = new ArrayList<>();
    // }

    // if (!agenciesOfProvince.contains(agencyId)) {
    // agenciesOfProvince.add(agencyId);
    // }

    // String districtSelectQuery = "SELECT agency_ids FROM district WHERE province
    // = ? AND district = ? LIMIT 1";
    // Query districtResultSelect =
    // entityManager.createNativeQuery(districtSelectQuery);
    // districtResultSelect.setParameter(1, province);
    // districtResultSelect.setParameter(2, district);
    // List<String> agenciesOfDistrict = (List<String>)
    // districtResultSelect.getSingleResult();
    // if (agenciesOfDistrict == null) {
    // agenciesOfDistrict = new ArrayList<>();
    // }

    // if (!agenciesOfDistrict.contains(agencyId)) {
    // agenciesOfDistrict.add(agencyId);
    // }

    // String provinceUpdateQuery = "UPDATE province SET agency_ids = ? WHERE
    // province = ?";
    // Query provinceResultUpdate =
    // entityManager.createNativeQuery(provinceUpdateQuery);
    // provinceResultUpdate.setParameter(1, agenciesOfProvince.toString());
    // provinceResultUpdate.setParameter(2, province);
    // provinceResultUpdate.executeUpdate();

    // String districtUpdateQuery = "UPDATE district SET agency_ids = ? WHERE
    // province = ? AND district = ?";
    // Query districtResultUpdate =
    // entityManager.createNativeQuery(districtUpdateQuery);
    // districtResultUpdate.setParameter(1, agenciesOfDistrict.toString());
    // districtResultUpdate.setParameter(2, province);
    // districtResultUpdate.setParameter(3, district);
    // districtResultUpdate.executeUpdate();

    // for (String ward : wards) {
    // String wardUpdateQuery = "UPDATE ward SET agency_id = ?, postal_code = ?
    // WHERE province = ? AND district = ? AND ward = ?";
    // Query wardResultUpdate = entityManager.createNativeQuery(wardUpdateQuery);
    // wardResultUpdate.setParameter(1, agencyId);
    // wardResultUpdate.setParameter(2, postalCode);
    // wardResultUpdate.setParameter(3, province);
    // wardResultUpdate.setParameter(4, district);
    // wardResultUpdate.setParameter(5, ward);
    // wardResultUpdate.executeUpdate();
    // }
    // }
    // if (choice == 1) {
    // String provinceSelectQuery = "SELECT agency_ids FROM province WHERE province
    // = ? LIMIT 1";
    // Query provinceResultSelect =
    // entityManager.createNativeQuery(provinceSelectQuery);
    // provinceResultSelect.setParameter(1, province);
    // List<String> agenciesOfProvince = (List<String>)
    // provinceResultSelect.getSingleResult();
    // if (agenciesOfProvince == null) {
    // agenciesOfProvince = new ArrayList<>();
    // }
    // if (agenciesOfProvince.contains(agencyId)) {
    // agenciesOfProvince.remove(agencyId);
    // }

    // String districtSelectQuery = "SELECT agency_ids FROM district WHERE province
    // = ? AND district = ? LIMIT 1";
    // Query districtResultSelect =
    // entityManager.createNativeQuery(districtSelectQuery);
    // districtResultSelect.setParameter(1, province);
    // districtResultSelect.setParameter(2, district);
    // List<String> agenciesOfDistrict = (List<String>)
    // districtResultSelect.getSingleResult();
    // if (agenciesOfDistrict == null) {
    // agenciesOfDistrict = new ArrayList<>();
    // }
    // if (agenciesOfDistrict.contains(agencyId)) {
    // agenciesOfDistrict.remove(agencyId);
    // }

    // String provinceUpdateQuery = "UPDATE province SET agency_ids = ? WHERE
    // province = ?";
    // Query provinceResultUpdate =
    // entityManager.createNativeQuery(provinceUpdateQuery);
    // provinceResultUpdate.setParameter(1, agenciesOfProvince.toString());
    // provinceResultUpdate.setParameter(2, province);
    // provinceResultUpdate.executeUpdate();

    // String districtUpdateQuery = "UPDATE district SET agency_ids = ? WHERE
    // province = ? AND district = ?";
    // Query districtResultUpdate =
    // entityManager.createNativeQuery(districtUpdateQuery);
    // districtResultUpdate.setParameter(1, agenciesOfDistrict.toString());
    // districtResultUpdate.setParameter(2, province);
    // districtResultUpdate.setParameter(3, district);
    // districtResultUpdate.executeUpdate();

    // for (String ward : wards) {
    // String wardUpdateQuery = "UPDATE ward SET agency_id = ?, postal_code = ?
    // WHERE province = ? AND district = ? AND ward = ?";
    // Query wardResultUpdate = entityManager.createNativeQuery(wardUpdateQuery);
    // wardResultUpdate.setParameter(1, null);
    // wardResultUpdate.setParameter(2, null);
    // wardResultUpdate.setParameter(3, province);
    // wardResultUpdate.setParameter(4, district);
    // wardResultUpdate.setParameter(5, ward);
    // wardResultUpdate.executeUpdate();
    // }
    // }

    // return new Response<>(false, "Cập nhật thông tin địa bàn hoạt động thành
    // công.", null);

    // } catch (Exception e) {
    // return new Response<>(true, e.getMessage(), null);
    // }
    // }

    // @Override
    // @Transactional
    // public List<Agency> getManyAgencies(Agency info, Map<String, Object>
    // paginationConditions) {
    // try {
    // String queryString = "SELECT a FROM Agency a WHERE ";
    // for (String field : info.keySet()) {
    // queryString += "a." + field + " = :" + field + " AND ";
    // }
    // queryString = queryString.substring(0, queryString.length() - 5); // Remove
    // the last " AND "

    // TypedQuery<Agency> query = entityManager.createQuery(queryString,
    // Agency.class);
    // for (Map.Entry<String, Object> entry : info.entrySet()) {
    // query.setParameter(entry.getKey(), entry.getValue());
    // }

    // int limit = 0;
    // int offset = 0;

    // if (paginationConditions.containsKey("rows")) {
    // limit = (int) paginationConditions.get("rows");
    // }
    // if (paginationConditions.containsKey("page")) {
    // offset = (limit * ((int) paginationConditions.get("page")));
    // }

    // query.setFirstResult(offset);
    // query.setMaxResults(limit);

    // List<Agency> result = query.getResultList();

    // // for (Agency agency : result) {
    // // if (agency.getManagedWards() != null) {
    // // // Parse the managed_areas field as needed
    // // // agency.setManagedAreas(parseManagedAreas(agency.getManagedAreas()));
    // // }
    // // }

    // return result;
    // } catch (Exception e) {
    // System.err.println("An error occurred: " + e.getMessage());
    // return new ArrayList<>();

    // }
    // }

    @Override
    @Transactional
    public List<String> getAgencyManagedWards(String agencyId) {
        String queryString = "SELECT a.managed_wards FROM Agency a WHERE a.agency_id = :agencyId";
        TypedQuery<String> query = entityManager.createQuery(queryString, String.class);
        query.setParameter("agencyId", agencyId);

        List<String> result = query.getResultList();

        return result;
    }
}
