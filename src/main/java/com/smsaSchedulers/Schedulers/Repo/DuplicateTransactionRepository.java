/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftMessageHeader;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DuplicateTransactionRepository extends JpaRepository<SwiftMessageHeader, Long> {

    @Query(value = "SELECT h.smsa_msg_io AS Identifier, "
            + "       h.smsa_sender_bic AS Sender, "
            + "       h.smsa_receiver_bic AS Receiver, "
            + "       h.smsa_mt_code AS MessageType, "
            + "       h.smsa_txn_ref AS ReferenceNo, "
            + "       h.smsa_trxn_rltd_refn AS RelatedReferenceNo, "
            + "       h.smsa_file_type AS FileType, "
            + "       h.smsa_msg_valdate AS ValueDate, "
            + "       h.smsa_msg_currency AS Currency, "
            + "       h.smsa_txn_amount AS Amount, "
            + "       ms.geog_name AS Location, "
            + "       h.smsa_file_date AS SendRecDate, "
            + "       h.smsa_file_time AS SendRecTime, "
            + "       h.smsa_mior_ref AS MirMor "
            + "FROM smsa_prt_message_hdr h "
            + "JOIN smsa_geo_mst ms ON h.smsa_geo_id = ms.geog_code "
            + "JOIN ( "
            + "    SELECT smsa_msg_io, smsa_sender_bic, smsa_receiver_bic, smsa_mt_code, "
            + "           smsa_txn_ref, smsa_trxn_rltd_refn, smsa_msg_valdate, "
            + "           smsa_msg_currency, smsa_txn_amount "
            + "    FROM smsa_prt_message_hdr "
            + "    GROUP BY smsa_msg_io, smsa_sender_bic, smsa_receiver_bic, smsa_mt_code, "
            + "             smsa_txn_ref, smsa_trxn_rltd_refn, smsa_msg_valdate, "
            + "             smsa_msg_currency, smsa_txn_amount "
            + "    HAVING COUNT(*) > 1 "
            + ") dup "
            + "ON  h.smsa_msg_io = dup.smsa_msg_io "
            + "AND h.smsa_sender_bic = dup.smsa_sender_bic "
            + "AND h.smsa_receiver_bic = dup.smsa_receiver_bic "
            + "AND h.smsa_mt_code = dup.smsa_mt_code "
            + "AND h.smsa_txn_ref = dup.smsa_txn_ref "
            + "AND h.smsa_trxn_rltd_refn = dup.smsa_trxn_rltd_refn "
            + "AND h.smsa_msg_valdate = dup.smsa_msg_valdate "
            + "AND h.smsa_msg_currency = dup.smsa_msg_currency "
            + "AND h.smsa_txn_amount = dup.smsa_txn_amount "
            + "WHERE (:geographies IS NULL OR ms.geog_name IN (:geographies)) "
            + "  AND (:referenceNo IS NULL OR h.smsa_txn_ref = :referenceNo) "
            + "  AND (:relatedReferenceNo IS NULL OR h.smsa_trxn_rltd_refn = :relatedReferenceNo) "
            + "  AND (:currency IS NULL OR h.smsa_msg_currency = :currency) "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date <= :toDate) "
            + "ORDER BY h.smsa_file_date",
           countQuery = "SELECT COUNT(*) "
            + "FROM smsa_prt_message_hdr h "
            + "JOIN smsa_geo_mst ms ON h.smsa_geo_id = ms.geog_code "
            + "JOIN ( "
            + "    SELECT smsa_msg_io, smsa_sender_bic, smsa_receiver_bic, smsa_mt_code, "
            + "           smsa_txn_ref, smsa_trxn_rltd_refn, smsa_msg_valdate, "
            + "           smsa_msg_currency, smsa_txn_amount "
            + "    FROM smsa_prt_message_hdr "
            + "    GROUP BY smsa_msg_io, smsa_sender_bic, smsa_receiver_bic, smsa_mt_code, "
            + "             smsa_txn_ref, smsa_trxn_rltd_refn, smsa_msg_valdate, "
            + "             smsa_msg_currency, smsa_txn_amount "
            + "    HAVING COUNT(*) > 1 "
            + ") dup "
            + "ON  h.smsa_msg_io = dup.smsa_msg_io "
            + "AND h.smsa_sender_bic = dup.smsa_sender_bic "
            + "AND h.smsa_receiver_bic = dup.smsa_receiver_bic "
            + "AND h.smsa_mt_code = dup.smsa_mt_code "
            + "AND h.smsa_txn_ref = dup.smsa_txn_ref "
            + "AND h.smsa_trxn_rltd_refn = dup.smsa_trxn_rltd_refn "
            + "AND h.smsa_msg_valdate = dup.smsa_msg_valdate "
            + "AND h.smsa_msg_currency = dup.smsa_msg_currency "
            + "AND h.smsa_txn_amount = dup.smsa_txn_amount "
            + "WHERE (:geographies IS NULL OR ms.geog_name IN (:geographies)) "
            + "  AND (:referenceNo IS NULL OR h.smsa_txn_ref = :referenceNo) "
            + "  AND (:relatedReferenceNo IS NULL OR h.smsa_trxn_rltd_refn = :relatedReferenceNo) "
            + "  AND (:currency IS NULL OR h.smsa_msg_currency = :currency) "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date <= :toDate)",
           nativeQuery = true)
    Page<DuplicateTransactionProjection> findDuplicateMessages(
            @Param("geographies") List<String> geographies,
            @Param("referenceNo") String referenceNo,
            @Param("relatedReferenceNo") String relatedReferenceNo,
            @Param("currency") String currency,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}

