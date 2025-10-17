package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationProjection;
import com.smsaSchedulers.Schedulers.Repo.DailyValidationRepository;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DailyValidationService {

    private static final Logger logger = LogManager.getLogger(DailyValidationService.class);

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Fetch complete data for CSV/Excel export — with batch fetching and
     * detailed logging
     * @param filter
     * @return 
     */
    public List<Map<String, String>> filterDownloadValidationFilterData(DailyValidationFilter filter) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting CSV download data fetch for Daily Validation | Filter: {}", filter);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        // --- Base SELECT ---
        queryBuilder.append("SELECT ")
                .append("TO_CHAR(h.SMSA_FILE_DATE, 'DD-MON-YYYY') AS M_SEND_REC_DATE, ")
                .append("h.SMSA_MSG_IO AS IO, ")
                .append("h.SMSA_MT_CODE AS MESSAGE_TYPE, ")
                .append("h.SMSA_SENDER_BIC AS SENDER, ")
                .append("h.SMSA_RECEIVER_BIC AS RECEIVER, ")
                .append("h.SMSA_TXN_REF AS REFERENCE, ")
                .append("na.SMSA_NOSTRO_ACCOUNT AS ACCOUNT_NO, ")
                .append("COALESCE( ")
                .append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62M_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR), ")
                .append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62F_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR)) AS CURRENCY, ")
                .append("TO_CHAR(h.SMSA_FILE_DATE, 'DD-MON-YYYY') AS SEND_REC_DATE, ")
                .append("h.SMSA_FILE_TIME AS SEND_REC_TIME, ")
                .append("h.SMSA_FILE_TYPE AS FILE_TYPE, ")
                .append("h.SMSA_TXN_AMOUNT AS AMOUNT, ")
                .append("JSON_VALUE(h.SMSA_HDR_OBJ, '$.Receiver_obj.BANK_NAME') AS BANK_NAME ")
                .append("FROM SMSA_NOSTRO_ACCOUNT_MASTER na ")
                .append("INNER JOIN SMSA_PRT_MESSAGE_HDR h ON h.SMSA_MESSAGE_ID = na.SMSA_MESSAGE_ID ")
                .append("INNER JOIN SMSA_MSG_TXT t ON h.SMSA_MESSAGE_ID = t.SMSA_MESSAGE_ID ")
                .append("LEFT JOIN SMSA_GEO_MST g ON g.GEOG_CODE = h.SMSA_GEO_ID ")
                .append("WHERE na.SMSA_NOSTRO_ACCOUNT_CNFM = '0' ");

        // --- Dynamic Filters ---
        logger.info("🧱 Applying dynamic filters...");
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            queryBuilder.append(" AND h.SMSA_SENDER_BIC = :sender");
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            queryBuilder.append(" AND h.SMSA_RECEIVER_BIC = :receiver");
        }
        if (filter.getTransactionReference() != null && !filter.getTransactionReference().isEmpty()) {
            queryBuilder.append(" AND h.SMSA_TXN_REF = :txnRef");
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            queryBuilder.append(" AND na.SMSA_NOSTRO_ACCOUNT = :accountNo");
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            queryBuilder.append(" AND COALESCE(")
                    .append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62M_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR), ")
                    .append("JSON_VALUE(t.SMSA_MSG_TXTJ, '$.\"62F_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR)) = :currency");
        }
        if (filter.getMessageType() != null && !filter.getMessageType().isEmpty()) {
            queryBuilder.append(" AND h.SMSA_MT_CODE IN :msgTypeList");
        }
        if (filter.getFromDate() != null) {
            queryBuilder.append(" AND h.SMSA_FILE_DATE >= :fromDate");
        }
        if (filter.getToDate() != null) {
            queryBuilder.append(" AND h.SMSA_FILE_DATE <= :toDate");
        }
        if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
            queryBuilder.append(" AND h.SMSA_GEO_ID IN :geoList");
        }
        if (filter.getText() != null && !filter.getText().isEmpty()) {
            queryBuilder.append(" AND LOWER(h.SMSA_MSG_TEXT) LIKE LOWER(CONCAT('%', :text, '%'))");
        }

        queryBuilder.append(" ORDER BY h.SMSA_FILE_DATE DESC, h.SMSA_FILE_TIME DESC");

        int batchSize = 1000;
        int offset = 0;
        int totalFetched = 0;

        logger.info("Starting batched fetch | Batch size: {}", batchSize);

        while (true) {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindParameters(query, filter);
            query.setFirstResult(offset);
            query.setMaxResults(batchSize);

            List<Object[]> results = query.getResultList();
            if (results.isEmpty()) {
                logger.info("🟢 All batches fetched successfully | Total Records: {}", totalFetched);
                break;
            }

            for (Object[] row : results) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                dataMap.put("M_SEND/Rec_Date", value(row, 0));
                dataMap.put("I/O", value(row, 1));
                dataMap.put("Message Type", value(row, 2));
                dataMap.put("Sender", value(row, 3));
                dataMap.put("Receiver", value(row, 4));
                dataMap.put("Reference", value(row, 5));
                dataMap.put("AccountNo", value(row, 6));
                dataMap.put("Currency", value(row, 7));
                dataMap.put("Send/Rec Date", value(row, 8));
                dataMap.put("Send/Rec Time", value(row, 9));
                dataMap.put("FileType", value(row, 10));
                dataMap.put("Transacton Code", "");
                dataMap.put("Amount", value(row, 11));
                dataMap.put("BankName", value(row, 12));
                dataMap.put("Team", "");
                dataMap.put("Remark", "");
                dataMapList.add(dataMap);
            }

            totalFetched += results.size();
            offset += batchSize;
            logger.info("Fetched batch of {} records | Total so far: {}", results.size(), totalFetched);

            if (dataMapList.size() >= 100_000) {
                logger.warn("⚠️ Record limit (100k) reached — stopping further fetch.");
                break;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Completed CSV data fetch | Total Records: {} | Time Taken: {} ms", totalFetched, duration);
        return dataMapList;
    }

    /**
     * Paginated version for UI table
     * @param filter
     * @param pageable
     * @return 
     */
    public Page<Map<String, String>> getDailyValidationFilterData(DailyValidationFilter filter, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting paginated Daily Validation fetch | Filter: {} | Page: {} | Size: {}", filter, pageable.getPageNumber(), pageable.getPageSize());

        List<Map<String, String>> dataMapList = new ArrayList<>();

        StringBuilder baseQuery = new StringBuilder();
        baseQuery.append(" FROM smsa_nostro_account_master na ")
                .append(" INNER JOIN smsa_prt_message_hdr h ON h.smsa_message_id = na.smsa_message_id ")
                .append(" INNER JOIN SMSA_MSG_TXT t ON h.smsa_message_id = t.smsa_message_id ")
                .append(" LEFT JOIN smsa_geo_mst g ON g.geog_code = h.smsa_geo_id ")
                .append(" WHERE na.smsa_nostro_account_cnfm = '0' ");

        // Apply filters
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            baseQuery.append(" AND h.SMSA_SENDER_BIC = :sender");
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            baseQuery.append(" AND h.SMSA_RECEIVER_BIC = :receiver");
        }
        if (filter.getTransactionReference() != null && !filter.getTransactionReference().isEmpty()) {
            baseQuery.append(" AND h.SMSA_TXN_REF = :txnRef");
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            baseQuery.append(" AND na.SMSA_NOSTRO_ACCOUNT = :accountNo");
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            baseQuery.append(" AND COALESCE(")
                    .append("JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR), ")
                    .append("JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR)) = :currency");
        }
        if (filter.getMessageType() != null && !filter.getMessageType().isEmpty()) {
            baseQuery.append(" AND h.SMSA_MT_CODE IN :msgTypeList");
        }
        if (filter.getFromDate() != null) {
            baseQuery.append(" AND h.SMSA_FILE_DATE >= :fromDate");
        }
        if (filter.getToDate() != null) {
            baseQuery.append(" AND h.SMSA_FILE_DATE <= :toDate");
        }
        if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
            baseQuery.append(" AND h.SMSA_GEO_ID IN :geoList");
        }
        if (filter.getText() != null && !filter.getText().isEmpty()) {
            baseQuery.append(" AND LOWER(h.smsa_msg_text) LIKE LOWER(CONCAT('%', :text, '%'))");
        }

        // Count query
        String countQueryStr = "SELECT COUNT(*) " + baseQuery;
        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        bindParameters(countQuery, filter);
        Long totalElements = ((Number) countQuery.getSingleResult()).longValue();

        logger.info("Total Records Found: {}", totalElements);

        // Select query
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT ")
                .append("h.smsa_msg_io, ")
                .append("na.smsa_nostro_account, ")
                .append("h.smsa_sender_bic, ")
                .append("h.smsa_receiver_bic, ")
                .append("COALESCE(")
                .append("JSON_VALUE(t.smsa_msg_txtj, '$.\"62M_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR), ")
                .append("JSON_VALUE(t.smsa_msg_txtj, '$.\"62F_obj\".Currency' RETURNING VARCHAR2 NULL ON ERROR)) AS Currency, ")
                .append("h.smsa_mt_code, ")
                .append("g.geog_name ")
                .append(baseQuery)
                .append(" ORDER BY h.SMSA_FILE_DATE DESC");

        Query query = entityManager.createNativeQuery(selectQuery.toString());
        bindParameters(query, filter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Object[]> results = query.getResultList();

        for (Object[] row : results) {
            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("IO", value(row, 0));
            dataMap.put("AccountNo", value(row, 1));
            dataMap.put("SenderBic", value(row, 2));
            dataMap.put("ReceiverBic", value(row, 3));
            dataMap.put("Currency", value(row, 4));
            dataMap.put("MessageType", value(row, 5));
            dataMap.put("Location", value(row, 6));
            dataMapList.add(dataMap);
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Paginated fetch complete | Records: {} | Total: {} | Time Taken: {} ms",
                results.size(), totalElements, duration);

        return new PageImpl<>(dataMapList, pageable, totalElements);
    }

    private String value(Object[] row, int index) {
        return row[index] != null ? row[index].toString() : "";
    }

    private void bindParameters(Query query, DailyValidationFilter filter) {
        logger.info("Binding query parameters...");
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            query.setParameter("sender", filter.getSender());
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            query.setParameter("receiver", filter.getReceiver());
        }
        if (filter.getTransactionReference() != null && !filter.getTransactionReference().isEmpty()) {
            query.setParameter("txnRef", filter.getTransactionReference());
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            query.setParameter("accountNo", filter.getAccountNo());
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            query.setParameter("currency", filter.getCurrency());
        }
        if (filter.getMessageType() != null && !filter.getMessageType().isEmpty()) {
            query.setParameter("msgTypeList", filter.getMessageType());
        }
        if (filter.getFromDate() != null) {
            query.setParameter("fromDate", filter.getFromDate());
        }
        if (filter.getToDate() != null) {
            query.setParameter("toDate", filter.getToDate());
        }
        if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
            query.setParameter("geoList", filter.getGeography());
        }
        if (filter.getText() != null && !filter.getText().isEmpty()) {
            query.setParameter("text", filter.getText());
        }
    }
}
