package com.sanedge.ecommerce;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Module-local test base for the transaction service.
 *
 * <p>MongoDB runs as a single-node replica set (testcontainers) so the transactional outbox can
 * be exercised with real multi-document transactions — the business row and the outbox row are
 * committed in one transaction.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    static {
        MONGO.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", () -> "true");
    }

    @org.springframework.beans.factory.annotation.Autowired
    protected org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    /**
     * MongoDB has no test-rollback: clear all collections before each test so fixtures seeded
     * by @BeforeEach (or the test body) start from a clean slate.
     */
    @org.junit.jupiter.api.BeforeEach
    void clearDatabaseBeforeEachTest() {
        for (String name : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.getDb().getCollection(name).deleteMany(new org.bson.Document());
        }
    }
}
