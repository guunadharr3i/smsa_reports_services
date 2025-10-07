package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftMessageHeader;
import com.smsaSchedulers.Schedulers.Pojo.NostroClosingBalanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SwiftMessageHeaderRepository extends JpaRepository<SwiftMessageHeader, Long> {

    @Query(value = "SELECT g.geog_name AS location, "
            + " h.smsa_receiver_bic AS senderBic, "
            + " h.smsa_sender_bic AS receiverBic, "
            + " h.smsa_mt_code AS messageType, "
            + " t.smsa_trxn_acc_id AS nostroAccountNumber, "
            + " JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS senderBankName, "
            + " h.smsa_file_date AS statementReceivedDate, "
            + " t.smsa_closing_62f AS amount, "
            + " t.smsa_msg_close_ccy AS currency, "
            + " t.smsa_msg_valdate62f AS valueDate, "
            + " t.smsa_msg_close_cred AS debitCredit "
            + "FROM smsa_prt_message_hdr h "
            + "JOIN smsa_msg_txt t ON h.smsa_message_id = t.smsa_message_id "
            + "JOIN smsa_geo_mst g ON h.smsa_geo_id = g.geog_code "
            + "WHERE h.smsa_msg_io = 'O' "
            + "AND t.smsa_closing_62f IS NOT NULL "
            + "AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) "
            + "AND (:currency IS NULL OR t.smsa_msg_close_ccy = :currency) "
            + "AND h.smsa_mt_code IN (:mtCodes) "
            + // ✅ Only IN clause
            "AND ( :geoIds IS NULL OR h.smsa_geo_id IN (:geoIds) ) "
            + "ORDER BY h.smsa_file_date DESC",
            countQuery = "SELECT COUNT(*) "
            + "FROM smsa_prt_message_hdr h "
            + "JOIN smsa_msg_txt t ON h.smsa_message_id = t.smsa_message_id "
            + "JOIN smsa_geo_mst g ON h.smsa_geo_id = g.geog_code "
            + "WHERE h.smsa_msg_io = 'O' "
            + "AND t.smsa_closing_62f IS NOT NULL "
            + "AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) "
            + "AND (:currency IS NULL OR t.smsa_msg_close_ccy = :currency) "
            + "AND h.smsa_mt_code IN (:mtCodes) "
            + // ✅ Same fix for count query
            "AND ( :geoIds IS NULL OR h.smsa_geo_id IN (:geoIds) ) ",
            nativeQuery = true)
    Page<NostroClosingBalanceProjection> getClosingBalances(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("mtCodes") List<Integer> mtCodes,
            @Param("geoIds") List<String> geoIds,
            @Param("accountNumber") String accountNumber,
            @Param("currency") String currency,
            Pageable pageable
    );

}
