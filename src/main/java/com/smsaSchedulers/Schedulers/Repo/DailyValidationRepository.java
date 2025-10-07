/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftNostroAccountMaster;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyValidationRepository extends JpaRepository<SwiftNostroAccountMaster, Long> {

    @Query(value
            = "SELECT h.smsa_message_id AS serialNo, "
            + "       h.smsa_file_date AS sendRecDate, "
            + "       h.smsa_msg_io AS ioFlag, "
            + "       h.smsa_mt_code AS messageType, "
            + "       h.smsa_sender_bic AS sender, "
            + "       h.smsa_receiver_bic AS receiver, "
            + "       h.smsa_txn_ref AS transactionReference, "
            + "       na.smsa_nostro_account AS accountNo, "
            + "       h.smsa_msg_currency AS currency, "
            + "       m.geog_name AS geography, "
            + "       na.smsa_nostro_account_team AS team, "
            + "       na.smsa_nostro_account_rmrk AS remark "
            + "FROM SMSA_NOSTRO_ACCOUNT_MASTER na "
            + "JOIN SMSA_PRT_MESSAGE_HDR h ON na.smsa_message_id = h.smsa_message_id "
            + "LEFT JOIN smsa_geo_mst m ON m.geog_code = h.smsa_geo_id "
            + "WHERE na.smsa_nostro_account_cnfm = 0 "
            + "  AND (h.smsa_mt_code IN (:messageTypes) ) "
            + "  AND ( :sender IS NULL OR h.smsa_sender_bic = :sender ) "
            + "  AND ( :receiver IS NULL OR h.smsa_receiver_bic = :receiver ) "
            + "  AND ( :transactionReference IS NULL OR h.smsa_txn_ref = :transactionReference ) "
            + "  AND ( :accountNo IS NULL OR na.smsa_nostro_account = :accountNo ) "
            + "  AND ( :currency IS NULL OR h.smsa_msg_currency = :currency ) "
            + "  AND ( :geographies IS NULL OR m.geog_name IN (:geographies) ) "
            + "  AND ( :fromDate IS NULL OR h.smsa_file_date >= :fromDate ) "
            + "  AND ( :toDate IS NULL OR h.smsa_file_date < :toDate ) "
            + "  AND ( :text IS NULL OR h.smsa_hdr_obj LIKE '%' || :text || '%' ) "
            + "ORDER BY h.smsa_file_date, h.smsa_mt_code, na.smsa_nostro_account",
            countQuery
            = "SELECT COUNT(*) "
            + "FROM SMSA_NOSTRO_ACCOUNT_MASTER na "
            + "JOIN SMSA_PRT_MESSAGE_HDR h ON na.smsa_message_id = h.smsa_message_id "
            + "LEFT JOIN smsa_geo_mst m ON m.geog_code = h.smsa_geo_id "
            + "WHERE na.smsa_nostro_account_cnfm = 0 "
            + "  AND (h.smsa_mt_code IN (:messageTypes) ) "
            + "  AND ( :sender IS NULL OR h.smsa_sender_bic = :sender ) "
            + "  AND ( :receiver IS NULL OR h.smsa_receiver_bic = :receiver ) "
            + "  AND ( :transactionReference IS NULL OR h.smsa_txn_ref = :transactionReference ) "
            + "  AND ( :accountNo IS NULL OR na.smsa_nostro_account = :accountNo ) "
            + "  AND ( :currency IS NULL OR h.smsa_msg_currency = :currency ) "
            + "  AND ( :geographies IS NULL OR m.geog_name IN (:geographies) ) "
            + "  AND ( :fromDate IS NULL OR h.smsa_file_date >= :fromDate ) "
            + "  AND ( :toDate IS NULL OR h.smsa_file_date < :toDate ) "
            + "  AND ( :text IS NULL OR h.smsa_hdr_obj LIKE '%' || :text || '%' ) ",
            nativeQuery = true)
    Page<DailyValidationProjection> findDailyValidation(
            @Param("messageTypes") List<Integer> messageTypes,
            @Param("geographies") List<String> geographies,
            @Param("sender") String sender,
            @Param("receiver") String receiver,
            @Param("transactionReference") String transactionReference,
            @Param("accountNo") String accountNo,
            @Param("currency") String currency,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("text") String text,
            Pageable pageable
    );

}
