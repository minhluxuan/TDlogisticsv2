package project.tdlogistics.orders.repositories;

import java.util.HashMap;
import java.util.Map;

public class ColumnNameMapper {
    private static final Map<String, String> columnNameMap = new HashMap<>();

    static {
        columnNameMap.put("orderId", "order_id");
        columnNameMap.put("userId", "user_id");
        columnNameMap.put("agencyId", "agency_id");
        columnNameMap.put("serviceType", "service_type");
        columnNameMap.put("nameSender", "name_sender");
        columnNameMap.put("phoneNumberSender", "phone_number_sender");
        columnNameMap.put("nameReceiver", "name_receiver");
        columnNameMap.put("phoneNumberReceiver", "phone_number_receiver");
        columnNameMap.put("mass", "mass");
        columnNameMap.put("height", "height");
        columnNameMap.put("width", "width");
        columnNameMap.put("length", "length");
        columnNameMap.put("provinceSource", "province_source");
        columnNameMap.put("districtSource", "district_source");
        columnNameMap.put("wardSource", "ward_source");
        columnNameMap.put("detailSource", "detail_source");
        columnNameMap.put("longSource", "long_source");
        columnNameMap.put("latSource", "lat_source");
        columnNameMap.put("provinceDest", "province_dest");
        columnNameMap.put("districtDest", "district_dest");
        columnNameMap.put("wardDest", "ward_dest");
        columnNameMap.put("detailDest", "detail_dest");
        columnNameMap.put("longDestination", "long_destination");
        columnNameMap.put("latDestination", "lat_destination");
        columnNameMap.put("fee", "fee");
        columnNameMap.put("parent", "parent");
        columnNameMap.put("journey", "journey");
        columnNameMap.put("cod", "COD");
        columnNameMap.put("shipper", "shipper");
        columnNameMap.put("statusCode", "status_code");
        columnNameMap.put("miss", "miss");
        columnNameMap.put("sendImages", "send_images");
        columnNameMap.put("receiveImages", "receive_images");
        columnNameMap.put("sendSignature", "send_signature");
        columnNameMap.put("receiveSignature", "receive_signature");
        columnNameMap.put("qrcode", "qrcode");
        columnNameMap.put("signature", "signature");
        columnNameMap.put("paid", "paid");
        columnNameMap.put("createdAt", "created_at");
        columnNameMap.put("lastUpdate", "last_update");
        columnNameMap.put("orderCode", "order_code");
    }

    public static String mappingColumn(String camelCase) {
        return columnNameMap.getOrDefault(camelCase, camelCase);
    }
}
