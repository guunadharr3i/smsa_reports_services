package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NostroNegativeBalanceService {

    private static final Logger logger = LogManager.getLogger(NostroNegativeBalanceService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Map<String, String>> filterNostroNegativeBalanceData(NostroClosingBalanceFilter filter, Pageable pageable) {
        logger.info("Starting filterNostroNegativeBalanceData with filter: {} and pageable: {}", filter, pageable);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Base SELECT ----------
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_GEO_ID AS Geo_ID, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("t.SMSA_TRXN_ACC_ID AS Account_No, ");
            queryBuilder.append("JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS SenderBankName, ");
            queryBuilder.append("COALESCE( ");
            queryBuilder.append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62M_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR), ");
            queryBuilder.append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62F_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR) ");
            queryBuilder.append(") AS CURRENCY, ");
            queryBuilder.append("h.SMSA_FILE_DATE AS FileDate, ");
            queryBuilder.append("t.SMSA_MSG_VALDATE62F AS ValueDate, ");
            queryBuilder.append("t.SMSA_CLOSING_62F AS ClosingBalance, ");
            queryBuilder.append("t.SMSA_MSG_CLOSE_CRED AS CRDR ");
            queryBuilder.append("FROM SMSA_MSG_TXT t ");
            queryBuilder.append("INNER JOIN smsa_prt_message_hdr h ON h.SMSA_MESSAGE_ID = t.SMSA_MESSAGE_ID ");
            queryBuilder.append("WHERE h.SMSA_MSG_IO = 'O' AND t.SMSA_CLOSING_62F IS NOT NULL ");
            queryBuilder.append("AND t.smsa_msg_close_cred = 'Debit' ");

            // ---------- Apply Filters Dynamically ----------
            if (filter.getFromDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE >= :fromDate ");
            }
            if (filter.getToDate() != null) {
                queryBuilder.append(" AND h.SMSA_FILE_DATE <= :toDate ");
            }
            if (filter.getMtCodes() != null && !filter.getMtCodes().isEmpty()) {
                queryBuilder.append(" AND h.SMSA_MT_CODE IN :mtCodes ");
            }
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                queryBuilder.append(" AND h.SMSA_GEO_ID IN :geoIds ");
            }
            if (filter.getAccountNumber() != null && !filter.getAccountNumber().isEmpty()) {
                queryBuilder.append(" AND t.SMSA_TRXN_ACC_ID = :accountNumber ");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND t.SMSA_MSG_CLOSE_CCY = :currency ");
            }

            // ---------- Order ----------
            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC ");

            logger.info("Generated Query: {}", queryBuilder);

            // ---------- Count Query ----------
            String countQueryStr = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ")";
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            bindNostroParams(countQuery, filter);
            Long totalElements = ((Number) countQuery.getSingleResult()).longValue();
            logger.info("Total elements found: {}", totalElements);

            // ---------- Main Query ----------
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindNostroParams(query, filter);

            if (pageable != null) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
                logger.info("Pagination applied - Offset: {}, Page Size: {}", pageable.getOffset(), pageable.getPageSize());
            }

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            logger.info("Fetched {} records from database", results.size());

            // ---------- Map Results ----------
            for (Object[] row : results) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                dataMap.put("Location", toStr(row[0]));
                dataMap.put("SenderBIC", toStr(row[1]));
                dataMap.put("ReceiverBIC", toStr(row[2]));
                dataMap.put("MessageType", toStr(row[3]));
                dataMap.put("NostroAccountNumber", toStr(row[4]));
                dataMap.put("SenderName", toStr(row[5]));
                dataMap.put("Currency", toStr(row[6]));
                dataMap.put("StatementReceivedDate", toStr(row[7]));
                dataMap.put("ValueDate", toStr(row[8]));
                dataMap.put("Amount", toStr(row[9]));
                dataMap.put("DRCR", toStr(row[10]));
                dataMapList.add(dataMap);
            }

            logger.info("Mapped {} records successfully.", dataMapList.size());
            return new PageImpl<>(dataMapList, pageable, totalElements);

        } catch (Exception e) {
            logger.error("Error while filtering Nostro Negative Balance Data: ", e);
            throw e;
        }
    }

    private void bindNostroParams(Query query, NostroClosingBalanceFilter filter) {
        logger.info("Binding parameters for query: {}", filter);
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
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            query.setParameter("currency", filter.getCurrency());
        }
    }

    private String toStr(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    public List<Map<String, String>> downloadNostroClosingBalanceData(NostroClosingBalanceFilter filter) {
        logger.info("Starting downloadNostroClosingBalanceData with filter: {}", filter);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            queryBuilder.append("SELECT ");
            queryBuilder.append("h.SMSA_GEO_ID AS Geo_ID, ");
            queryBuilder.append("h.SMSA_SENDER_BIC AS Sender, ");
            queryBuilder.append("h.SMSA_RECEIVER_BIC AS Receiver, ");
            queryBuilder.append("h.SMSA_MT_CODE AS Message_Type, ");
            queryBuilder.append("na.SMSA_NOSTRO_ACCOUNT AS Account_No, ");
            queryBuilder.append("JSON_VALUE(h.smsa_hdr_obj, '$.Receiver_obj.BANK_NAME') AS SenderBankName, ");
            queryBuilder.append("t.SMSA_MSG_CLOSE_CCY AS Currency, ");
            queryBuilder.append("h.SMSA_FILE_DATE AS FileDate, ");
            queryBuilder.append("h.SMSA_MSG_VALDATE AS ValueDate, ");
            queryBuilder.append("t.SMSA_CLOSING_62F AS ClosingBalance, ");
            queryBuilder.append("t.SMSA_MSG_CLOSE_CRED AS CreditFlag ");
            queryBuilder.append("FROM smsa_prt_message_hdr h ");
            queryBuilder.append("INNER JOIN SMSA_NOSTRO_ACCOUNT_MASTER na ON na.SMSA_MESSAGE_ID = h.SMSA_MESSAGE_ID ");
            queryBuilder.append("INNER JOIN SMSA_MSG_TXT t ON h.SMSA_MESSAGE_ID = t.SMSA_MESSAGE_ID ");
            queryBuilder.append("WHERE h.SMSA_MSG_IO = 'O' AND t.SMSA_CLOSING_62F IS NOT NULL ");
            logger.info("Generated Download Query: {}", queryBuilder);

            int batchSize = 1000;
            int offset = 0;

            while (true) {
                Query query = entityManager.createNativeQuery(queryBuilder.toString());
                bindNostroParams(query, filter);
                query.setFirstResult(offset);
                query.setMaxResults(batchSize);
                logger.info("Fetching batch with offset: {}", offset);

                List<Object[]> results = query.getResultList();
                if (results.isEmpty()) {
                    logger.info("No more results found. Stopping batch fetch.");
                    break;
                }

                for (Object[] row : results) {
                    Map<String, String> dataMap = new LinkedHashMap<>();
                    dataMap.put("Location", toStr(row[0]));
                    dataMap.put("Sender BIC", toStr(row[1]));
                    dataMap.put("Receiver BIC", toStr(row[2]));
                    dataMap.put("Message Type", toStr(row[3]));
                    dataMap.put("Nostro Account Number", toStr(row[4]));
                    dataMap.put("Sender Name", toStr(row[5]));
                    dataMap.put("Currency", toStr(row[6]));
                    dataMap.put("Statement Received Date", toStr(row[7]));
                    dataMap.put("Value Date", toStr(row[8]));
                    dataMap.put("Amount", toStr(row[9]));
                    dataMap.put("DR/CR", toStr(row[10]));
                    dataMapList.add(dataMap);
                }

                offset += batchSize;
            }

            logger.info("Download completed with total {} records.", dataMapList.size());
            return dataMapList;

        } catch (Exception e) {
            logger.error("Error while downloading Nostro Closing Balance Data: ", e);
            throw e;
        }
    }
}
