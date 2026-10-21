package com.sanedge.ecommerce.statsbackfill.service;

import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * One-shot backfill: reads aggregated revenue from the source MongoDB (transaction
 * context, {@code transactions} collection) and writes it into the ClickHouse
 * {@code stats_revenue} table.
 *
 * <p>Aggregates by period + {@code merchant_id} (sum {@code amount}); {@code product_id}
 * is not a column on transactions, so it is fixed to 0 to stay consistent with the
 * live {@code stats.*.event} producer. Idempotent: rows are re-inserted (ClickHouse
 * is append-only), so run once per backfill window.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatsBackfillRunner implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final JdbcTemplate clickHouseJdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("stats-backfill starting");
        ensureTable();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("merchant_id", "amount")
                        .and("created_at").dateAsFormattedString("%Y-%m").as("period"),
                Aggregation.group("period", "merchant_id").sum("amount").as("revenue"));

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "transactions", Document.class);

        int count = 0;
        for (Document row : results.getMappedResults()) {
            Document id = (Document) row.get("_id");
            String period = id.getString("period");
            long merchantId = numericId(id.get("merchant_id"));
            double revenue = ((Number) row.get("revenue")).doubleValue();
            clickHouseJdbcTemplate.update(
                    "INSERT INTO stats_revenue (period, revenue, merchant_id, product_id, metric) "
                            + "VALUES (?, ?, ?, ?, 'txn')",
                    period, revenue, merchantId, 0L);
            count++;
        }
        log.info("stats-backfill completed: {} rows written to ClickHouse", count);
    }

    private void ensureTable() {
        clickHouseJdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS stats_revenue ("
                        + "period String, "
                        + "revenue Float64, "
                        + "merchant_id Int64 DEFAULT 0, "
                        + "product_id Int64 DEFAULT 0, "
                        + "metric String DEFAULT 'txn'"
                        + ") ENGINE = MergeTree() ORDER BY (metric, period)");
    }

    private static long numericId(Object value) {
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
