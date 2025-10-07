/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftMessageHeader;
import com.smsaSchedulers.Schedulers.Pojo.NostroNegativeBalanceProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NostroNegativeBalanceRepository extends JpaRepository<SwiftMessageHeader, Long> {

    @Query(
            value = "SELECT g.geog_name AS location, "
            + "       h.smsa_sender_bic AS senderBic, "
            + "       h.smsa_receiver_bic AS receiverBic, "
            + "       h.smsa_mt_code AS messageType, "
            + "       h.smsa_file_date AS statementReceivedDate, "
            + "       t.smsa_trxn_acc_id AS nostroAccountNumber, "
            + "       JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS nostroName, "
            + "       t.smsa_closing_62f AS amount, "
            + "       t.smsa_msg_valdate62f AS valueDate, "
            + "       t.smsa_msg_close_cred AS debitCredit, "
            + "       COALESCE( "
            + "          JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency'), "
            + "          JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency') "
            + "       ) AS currency "
            + "FROM smsa_msg_txt t "
            + "JOIN smsa_prt_message_hdr h ON h.smsa_message_id = t.smsa_message_id "
            + "JOIN smsa_geo_mst g ON h.smsa_geo_id = g.geog_code "
            + "WHERE h.smsa_msg_io = 'O' "
            + "  AND t.smsa_closing_62f IS NOT NULL "
            + "  AND t.smsa_msg_close_cred = 'Debit' "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "  AND (h.smsa_mt_code IN (:mtCodes)) "
            + "  AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds)) "
            + "  AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) "
            + "ORDER BY h.smsa_file_date, h.smsa_mt_code, g.geog_name",
            countQuery = "SELECT COUNT(*) "
            + "FROM smsa_msg_txt t "
            + "JOIN smsa_prt_message_hdr h ON h.smsa_message_id = t.smsa_message_id "
            + "JOIN smsa_geo_mst g ON h.smsa_geo_id = g.geog_code "
            + "WHERE h.smsa_msg_io = 'O' "
            + "  AND t.smsa_closing_62f IS NOT NULL "
            + "  AND t.smsa_msg_close_cred = 'Debit' "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "  AND (h.smsa_mt_code IN (:mtCodes)) "
            + "  AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds)) "
            + "  AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) ",
            nativeQuery = true
    )
    Page<NostroNegativeBalanceProjection> findNegativeBalances(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("mtCodes") List<Integer> mtCodes,
            @Param("geoIds") List<String> geoIds,
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );

}
