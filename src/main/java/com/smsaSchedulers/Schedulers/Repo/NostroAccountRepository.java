package com.smsaSchedulers.Schedulers.Repo;

import com.smsaSchedulers.Schedulers.Entity.SwiftMessageHeader;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NostroAccountRepository extends JpaRepository<SwiftMessageHeader, Long> {

    @Query(value = "WITH base AS ( "
            + " SELECT "
            + "   h.smsa_file_name, "
            + "   t.smsa_message_id, "
            + "   h.smsa_mt_code AS messageType, "
            + "   h.smsa_sender_bic AS sender, "
            + "   h.smsa_receiver_bic AS receiver, "
            + "   t.smsa_trxn_acc_id AS accountNo, "
            + "   JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS nostroName, "
            + "   h.smsa_file_date AS statementReceivedDate, "
            + "   jt.valueDate, jt.entryDate, jt.reference, jt.amount, jt.fcode "
            + " FROM smsa_msg_txt t "
            + " JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id "
            + " CROSS JOIN JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "   COLUMNS (valueDate VARCHAR2(50) PATH '$.valueDate', "
            + "            entryDate VARCHAR2(10) PATH '$.entryDate', "
            + "            reference VARCHAR2(50) PATH '$.reference', "
            + "            amount VARCHAR2(20) PATH '$.amount', "
            + "            fcode VARCHAR2(10) PATH '$.fcode')) jt "
            + " WHERE h.smsa_msg_io = 'O' "
            + "   AND (:sender IS NULL OR h.smsa_sender_bic LIKE :sender) "
            + "   AND (:receiver IS NULL OR h.smsa_receiver_bic LIKE :receiver) "
            + "   AND (:accountNo IS NULL OR t.smsa_trxn_acc_id = :accountNo) "
            + "   AND (:currency IS NULL OR t.smsa_msg_close_ccy = :currency) "
            + "   AND (:transactionReference IS NULL OR jt.reference LIKE :transactionReference) "
            + "   AND (:relatedReference IS NULL OR jt.reference LIKE :relatedReference) "
            + "   AND (:text IS NULL OR jt.reference LIKE :text) "
            + "   AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "   AND (:toDate IS NULL OR h.smsa_file_date <= :toDate) "
            + "   AND (:valueDateFrom IS NULL OR t.smsa_msg_valdate62f >= :valueDateFrom) "
            + "   AND (:valueDateTo IS NULL OR t.smsa_msg_valdate62f <= :valueDateTo) "
            + "   AND h.smsa_mt_code IN (:messageTypes) "
            + "   AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds)) "
            + ") SELECT * FROM base ",
            countQuery = "SELECT COUNT(*) "
            + "FROM smsa_msg_txt t "
            + "JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id "
            + "WHERE h.smsa_msg_io = 'O' "
            + "  AND (:sender IS NULL OR h.smsa_sender_bic LIKE :sender) "
            + "  AND (:receiver IS NULL OR h.smsa_receiver_bic LIKE :receiver) "
            + "  AND (:accountNo IS NULL OR t.smsa_trxn_acc_id = :accountNo) "
            + "  AND (:currency IS NULL OR t.smsa_msg_close_ccy = :currency) "
            + "  AND (:transactionReference IS NULL OR EXISTS ( "
            + "        SELECT 1 FROM JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "          COLUMNS (reference VARCHAR2(50) PATH '$.reference')) jt "
            + "        WHERE jt.reference LIKE :transactionReference)) "
            + "  AND (:relatedReference IS NULL OR EXISTS ( "
            + "        SELECT 1 FROM JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "          COLUMNS (reference VARCHAR2(50) PATH '$.reference')) jt "
            + "        WHERE jt.reference LIKE :relatedReference)) "
            + "  AND (:text IS NULL OR EXISTS ( "
            + "        SELECT 1 FROM JSON_TABLE(t.smsa_msg_stmnt, '$[*]' "
            + "          COLUMNS (reference VARCHAR2(50) PATH '$.reference')) jt "
            + "        WHERE jt.reference LIKE :text)) "
            + "  AND (:fromDate IS NULL OR h.smsa_file_date >= :fromDate) "
            + "  AND (:toDate IS NULL OR h.smsa_file_date <= :toDate) "
            + "  AND h.smsa_mt_code IN (:messageTypes) "
            + "  AND (:geoIds IS NULL OR h.smsa_geo_id IN (:geoIds))"
            + "   AND (:valueDateFrom IS NULL OR t.smsa_msg_valdate62f >= :valueDateFrom) "
            + "   AND (:valueDateTo IS NULL OR t.smsa_msg_valdate62f <= :valueDateTo) ",
            nativeQuery = true
    )
    Page<NostroAccountProjection> findAccounts(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("valueDateFrom") LocalDate valueDateFrom,
            @Param("valueDateTo") LocalDate valueDateTo,
            @Param("sender") String sender,
            @Param("receiver") String receiver,
            @Param("accountNo") String accountNo,
            @Param("currency") String currency,
            @Param("transactionReference") String transactionReference,
            @Param("relatedReference") String relatedReference,
            @Param("text") String text,
            @Param("messageTypes") List<Integer> messageTypes,
            @Param("geoIds") List<String> geoIds,
            Pageable pageable);
}
