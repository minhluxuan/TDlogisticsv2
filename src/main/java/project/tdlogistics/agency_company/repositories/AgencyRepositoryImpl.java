package project.tdlogistics.agency_company.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.tdlogistics.agency_company.entities.Agency;
import project.tdlogistics.agency_company.entities.Response;

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
        query.setParameter("postalCode", "%" + postalCode + "%");
        return !query.getResultList().isEmpty();
    }

    @Transactional
    @Override
    public boolean checkTableExists(String tableName) {
        String sql = "SHOW TABLES LIKE :tableName";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("tableName", "%" + tableName + "%");
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
    public void dropTablesForAgency(String postalCode) {
        String ordersTable = postalCode + "_orders";
        String shipmentTable = postalCode + "_shipment";
        String shipperTasksTable = postalCode + "_shipper_tasks";
        String driverTasksTable = postalCode + "_driver_tasks";
        String scheduleTable = postalCode + "_schedule";

        List<String> necessaryTables = Arrays.asList(ordersTable, shipmentTable, shipperTasksTable, driverTasksTable, scheduleTable);
        List<String> successDroppedTable = new ArrayList<>();

        for (String table : necessaryTables) {
			String dropTableQuery = "DROP TABLE IF EXISTS " + table;
			entityManager.createNativeQuery(dropTableQuery).executeUpdate();
			if (!checkTableExists(table)) {
				successDroppedTable.add(table);
			}
        }
    }

    // // @Override
    // // @Transactional
    // // public List<Agency> getManyAgencies(Agency info, Map<String, Object>
    // // paginationConditions) {
    // // try {
    // // String queryString = "SELECT a FROM Agency a WHERE ";
    // // for (String field : info.keySet()) {
    // // queryString += "a." + field + " = :" + field + " AND ";
    // // }
    // // queryString = queryString.substring(0, queryString.length() - 5); // Remove
    // // the last " AND "

    // // TypedQuery<Agency> query = entityManager.createQuery(queryString,
    // // Agency.class);
    // // for (Map.Entry<String, Object> entry : info.entrySet()) {
    // // query.setParameter(entry.getKey(), entry.getValue());
    // // }

    // // int limit = 0;
    // // int offset = 0;

    // // if (paginationConditions.containsKey("rows")) {
    // // limit = (int) paginationConditions.get("rows");
    // // }
    // // if (paginationConditions.containsKey("page")) {
    // // offset = (limit * ((int) paginationConditions.get("page")));
    // // }

    // // query.setFirstResult(offset);
    // // query.setMaxResults(limit);

    // // List<Agency> result = query.getResultList();

    // // // for (Agency agency : result) {
    // // // if (agency.getManagedWards() != null) {
    // // // // Parse the managed_areas field as needed
    // // // // agency.setManagedAreas(parseManagedAreas(agency.getManagedAreas()));
    // // // }
    // // // }

    // // return result;
    // // } catch (Exception e) {
    // // System.err.println("An error occurred: " + e.getMessage());
    // // return new ArrayList<>();

    // // }
    // // }

    // @Override
    // @Transactional
    // public List<String> getAgencyManagedWards(String agencyId) {
    //     String queryString = "SELECT a.managed_wards FROM Agency a WHERE a.agency_id = :agencyId";
    //     TypedQuery<String> query = entityManager.createQuery(queryString, String.class);
    //     query.setParameter("agencyId", agencyId);

    //     List<String> result = query.getResultList();

    //     return result;
    // }
}
