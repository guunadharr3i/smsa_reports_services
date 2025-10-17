package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NostroClosingBalanceService {

    private static final Logger logger = LogManager.getLogger(NostroClosingBalanceService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Map<String, String>> filterNostroClosingBalanceData(NostroClosingBalanceFilter filter, Pageable pageable) {
        logger.info("Starting filterNostroClosingBalanceData with filter: {} and pageable: {}", filter, pageable);

        filter.setMtCodes(Arrays.asList(940, 950));
        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Base SELECT ----------
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

            // ---------- Apply Filters ----------
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
                queryBuilder.append(" AND na.SMSA_NOSTRO_ACCOUNT = :accountNumber ");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND t.SMSA_MSG_CLOSE_CCY = :currency ");
            }

            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC ");
            logger.info("Constructed SQL Query: {}", queryBuilder);

            // ---------- Count Query ----------
            String countQueryStr = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ")";
            Query countQuery = entityManager.createNativeQuery(countQueryStr);
            bindNostroParams(countQuery, filter);
            Long totalElements = ((Number) countQuery.getSingleResult()).longValue();
            logger.info("Total records found: {}", totalElements);

            // ---------- Main Query ----------
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindNostroParams(query, filter);

            if (pageable != null) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
                logger.info("Pagination applied: Offset={}, PageSize={}", pageable.getOffset(), pageable.getPageSize());
            }

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            logger.info("Fetched {} records for current page.", results.size());

            for (Object[] row : results) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                dataMap.put("Location", toStringSafe(row[0]));
                dataMap.put("SenderBIC", toStringSafe(row[1]));
                dataMap.put("ReceiverBIC", toStringSafe(row[2]));
                dataMap.put("MessageType", toStringSafe(row[3]));
                dataMap.put("NostroAccountNumber", toStringSafe(row[4]));
                dataMap.put("SenderName", toStringSafe(row[5]));
                dataMap.put("Currency", toStringSafe(row[6]));
                dataMap.put("StatementReceivedDate", toStringSafe(row[7]));
                dataMap.put("ValueDate", toStringSafe(row[8]));
                dataMap.put("Amount", toStringSafe(row[9]));
                dataMap.put("DRCR", toStringSafe(row[10]));
                dataMapList.add(dataMap);
            }

            logger.info("Mapped {} records successfully.", dataMapList.size());
            return new PageImpl<>(dataMapList, pageable, totalElements);
        } catch (Exception e) {
            logger.error("Error while filtering Nostro Closing Balance data: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void bindNostroParams(Query query, NostroClosingBalanceFilter filter) {
        logger.info("Binding query parameters for filter: {}", filter);

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

    public List<Map<String, String>> downloadNostroClosingBalanceData(NostroClosingBalanceFilter filter) {
        logger.info("Starting downloadNostroClosingBalanceData with filter: {}", filter);
        filter.setMtCodes(Arrays.asList(940, 950));
        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        try {
            // ---------- Build Query ----------
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
                queryBuilder.append(" AND na.SMSA_NOSTRO_ACCOUNT = :accountNumber ");
            }
            if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
                queryBuilder.append(" AND t.SMSA_MSG_CLOSE_CCY = :currency ");
            }

            queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC ");
            logger.info("Constructed Download Query: {}", queryBuilder);

            int batchSize = 1000;
            int offset = 0;
            int totalFetched = 0;

            while (true) {
                Query query = entityManager.createNativeQuery(queryBuilder.toString());
                bindNostroParams(query, filter);
                query.setFirstResult(offset);
                query.setMaxResults(batchSize);

                List<Object[]> results = query.getResultList();
                if (results.isEmpty()) {
                    logger.info("No more data found. Download complete. Total rows fetched: {}", totalFetched);
                    break;
                }

                for (Object[] row : results) {
                    Map<String, String> dataMap = new LinkedHashMap<>();
                    dataMap.put("Location", toStringSafe(row[0]));
                    dataMap.put("Sender BIC", toStringSafe(row[1]));
                    dataMap.put("Receiver BIC", toStringSafe(row[2]));
                    dataMap.put("Message Type", toStringSafe(row[3]));
                    dataMap.put("Nostro Account Number", toStringSafe(row[4]));
                    dataMap.put("Sender Name", toStringSafe(row[5]));
                    dataMap.put("Currency", toStringSafe(row[6]));
                    dataMap.put("Statement Received Date", toStringSafe(row[7]));
                    dataMap.put("Value Date", toStringSafe(row[8]));
                    dataMap.put("Amount", toStringSafe(row[9]));
                    dataMap.put("DR/CR", toStringSafe(row[10]));
                    dataMapList.add(dataMap);
                }

                offset += batchSize;
                totalFetched += results.size();
                logger.info("Batch fetched: {} rows (Total so far: {})", results.size(), totalFetched);
            }

            logger.info("Total records fetched for download: {}", dataMapList.size());
            return dataMapList;
        } catch (Exception e) {
            logger.error("Error while downloading Nostro Closing Balance data: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String toStringSafe(Object value) {
        return value != null ? value.toString() : "";
    }
}
