package project.tdlogistics.routes.configurations;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Converter(autoApply = true)
public class ListMapToStringConverter implements AttributeConverter<List<Map<String, String>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Map<String, String>> attribute) {
        if (attribute == null) {
            return "[]"; // or return a default value
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list to string", e);
        }
    }

    @Override
    public List<Map<String, String>> convertToEntityAttribute(String dbData) {
        System.out.println("Attempting to convert dbData to List<Map<String, String>>: " + dbData);
        if (dbData == null) {
            return List.of(); // or return a default value
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting string to list", e);
        }
    }

}