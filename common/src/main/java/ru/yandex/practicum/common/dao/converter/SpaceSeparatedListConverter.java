package ru.yandex.practicum.common.dao.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

@Converter(autoApply = true)
public class SpaceSeparatedListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return String.join(" ", attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return Collections.emptyList();
        }
        return Arrays.asList(dbData.split("\\s+"));
    }
}
