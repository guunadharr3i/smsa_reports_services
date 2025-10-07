/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftMessageHeader;
import com.smsaSchedulers.Schedulers.Pojo.NostroTurnoverProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NostroTurnoverRepository extends JpaRepository<SwiftMessageHeader, Long> {

    @Query(
            value = "SELECT t.smsa_message_id, "
            + "       h.smsa_msg_io, "
            + "       h.smsa_geo_id, "
            + "       t.smsa_trxn_acc_id AS accountNumber, "
            + "       JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS nostroName, "
            + "       h.smsa_mt_code AS messageType, "
            + "       h.smsa_sender_bic AS senderBic, "
            + "       h.smsa_receiver_bic AS receiverBic, "
            + "       h.smsa_file_date AS sendRecTime, "
            + "       COALESCE( "
            + "          JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency'), "
            + "          JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency') "
            + "       ) AS currency, "
            + "       jt.valueDate AS valueDate, "
            + "       jt.entryDate AS entryDate, "
            + "       jt.fcode AS code, "
            + "       jt.reference AS referenceNumber, "
            + "       jt.amount AS amount, "
            + "       jt.debitCreditMark AS debitCredit "
            + "FROM smsa_msg_txt t "
            + "JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id, "
            + "     JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "         COLUMNS ( "
            + "             valueDate VARCHAR2(50) PATH '$.valueDate', "
            + "             entryDate VARCHAR2(10) PATH '$.entryDate', "
            + "             reference VARCHAR2(50) PATH '$.reference', "
            + "             amount VARCHAR2(20) PATH '$.amount', "
            + "             debitCreditMark VARCHAR2(1) PATH '$.debitCreditMark', "
            + "             fcode VARCHAR2(10) PATH '$.fcode' "
            + "         ) "
            + "     ) jt "
            + "WHERE (h.smsa_mt_code IN (:mtCodes)) "
            + "  AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds)) "
            + "  AND h.smsa_msg_io = 'O' "
            + "  AND jt.reference IS NOT NULL "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "  AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) "
            + "ORDER BY h.smsa_file_date, jt.entryDate",
            countQuery = "SELECT COUNT(*) "
            + "FROM smsa_msg_txt t "
            + "JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id, "
            + "     JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "         COLUMNS ( "
            + "             valueDate VARCHAR2(50) PATH '$.valueDate', "
            + "             entryDate VARCHAR2(10) PATH '$.entryDate', "
            + "             reference VARCHAR2(50) PATH '$.reference', "
            + "             amount VARCHAR2(20) PATH '$.amount', "
            + "             debitCreditMark VARCHAR2(1) PATH '$.debitCreditMark', "
            + "             fcode VARCHAR2(10) PATH '$.fcode' "
            + "         ) "
            + "     ) jt "
            + "WHERE (h.smsa_mt_code IN (:mtCodes)) "
            + "  AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds)) "
            + "  AND h.smsa_msg_io = 'O' "
            + "  AND jt.reference IS NOT NULL "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date < :toDate) "
            + "  AND (:accountNumber IS NULL OR t.smsa_trxn_acc_id = :accountNumber) ",
            nativeQuery = true
    )
    Page<NostroTurnoverProjection> findTurnover(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("mtCodes") List<Integer> mtCodes,
            @Param("geoIds") List<String> geoIds,
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );

}
