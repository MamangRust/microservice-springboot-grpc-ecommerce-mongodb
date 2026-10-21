package com.sanedge.ecommerce.common.config;

import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mengisi field @Id bertipe String dengan id numerik (string) sebelum dokumen
 * dikonversi, sehingga id tetap kompatibel dengan kontrak gRPC (int32) —
 * bukan ObjectId hex bawaan MongoDB.
 *
 * <p>Sequence diambil dari {@link NumericIdGenerator} supaya id yang dipesan manual
 * (mis. oleh command service sebelum menyimpan) dan id yang diisi otomatis tidak
 * saling bentrok.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoStringIdGenerator extends AbstractMongoEventListener<Object> {

    private final NumericIdGenerator idGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source == null) {
            return;
        }

        for (Class<?> type = source.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Id.class) || field.getType() != String.class) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    if (field.get(source) == null) {
                        field.set(source, idGenerator.nextId());
                    }
                    return;
                } catch (IllegalAccessException e) {
                    log.warn("Failed to generate id for {}: {}", source.getClass().getSimpleName(), e.getMessage());
                    return;
                }
            }
        }
    }
}
