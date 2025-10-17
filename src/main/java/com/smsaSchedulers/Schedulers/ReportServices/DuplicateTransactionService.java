package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DuplicateTransactionService {

    private static final Logger logger = LogManager.getLogger(DuplicateTransactionService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Map<String, String>> filterDuplicateTransactionData(DuplicateTransactionFilter filter, Pageable pageable) {
        logger.info("Entering filterDuplicateTransactionData() with filter: {} and pageable: {}", filter, pageable);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Base SELECT ----------
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_MSG_IO AS Identifier, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("h.SMSA_TXN_REF AS Reference_No, ");
            queryBuilder.append("h.SMSA_TRXN_RLTD_REFN AS Related_Reference_No, ");
            queryBuilder.append("h.SMSA_MSG_VALDATE AS ValueDate, ");
            queryBuilder.append("h.SMSA_MSG_CURRENCY AS Currency, ");
            queryBuilder.append("h.SMSA_TXN_AMOUNT AS Amount, ");
            queryBuilder.append("h.SMSA_GEO_ID AS Geo_ID, ");
            queryBuilder.append("h.SMSA_FILE_TYPE AS File_Type, ");
            queryBuilder.append("h.SMSA_FILE_DATE AS File_Date, ");
            queryBuilder.append("h.SMSA_FILE_TIME AS File_Time, ");
            queryBuilder.append("h.SMSA_MIOR_REF AS MIR_MOR ");
            queryBuilder.append("FROM smsa_prt_message_hdr h ");

            // ---------- JOIN with duplicate detection ----------
            queryBuilder.append("JOIN ( ");
            queryBuilder.append("SELECT s.SMSA_MSG_IO, s.SMSA_SENDER_BIC, s.SMSA_RECEIVER_BIC, s.SMSA_MT_CODE, ");
            queryBuilder.append("s.SMSA_TXN_REF, s.SMSA_TRXN_RLTD_REFN, s.SMSA_MSG_VALDATE, ");
            queryBuilder.append("s.SMSA_MSG_CURRENCY, s.SMSA_TXN_AMOUNT ");
            queryBuilder.append("FROM smsa_prt_message_hdr s ");
            queryBuilder.append("WHERE 1=1 ");

            // ---------- Required non-null conditions ----------
            queryBuilder.append(" AND s.SMSA_MSG_IO IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_SENDER_BIC IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_RECEIVER_BIC IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MT_CODE IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TXN_REF IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TRXN_RLTD_REFN IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MSG_CURRENCY IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TXN_AMOUNT IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MSG_VALDATE IS NOT NULL ");

            // ---------- Inner filters ----------
            if (filter.getFromDate() != null) {
                queryBuilder.append(" AND s.SMSA_FILE_DATE >= :fromDate");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append(" AND s.SMSA_FILE_DATE <= :toDate");
            }
            if (filter.getReferenceNo() != null && !filter.getReferenceNo().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_TXN_REF = :referenceNo");
            }
            if (filter.getRelatedReferenceNo() != null && !filter.getRelatedReferenceNo().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_TRXN_RLTD_REFN = :relatedReferenceNo");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_MSG_CURRENCY = :currency ");
            }
            if (filter.getGeographies() != null && !filter.getGeographies().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_GEO_ID IN :geoList");
            }

            // ---------- Grouping for duplicates ----------
            queryBuilder.append(" GROUP BY s.SMSA_MSG_IO, s.SMSA_SENDER_BIC, s.SMSA_RECEIVER_BIC, s.SMSA_MT_CODE, ");
            queryBuilder.append("s.SMSA_TXN_REF, s.SMSA_TRXN_RLTD_REFN, s.SMSA_MSG_VALDATE, s.SMSA_MSG_CURRENCY, s.SMSA_TXN_AMOUNT ");
            queryBuilder.append("HAVING COUNT(*) > 1 ");
            queryBuilder.append(") dup ON h.SMSA_MSG_IO = dup.SMSA_MSG_IO ");
            queryBuilder.append("AND h.SMSA_SENDER_BIC = dup.SMSA_SENDER_BIC ");
            queryBuilder.append("AND h.SMSA_RECEIVER_BIC = dup.SMSA_RECEIVER_BIC ");
            queryBuilder.append("AND h.SMSA_MT_CODE = dup.SMSA_MT_CODE ");
            queryBuilder.append("AND h.SMSA_TXN_REF = dup.SMSA_TXN_REF ");
            queryBuilder.append("AND h.SMSA_TRXN_RLTD_REFN = dup.SMSA_TRXN_RLTD_REFN ");
            queryBuilder.append("AND h.SMSA_MSG_VALDATE = dup.SMSA_MSG_VALDATE ");
            queryBuilder.append("AND h.SMSA_MSG_CURRENCY = dup.SMSA_MSG_CURRENCY ");
            queryBuilder.append("AND h.SMSA_TXN_AMOUNT = dup.SMSA_TXN_AMOUNT ");
            queryBuilder.append("WHERE 1=1 ");

            // ---------- Outer filters ----------
            if (filter.getFromDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE >= :fromDate");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE <= :toDate");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND h.SMSA_MSG_CURRENCY = :currency ");
            }

            logger.info("Constructed duplicate transaction query: {}", queryBuilder);

            // --- Count query ---
            String countQueryStr = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ")";
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            bindDuplicateParams(countQuery, filter);
            Long totalElements = ((Number) countQuery.getSingleResult()).longValue();
            logger.info("Total records found for filterDuplicateTransactionData(): {}", totalElements);

            // --- Main query ---
            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC");
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindDuplicateParams(query, filter);

            if (pageable != null) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
                logger.info("Pagination applied: offset={}, size={}", pageable.getOffset(), pageable.getPageSize());
            }

            List<Object[]> results = query.getResultList();
            logger.info("Fetched {} records from database", results.size());

            for (Object[] row : results) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                dataMap.put("Identifier", row[0] != null ? row[0].toString() : "");
                dataMap.put("Sender", row[1] != null ? row[1].toString() : "");
                dataMap.put("Receiver", row[2] != null ? row[2].toString() : "");
                dataMap.put("Message Type", row[3] != null ? row[3].toString() : "");
                dataMap.put("Reference No", row[4] != null ? row[4].toString() : "");
                dataMap.put("Related Ref No", row[5] != null ? row[5].toString() : "");
                dataMap.put("ValueDate", row[6] != null ? row[6].toString() : "");
                dataMap.put("CCY", row[7] != null ? row[7].toString() : "");
                dataMap.put("Amount", row[8] != null ? row[8].toString() : "");
                dataMap.put("Location", row[9] != null ? row[9].toString() : "");
                dataMap.put("FileTypeA/N", row[10] != null ? row[10].toString() : "");
                dataMap.put("Send/Rec Date", row[11] != null ? row[11].toString() : "");
                dataMap.put("Send/Rec Time", row[12] != null ? row[12].toString() : "");
                dataMap.put("MIR/MOR", row[13] != null ? row[13].toString() : "");
                dataMapList.add(dataMap);
            }

            logger.info("Successfully mapped {} records", dataMapList.size());
            return new PageImpl<>(dataMapList, pageable, totalElements);

        } catch (Exception e) {
            logger.error("Error in filterDuplicateTransactionData(): {}", e.getMessage(), e);
            throw e;
        }
    }

    private void bindDuplicateParams(Query query, DuplicateTransactionFilter filter) {
        logger.info("Binding query parameters for filter: {}", filter);
        if (filter.getFromDate() != null) {
            query.setParameter("fromDate", filter.getFromDate());
        }
        if (filter.getToDate() != null) {
            query.setParameter("toDate", filter.getToDate());
        }
        if (filter.getReferenceNo() != null && !filter.getReferenceNo().isEmpty()) {
            query.setParameter("referenceNo", filter.getReferenceNo());
        }
        if (filter.getRelatedReferenceNo() != null && !filter.getRelatedReferenceNo().isEmpty()) {
            query.setParameter("relatedReferenceNo", filter.getRelatedReferenceNo());
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            query.setParameter("currency", filter.getCurrency());
        }
        if (filter.getGeographies() != null && !filter.getGeographies().isEmpty()) {
            query.setParameter("geoList", filter.getGeographies());
        }
    }

    public List<Map<String, String>> filterDownloadDuplicateTransactionFilterData(DuplicateTransactionFilter filter) {
        logger.info("Entering filterDownloadDuplicateTransactionFilterData() with filter: {}", filter);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // (same SQL building logic as before)
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_MSG_IO AS Identifier, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("h.SMSA_TXN_REF AS Reference_No, ");
            queryBuilder.append("h.SMSA_TRXN_RLTD_REFN AS Related_Reference_No, ");
            queryBuilder.append("h.SMSA_MSG_VALDATE AS ValueDate, ");
            queryBuilder.append("h.SMSA_MSG_CURRENCY AS Currency, ");
            queryBuilder.append("h.SMSA_TXN_AMOUNT AS Amount, ");
            queryBuilder.append("h.SMSA_GEO_ID AS Geo_ID, ");
            queryBuilder.append("h.SMSA_FILE_TYPE AS File_Type, ");
            queryBuilder.append("h.SMSA_FILE_DATE AS File_Date, ");
            queryBuilder.append("h.SMSA_FILE_TIME AS File_Time, ");
            queryBuilder.append("h.SMSA_MIOR_REF AS MIR_MOR ");
            queryBuilder.append("FROM smsa_prt_message_hdr h ");
            queryBuilder.append("JOIN ( ");
            queryBuilder.append("SELECT s.SMSA_MSG_IO, s.SMSA_SENDER_BIC, s.SMSA_RECEIVER_BIC, s.SMSA_MT_CODE, ");
            queryBuilder.append("s.SMSA_TXN_REF, s.SMSA_TRXN_RLTD_REFN, s.SMSA_MSG_VALDATE, ");
            queryBuilder.append("s.SMSA_MSG_CURRENCY, s.SMSA_TXN_AMOUNT ");
            queryBuilder.append("FROM smsa_prt_message_hdr s WHERE 1=1 ");
            queryBuilder.append(" AND s.SMSA_MSG_IO IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_SENDER_BIC IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_RECEIVER_BIC IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MT_CODE IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TXN_REF IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TRXN_RLTD_REFN IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MSG_CURRENCY IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_TXN_AMOUNT IS NOT NULL ");
            queryBuilder.append(" AND s.SMSA_MSG_VALDATE IS NOT NULL ");

            if (filter.getFromDate() != null) {
                queryBuilder.append(" AND s.SMSA_FILE_DATE >= :fromDate");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append(" AND s.SMSA_FILE_DATE <= :toDate");
            }
            if (filter.getReferenceNo() != null && !filter.getReferenceNo().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_TXN_REF = :referenceNo");
            }
            if (filter.getRelatedReferenceNo() != null && !filter.getRelatedReferenceNo().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_TRXN_RLTD_REFN = :relatedReferenceNo");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_MSG_CURRENCY = :currency ");
            }
            if (filter.getGeographies() != null && !filter.getGeographies().isEmpty()) {
                queryBuilder.append(" AND s.SMSA_GEO_ID IN :geoList");
            }

            queryBuilder.append(" GROUP BY s.SMSA_MSG_IO, s.SMSA_SENDER_BIC, s.SMSA_RECEIVER_BIC, s.SMSA_MT_CODE, ");
            queryBuilder.append("s.SMSA_TXN_REF, s.SMSA_TRXN_RLTD_REFN, s.SMSA_MSG_VALDATE, s.SMSA_MSG_CURRENCY, s.SMSA_TXN_AMOUNT ");
            queryBuilder.append("HAVING COUNT(*) > 1 ) dup ON h.SMSA_MSG_IO = dup.SMSA_MSG_IO ");
            queryBuilder.append("AND h.SMSA_SENDER_BIC = dup.SMSA_SENDER_BIC ");
            queryBuilder.append("AND h.SMSA_RECEIVER_BIC = dup.SMSA_RECEIVER_BIC ");
            queryBuilder.append("AND h.SMSA_MT_CODE = dup.SMSA_MT_CODE ");
            queryBuilder.append("AND h.SMSA_TXN_REF = dup.SMSA_TXN_REF ");
            queryBuilder.append("AND h.SMSA_TRXN_RLTD_REFN = dup.SMSA_TRXN_RLTD_REFN ");
            queryBuilder.append("AND h.SMSA_MSG_VALDATE = dup.SMSA_MSG_VALDATE ");
            queryBuilder.append("AND h.SMSA_MSG_CURRENCY = dup.SMSA_MSG_CURRENCY ");
            queryBuilder.append("AND h.SMSA_TXN_AMOUNT = dup.SMSA_TXN_AMOUNT ");
            queryBuilder.append("WHERE 1=1 ");

            if (filter.getFromDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE >= :fromDate");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE <= :toDate");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND h.SMSA_MSG_CURRENCY = :currency ");
            }
            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC");

            logger.info("Constructed download query for duplicates: {}", queryBuilder);

            int batchSize = 1000;
            int offset = 0;

            while (true) {
                Query query = entityManager.createNativeQuery(queryBuilder.toString());
                bindDuplicateParams(query, filter);
                query.setFirstResult(offset);
                query.setMaxResults(batchSize);

                List<Object[]> results = query.getResultList();
                logger.info("Fetched {} records in batch starting at offset {}", results.size(), offset);

                if (results.isEmpty()) {
                    break;
                }

                for (Object[] row : results) {
                    Map<String, String> dataMap = new LinkedHashMap<>();
                    dataMap.put("Identifier", row[0] != null ? row[0].toString() : "");
                    dataMap.put("Sender", row[1] != null ? row[1].toString() : "");
                    dataMap.put("Receiver", row[2] != null ? row[2].toString() : "");
                    dataMap.put("Message Type", row[3] != null ? row[3].toString() : "");
                    dataMap.put("Reference No", row[4] != null ? row[4].toString() : "");
                    dataMap.put("Related Ref No", row[5] != null ? row[5].toString() : "");
                    dataMap.put("ValueDate", row[6] != null ? row[6].toString() : "");
                    dataMap.put("CCY", row[7] != null ? row[7].toString() : "");
                    dataMap.put("Amount", row[8] != null ? row[8].toString() : "");
                    dataMap.put("Location", row[9] != null ? row[9].toString() : "");
                    dataMap.put("FileTypeA/N", row[10] != null ? row[10].toString() : "");
                    dataMap.put("Send/Rec Date", row[11] != null ? row[11].toString() : "");
                    dataMap.put("Send/Rec Time", row[12] != null ? row[12].toString() : "");
                    dataMap.put("MIR/MOR", row[13] != null ? row[13].toString() : "");
                    dataMapList.add(dataMap);
                }

                offset += batchSize;
            }

            logger.info("Completed filterDownloadDuplicateTransactionFilterData(), total records: {}", dataMapList.size());
            return dataMapList;

        } catch (Exception e) {
            logger.error("Error in filterDownloadDuplicateTransactionFilterData(): {}", e.getMessage(), e);
            throw e;
        }
    }
}
