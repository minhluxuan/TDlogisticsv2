package project.tdlogistics.orders.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.tdlogistics.orders.entities.Request;
import project.tdlogistics.orders.entities.Response;

@Service
public class FilesService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String exchange = "rpc-direct-exchange";
    private final String routingKey = "rpc.file";

    public byte[] getFile(String path) throws Exception, JsonProcessingException {
        HashMap<String, Object> criteriaGetFile = new HashMap<String, Object>();
        criteriaGetFile.put("path", path);
        final byte[] jsonRequestGettingFile = objectMapper.writeValueAsBytes(new Request<byte[]>("getFile", criteriaGetFile, null));
        final byte[] jsonResponseGettingFile = (byte[]) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestGettingFile);
        if (jsonResponseGettingFile == null) {
            throw new IllegalStateException("Đã xảy ra lỗi khi lấy file. Vui lòng thử lại");
        }

        return jsonResponseGettingFile;
    }

    public String sendFile(String path, String option, MultipartFile file) throws Exception, JsonProcessingException {
        String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + file.getOriginalFilename();
        HashMap<String, Object> criteriaSendingFile = new HashMap<String, Object>();
        criteriaSendingFile.put("path", path);
        criteriaSendingFile.put("option", option);
        criteriaSendingFile.put("filename", filename);
        final byte[] jsonRequestSendingFile = objectMapper.writeValueAsBytes(new Request<byte[]>("storeFile", criteriaSendingFile, file.getBytes()));
        final byte[] jsonResponseSendingFile = (byte[]) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestSendingFile);
        if (jsonResponseSendingFile == null) {
            throw new IllegalStateException("Đã xảy ra lỗi. Vui lòng thử lại");
        }
        
        final Response<String> responseSendingFile = objectMapper.readValue(jsonResponseSendingFile, new TypeReference<Response<String>>() {});
        if (responseSendingFile.getError()) {
            throw new IllegalStateException(responseSendingFile.getMessage());
        }

        return filename;
    }

    public void deleteFile(String path) throws Exception, JsonProcessingException {
        HashMap<String, Object> criteriaDeleteFile = new HashMap<String, Object>();
        criteriaDeleteFile.put("path", path);
        final byte[] jsonRequestDeletingFile = objectMapper.writeValueAsBytes(new Request<byte[]>("deleteFile", criteriaDeleteFile, null));
        final byte[] jsonResponseDeletingFile = (byte[]) amqpTemplate.convertSendAndReceive(exchange, routingKey, jsonRequestDeletingFile);
        if (jsonResponseDeletingFile == null) {
            throw new IllegalStateException("Đã xảy ra lỗi. Vui lòng thử lại");
        }

        final Response<String> responseDeletingFile = objectMapper.readValue(jsonResponseDeletingFile, new TypeReference<Response<String>>() {});
        if (responseDeletingFile.getError()) {
            throw new IllegalStateException(responseDeletingFile.getMessage());
        }
    }

    public void isValidImageFile(MultipartFile file) {
        if (List.of("image/jpg", "image/jpeg", "image/png", "image/bmp", "image/webp", "image/svg", "image/heic").contains(file.getContentType())) {
            return;
        }

        throw new IllegalArgumentException("File không hợp lệ. Chỉ có file jpg, png, jpeg được cho phép");
    }
}
