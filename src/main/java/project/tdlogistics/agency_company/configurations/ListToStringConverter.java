package project.tdlogistics.agency_company.configurations;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
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
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<String>(); // or return a default value
        }
        String sanitizedJson = dbData.replaceAll("\\\\", "");
        try {
            return objectMapper.readValue(sanitizedJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting string to list", e);
        }
    }
}

