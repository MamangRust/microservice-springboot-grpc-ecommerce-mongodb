package com.sanedge.ecommerce.transaction.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.transaction.models.Transaction;

@Repository
public interface TransactionCommandRepository
        extends MongoRepository<Transaction, String>, TransactionCommandRepositoryCustom {
}
