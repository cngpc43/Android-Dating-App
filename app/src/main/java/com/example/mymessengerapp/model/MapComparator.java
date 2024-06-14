package com.example.mymessengerapp.model;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MapComparator implements Comparator<Map<String, Object>> {
    private final String key;

    public MapComparator(String key)
    {
        this.key = key;
    }

    public int compare(Map<String, Object> first,
                       Map<String, Object> second)
    {
        // TODO: Null checking, both for maps and values
        if (first != null && second != null) {
            if (first.get(key) != null && second.get(key) != null) {
                Long firstValue = (Long) first.get(key);
                Long secondValue = (Long) second.get(key);
                return firstValue.compareTo(secondValue);
            }
        }
        return 0;
    }
}
