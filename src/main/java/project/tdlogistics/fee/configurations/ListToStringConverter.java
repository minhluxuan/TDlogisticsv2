package project.tdlogistics.fee.configurations;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

// @Converter
// public class ListToStringConverter implements AttributeConverter<List<String>, String> {

//     private static final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public String convertToDatabaseColumn(List<String> attribute) {
//         try {
//             return objectMapper.writeValueAsString(attribute);
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException("Error converting list to string", e);
//         }
//     }

//     @Override
//     public List<String> convertToEntityAttribute(String dbData) {
//         try {
//             return objectMapper.readValue(dbData, new TypeReference<List<String>>(){});
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException("Error converting string to list", e);
//         }
//     }
// }

@Converter
public class ListToStringConverter implements AttributeConverter<List<Map<String, String>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Map<String, String>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list to string", e);
        }
    }

    @Override
    public List<Map<String, String>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Map<String, String>>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting string to list", e);
        }
    }
}