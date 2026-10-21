package com.sanedge.ecommerce.common.config;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

/**
 * Generator id numerik (String) untuk dokumen MongoDB.
 *
 * <p>Model memakai {@code @Id} String supaya kontrak gRPC (int32) tetap bisa dipakai, bukan
 * ObjectId hex bawaan MongoDB. Generator ini dipakai dua tempat:
 *
 * <ul>
 *   <li>{@link MongoStringIdGenerator} — mengisi {@code @Id} yang masih null saat dokumen
 *       dikonversi;</li>
 *   <li>command service — memesan id <em>sebelum</em> dokumen disimpan, supaya id bisa dipakai
 *       sebagai foreign key untuk panggilan gRPC tanpa menulis dokumen lebih dulu (menjaga
 *       transaksi tetap singkat).</li>
 * </ul>
 *
 * <p>Sequence per-JVM cukup unik karena tiap service hanya meng-generate id untuk koleksi
 * miliknya sendiri.
 */
@Component
public class NumericIdGenerator {

    private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis() % 2_000_000_000L);

    /** Next id as a numeric String (compatible with the int32 gRPC contract). */
    public String nextId() {
        long floor = System.currentTimeMillis() % 2_000_000_000L;
        return String.valueOf(sequence.updateAndGet(current -> Math.max(current + 1, floor)));
    }
}
