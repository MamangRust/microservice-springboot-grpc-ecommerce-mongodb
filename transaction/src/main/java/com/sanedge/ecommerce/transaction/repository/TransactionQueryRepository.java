package com.sanedge.ecommerce.transaction.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.transaction.models.Transaction;

@Repository
public interface TransactionQueryRepository extends MongoRepository<Transaction, String> {
  @Query(value = """
          { 'deleted_at': null, '$or': [
              { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'payment_status': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
          """, sort = "{ 'created_at': -1 }")
  Page<Transaction> findTransactions(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = """
          { 'deleted_at': null, '$or': [
              { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'payment_status': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
          """, sort = "{ 'created_at': -1 }")
  Page<Transaction> findActiveTransactions(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = """
          { 'deleted_at': { '$ne': null }, '$or': [
              { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'payment_status': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
          """, sort = "{ 'deleted_at': -1 }")
  Page<Transaction> findTrashedTransactions(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = """
          { 'deleted_at': null, 'merchant_id': ?#{#merchantId == null ? null : #merchantId}, '$or': [
              { 'payment_method': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } },
              { 'payment_status': { '$regex': ?#{#keyword == null ? '' : #keyword}, '$options': 'i' } }
          ] }
          """, sort = "{ 'created_at': -1 }")
  Page<Transaction> findTransactionsByMerchant(
      @Param("keyword") String keyword,
      @Param("merchantId") Integer merchantId,
      Pageable pageable);

  @Query("{ 'transaction_id': ?0, 'deleted_at': null }")
  Optional<Transaction> findTransactionById(@Param("transactionId") String transactionId);

  @Query(value = "{ 'order_id': ?0, 'deleted_at': null }", sort = "{ 'created_at': -1 }")
  Optional<Transaction> findByOrderId(@Param("orderId") Integer orderId);
}
