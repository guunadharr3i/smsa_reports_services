package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Service
public class NostroTurnoverService {

    private static final Logger logger = LogManager.getLogger(NostroTurnoverService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Map<String, String>> filterNostroReportData(NostroClosingBalanceFilter filter, Pageable pageable) {
        logger.info("Entered filterNostroReportData() with filter: {} and pageable: {}", filter, pageable);
        long startTime = System.currentTimeMillis();

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Base SELECT ----------
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_MSG_IO AS Identifier, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("t.smsa_trxn_acc_id AS AccountNumber, ");
            queryBuilder.append("JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS NostroName, ");
            queryBuilder.append("h.smsa_file_date AS SendRecTime, ");
            queryBuilder.append("jt.valueDate AS ValueDate, ");
            queryBuilder.append("COALESCE( ");
            queryBuilder.append("   JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency'), ");
            queryBuilder.append("   JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency') ");
            queryBuilder.append(") AS Currency, ");
            queryBuilder.append("jt.reference AS Reference, ");
            queryBuilder.append("jt.fcode AS Code, ");
            queryBuilder.append("jt.entryDate AS EntryDate, ");
            queryBuilder.append("jt.debitCreditMark AS DebitCreditMark, ");
            queryBuilder.append("h.SMSA_MIOR_REF AS MIR_MOR ");
            queryBuilder.append("FROM smsa_msg_txt t ");
            queryBuilder.append("JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id, ");
            queryBuilder.append("     JSON_TABLE(t.smsa_msg_stmnt, '$[*]' ");
            queryBuilder.append("         COLUMNS ( ");
            queryBuilder.append("             valueDate VARCHAR2(50) PATH '$.valueDate', ");
            queryBuilder.append("             entryDate VARCHAR2(10) PATH '$.entryDate', ");
            queryBuilder.append("             reference VARCHAR2(50) PATH '$.reference', ");
            queryBuilder.append("             amount VARCHAR2(20) PATH '$.amount', ");
            queryBuilder.append("             debitCreditMark VARCHAR2(1) PATH '$.debitCreditMark', ");
            queryBuilder.append("             fcode VARCHAR2(10) PATH '$.fcode' ");
            queryBuilder.append("         ) ");
            queryBuilder.append("     ) jt ");
            queryBuilder.append("WHERE h.SMSA_MSG_IO = 'O' ");
            queryBuilder.append("  AND jt.reference IS NOT NULL ");

            // ---------- Apply Filters ----------
            if (filter.getMtCodes() != null && !filter.getMtCodes().isEmpty()) {
                queryBuilder.append("  AND h.SMSA_MT_CODE IN :mtCodes ");
            }
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                queryBuilder.append("  AND h.SMSA_GEO_ID IN :geoIds ");
            }
            if (filter.getFromDate() != null) {
                queryBuilder.append("  AND h.SMSA_FILE_DATE >= :fromDate ");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append("  AND h.SMSA_FILE_DATE < :toDate ");
            }
            if (filter.getAccountNumber() != null && !filter.getAccountNumber().isEmpty()) {
                queryBuilder.append("  AND t.smsa_trxn_acc_id = :accountNumber ");
            }

            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE, jt.entryDate ");
            logger.debug("Constructed SQL Query: {}", queryBuilder);

            // ---------- Count Query ----------
            String countQueryStr = "SELECT COUNT(*) FROM (" + removeOrderBy(queryBuilder.toString()) + ") cnt";
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            bindNostroReportParams(countQuery, filter);
            Long totalElements = ((Number) countQuery.getSingleResult()).longValue();
            logger.info("Total elements found: {}", totalElements);

            // ---------- Data Query ----------
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindNostroReportParams(query, filter);

            if (pageable != null) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
                logger.debug("Pagination applied - Offset: {}, PageSize: {}", pageable.getOffset(), pageable.getPageSize());
            }

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            logger.info("Fetched {} records from DB.", results.size());

            for (Object[] row : results) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Identifier", getValue(row, 0));
                map.put("Sender", getValue(row, 1));
                map.put("Receiver", getValue(row, 2));
                map.put("MessageType", getValue(row, 3));
                map.put("AccountNumber", getValue(row, 4));
                map.put("NostroName", getValue(row, 5));
                map.put("SendRecTime", getValue(row, 6));
                map.put("ValueDate", getValue(row, 7));
                map.put("Currency", getValue(row, 8));
                map.put("Reference", getValue(row, 9));
                map.put("Code", getValue(row, 10));
                map.put("EntryDate", getValue(row, 11));
                map.put("DebitCreditMark", getValue(row, 12));
                map.put("MIRMOR", getValue(row, 13));
                dataMapList.add(map);
            }

            logger.info("Completed mapping {} records.", dataMapList.size());
            return new PageImpl<>(dataMapList, pageable, totalElements);

        } catch (Exception e) {
            logger.error("Error in filterNostroReportData(): {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.info("Exiting filterNostroReportData(). Execution time: {} ms", (System.currentTimeMillis() - startTime));
        }
    }

    private void bindNostroReportParams(Query query, NostroClosingBalanceFilter filter) {
        logger.debug("Binding query parameters for filter: {}", filter);
        if (filter.getFromDate() != null) {
            query.setParameter("fromDate", filter.getFromDate());
        }
        if (filter.getToDate() != null) {
            query.setParameter("toDate", filter.getToDate());
        }
        if (filter.getMtCodes() != null && !filter.getMtCodes().isEmpty()) {
            query.setParameter("mtCodes", filter.getMtCodes());
        }
        if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
            query.setParameter("geoIds", filter.getGeoIds());
        }
        if (filter.getAccountNumber() != null && !filter.getAccountNumber().isEmpty()) {
            query.setParameter("accountNumber", filter.getAccountNumber());
        }
    }

    private String removeOrderBy(String sql) {
        return sql.replaceAll("(?i)ORDER\\s+BY[\\s\\S]*", "");
    }

    private String getValue(Object[] row, int index) {
        return (row[index] != null) ? row[index].toString() : "";
    }

    public List<Map<String, String>> downloadNostroTurnOverReport(NostroClosingBalanceFilter filter) {
        logger.info("Entered downloadNostroTurnOverReport() with filter: {}", filter);
        long startTime = System.currentTimeMillis();

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Base SELECT ----------
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_MSG_IO AS Identifier, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("t.smsa_trxn_acc_id AS AccountNumber, ");
            queryBuilder.append("JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS NostroName, ");
            queryBuilder.append("h.smsa_file_date AS SendRecTime, ");
            queryBuilder.append("jt.valueDate AS ValueDate, ");
            queryBuilder.append("COALESCE( ");
            queryBuilder.append("   JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency'), ");
            queryBuilder.append("   JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency') ");
            queryBuilder.append(") AS Currency, ");
            queryBuilder.append("jt.reference AS Reference, ");
            queryBuilder.append("jt.fcode AS Code, ");
            queryBuilder.append("jt.entryDate AS EntryDate, ");
            queryBuilder.append("jt.debitCreditMark AS DebitCreditMark, ");
            queryBuilder.append("h.SMSA_MIOR_REF AS MIR_MOR ");
            queryBuilder.append("FROM smsa_msg_txt t ");
            queryBuilder.append("JOIN smsa_prt_message_hdr h ON t.smsa_message_id = h.smsa_message_id, ");
            queryBuilder.append("     JSON_TABLE(t.smsa_msg_stmnt, '$[*]' ");
            queryBuilder.append("         COLUMNS ( ");
            queryBuilder.append("             valueDate VARCHAR2(50) PATH '$.valueDate', ");
            queryBuilder.append("             entryDate VARCHAR2(10) PATH '$.entryDate', ");
            queryBuilder.append("             reference VARCHAR2(50) PATH '$.reference', ");
            queryBuilder.append("             amount VARCHAR2(20) PATH '$.amount', ");
            queryBuilder.append("             debitCreditMark VARCHAR2(1) PATH '$.debitCreditMark', ");
            queryBuilder.append("             fcode VARCHAR2(10) PATH '$.fcode' ");
            queryBuilder.append("         ) ");
            queryBuilder.append("     ) jt ");
            queryBuilder.append("WHERE h.SMSA_MSG_IO = 'O' ");
            queryBuilder.append("  AND jt.reference IS NOT NULL ");

            // ---------- Apply Filters ----------
            if (filter.getMtCodes() != null && !filter.getMtCodes().isEmpty()) {
                queryBuilder.append("  AND h.SMSA_MT_CODE IN :mtCodes ");
            }
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                queryBuilder.append("  AND h.SMSA_GEO_ID IN :geoIds ");
            }
            if (filter.getFromDate() != null) {
                queryBuilder.append("  AND h.SMSA_FILE_DATE >= :fromDate ");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append("  AND h.SMSA_FILE_DATE < :toDate ");
            }
            if (filter.getAccountNumber() != null && !filter.getAccountNumber().isEmpty()) {
                queryBuilder.append("  AND t.smsa_trxn_acc_id = :accountNumber ");
            }
            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE, jt.entryDate ");

            int batchSize = 1000;
            int offset = 0;
            logger.debug("Starting batch download process with batch size: {}", batchSize);

            while (true) {
                Query query = entityManager.createNativeQuery(queryBuilder.toString());
                bindNostroReportParams(query, filter);
                query.setFirstResult(offset);
                query.setMaxResults(batchSize);

                List<Object[]> results = query.getResultList();
                if (results.isEmpty()) {
                    logger.info("No more records found. Ending batch processing at offset: {}", offset);
                    break;
                }

                logger.debug("Fetched {} records for batch starting at offset {}", results.size(), offset);

                for (Object[] row : results) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("Identifier", getValue(row, 0));
                    map.put("Sender", getValue(row, 1));
                    map.put("Receiver", getValue(row, 2));
                    map.put("Message_Type", getValue(row, 3));
                    map.put("AccountNumber", getValue(row, 4));
                    map.put("NostroName", getValue(row, 5));
                    map.put("SendRecTime", getValue(row, 6));
                    map.put("ValueDate", getValue(row, 7));
                    map.put("Currency", getValue(row, 8));
                    map.put("Reference", getValue(row, 9));
                    map.put("Code", getValue(row, 10));
                    map.put("EntryDate", getValue(row, 11));
                    map.put("DebitCreditMark", getValue(row, 12));
                    map.put("MIR_MOR", getValue(row, 13));
                    dataMapList.add(map);
                }

                offset += batchSize;
            }

            logger.info("Completed downloadNostroTurnOverReport() successfully. Total records: {}", dataMapList.size());
            return dataMapList;

        } catch (Exception e) {
            logger.error("Error in downloadNostroTurnOverReport(): {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.info("Exiting downloadNostroTurnOverReport(). Execution time: {} ms", (System.currentTimeMillis() - startTime));
        }
    }
}
