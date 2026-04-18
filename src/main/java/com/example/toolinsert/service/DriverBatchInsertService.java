package com.example.toolinsert.service;

import com.example.toolinsert.config.ImportProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverBatchInsertService {

    private final ImportProperties properties;

    public DriverBatchInsertService(ImportProperties properties) {
        this.properties = properties;
    }

    public <T> void saveInBatches(List<T> items, JpaRepository<T, ?> repository) {
        if (items.isEmpty()) {
            return;
        }

        int batchSize = Math.max(1, properties.getBatchSize());
        for (int start = 0; start < items.size(); start += batchSize) {
            int end = Math.min(start + batchSize, items.size());
            repository.saveAllAndFlush(items.subList(start, end));
        }
    }
}
