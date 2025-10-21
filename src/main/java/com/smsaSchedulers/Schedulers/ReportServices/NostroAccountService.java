package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
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
public class NostroAccountService {

    private static final Logger logger = LogManager.getLogger(NostroAccountService.class);

    @PersistenceContext
    private EntityManager entityManager;

    // -------------------- PAGINATED FETCH --------------------
    public Page<Map<String, String>> filterNostroAccountData(NostroAccountFilterDto filter, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting paginated Nostro Account filter with filters: {}", filter);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT ");
        queryBuilder.append("h.smsa_mt_code AS MessageType, ");
        queryBuilder.append("h.smsa_sender_bic AS Sender, ");
        queryBuilder.append("h.smsa_receiver_bic AS Receiver, ");
        queryBuilder.append("t.smsa_trxn_acc_id AS AccountId, ");
        queryBuilder.append("h.smsa_file_date AS StatementReceivedDate, ");
        queryBuilder.append("t.smsa_msg_open_ccy AS Currency, ");
        queryBuilder.append("t.smsa_msg_opening_60f AS OpeningBalance, ");
        queryBuilder.append("t.smsa_msg_valdate62f AS ValueDate, ");
        queryBuilder.append("t.smsa_msg_open_cred AS OpeningCreditFlag, ");
        queryBuilder.append("t.smsa_closing_62f AS ClosingBalance ");
        queryBuilder.append("FROM smsa_prt_message_hdr h ");
        queryBuilder.append("INNER JOIN smsa_msg_txt t ON h.smsa_message_id = t.smsa_message_id ");
        queryBuilder.append("WHERE h.smsa_msg_io = 'O' ");

        // --- Apply dynamic filters ---
        if (filter.getSendRecFromDate() != null) {
            queryBuilder.append(" AND h.smsa_file_date >= :fromDate ");
        }
        if (filter.getSendRecToDate() != null) {
            queryBuilder.append(" AND h.smsa_file_date <= :toDate ");
        }
        if (filter.getMessageTypes() != null && !filter.getMessageTypes().isEmpty()) {
            queryBuilder.append(" AND h.smsa_mt_code IN :mtCodes ");
        }
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            queryBuilder.append(" AND h.smsa_sender_bic IN :senderBics ");
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            queryBuilder.append(" AND h.smsa_receiver_bic IN :receiverBics ");
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            queryBuilder.append(" AND t.smsa_trxn_acc_id = :accountId ");
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            queryBuilder.append(" AND t.smsa_msg_open_ccy = :currency ");
        }
        if (filter.getValueDateFrom() != null) {
            queryBuilder.append(" AND t.smsa_msg_valdate62f >= :valueDateFrom ");
        }
        if (filter.getValueDateTo() != null) {
            queryBuilder.append(" AND t.smsa_msg_valdate62f <= :valueDateTo ");
        }

        queryBuilder.append(" ORDER BY h.smsa_file_date DESC ");

        logger.debug("Generated SQL Query (Paginated): {}", queryBuilder);

        // --- Count query ---
        String countQueryStr = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ")";
        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        bindNostroAccountParams(countQuery, filter);

        Long totalElements = ((Number) countQuery.getSingleResult()).longValue();
        logger.info("Total elements found: {}", totalElements);

        // --- Main query ---
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        bindNostroAccountParams(query, filter);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            logger.debug("Pagination applied — Offset: {}, Page Size: {}", pageable.getOffset(), pageable.getPageSize());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        logger.info("Fetched {} records for current page.", results.size());

        for (Object[] row : results) {
            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("MessageType", getValue(row, 0));
            dataMap.put("Sender", getValue(row, 1));
            dataMap.put("Receiver", getValue(row, 2));
            dataMap.put("AccountIdentification", getValue(row, 3));
            dataMap.put("StatementReceivedDate", getValue(row, 4));
            dataMap.put("Currency", getValue(row, 5));
            dataMap.put("FirstOpeningBalance", getValue(row, 6));
            dataMap.put("ValueDate", getValue(row, 7));
            dataMap.put("OpeningCreditFlag", getValue(row, 8));
            dataMap.put("ClosingBalance", getValue(row, 9));
            dataMapList.add(dataMap);
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Paginated Nostro Account fetch completed in {} ms.", duration);

        return new PageImpl<>(dataMapList, pageable, totalElements);
    }

    // -------------------- NON-PAGINATED (BATCH) FETCH --------------------
    public List<Map<String, String>> filterNostroAccountData(NostroAccountFilterDto filter) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting batch Nostro Account filter with filters: {}", filter);

        List<Map<String, String>> dataMapList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT ");
        queryBuilder.append("h.smsa_msg_io, ");
        queryBuilder.append("h.smsa_mt_code AS MessageType, ");
        queryBuilder.append("h.smsa_sender_bic AS Sender, ");
        queryBuilder.append("h.smsa_receiver_bic AS Receiver, ");
        queryBuilder.append("t.smsa_trxn_acc_id AS AccountId, ");
        queryBuilder.append("h.smsa_file_date AS StatementReceivedDate, ");
        queryBuilder.append("t.smsa_msg_valdate60f, ");
        queryBuilder.append("t.smsa_msg_open_cred, ");
        queryBuilder.append("t.smsa_msg_open_ccy AS Currency, ");
        queryBuilder.append("t.smsa_msg_opening_60f AS OpeningBalance, ");
        queryBuilder.append("t.smsa_msg_stmt_number, ");
        queryBuilder.append("t.smsa_msg_valdate62f AS ValueDate, ");
        queryBuilder.append("t.smsa_msg_open_cred AS OpeningCreditFlag, ");
        queryBuilder.append("t.smsa_closing_62f AS ClosingBalance, ");
        queryBuilder.append("t.smsa_available_64, ");
        queryBuilder.append("t.smsa_msg_avail_cred ");
        queryBuilder.append("FROM smsa_prt_message_hdr h ");
        queryBuilder.append("INNER JOIN smsa_msg_txt t ON h.smsa_message_id = t.smsa_message_id ");
        queryBuilder.append("WHERE h.smsa_msg_io = 'O' ");

        // --- Filters ---
        if (filter.getSendRecFromDate() != null) {
            queryBuilder.append(" AND h.smsa_file_date >= :fromDate ");
        }
        if (filter.getSendRecToDate() != null) {
            queryBuilder.append(" AND h.smsa_file_date <= :toDate ");
        }
        if (filter.getMessageTypes() != null && !filter.getMessageTypes().isEmpty()) {
            queryBuilder.append(" AND h.smsa_mt_code IN :mtCodes ");
        }
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            queryBuilder.append(" AND h.smsa_sender_bic IN :senderBics ");
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            queryBuilder.append(" AND h.smsa_receiver_bic IN :receiverBics ");
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            queryBuilder.append(" AND t.smsa_trxn_acc_id = :accountId ");
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            queryBuilder.append(" AND t.smsa_msg_open_ccy = :currency ");
        }
        if (filter.getValueDateFrom() != null) {
            queryBuilder.append(" AND t.smsa_msg_valdate62f >= :valueDateFrom ");
        }
        if (filter.getValueDateTo() != null) {
            queryBuilder.append(" AND t.smsa_msg_valdate62f <= :valueDateTo ");
        }

        queryBuilder.append(" ORDER BY h.smsa_file_date DESC ");
        logger.debug("Generated SQL Query (Batch): {}", queryBuilder);

        int batchSize = 1000;
        int offset = 0;

        while (true) {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            bindNostroAccountParams(query, filter);
            query.setFirstResult(offset);
            query.setMaxResults(batchSize);

            logger.debug("Fetching batch from offset {} (limit {})", offset, batchSize);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            if (results.isEmpty()) {
                logger.info("No more records found. Ending batch fetch.");
                break;
            }

            for (Object[] row : results) {
                Map<String, String> dataMap = new LinkedHashMap<>();
                dataMap.put("Identifier", getValue(row, 0));
                dataMap.put("Message Type", getValue(row, 1));
                dataMap.put("Sender", getValue(row, 2));
                dataMap.put("Receiver", getValue(row, 3));
                dataMap.put("Account Identification", getValue(row, 4));
                dataMap.put("Statement Received Date", getValue(row, 5));
                dataMap.put("ValueDate(60F Field)", getValue(row, 6));
                dataMap.put("Debit/Credit Identifier", getValue(row, 7));
                dataMap.put("Currency", getValue(row, 8));
                dataMap.put("First Opening Balance (60F Field)", getValue(row, 9));
                dataMap.put("Transaction Code", getValue(row, 10));
                dataMap.put("Last Transaction Date", "");
                dataMap.put("ValueDate (62F Field)", getValue(row, 11));
                dataMap.put("Debit/Credit Identifier(62F Field)", getValue(row, 12));
                dataMap.put("Closing Balance (62F Field)", getValue(row, 13));
                dataMap.put("Available Balance (64F Field)", getValue(row, 14));
                dataMap.put("Debit/Credit Identifier(64F Field)", getValue(row, 15));

                dataMapList.add(dataMap);
            }

            offset += batchSize;
            logger.info("Processed batch till offset: {}. Total records so far: {}", offset, dataMapList.size());
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Batch Nostro Account fetch completed. Total records: {} | Time taken: {} ms", dataMapList.size(), duration);

        return dataMapList;
    }

    // -------------------- Helper Methods --------------------
    private void bindNostroAccountParams(Query query, NostroAccountFilterDto filter) {
        logger.debug("Binding parameters for NostroAccount query...");
        if (filter.getSendRecFromDate() != null) {
            query.setParameter("fromDate", filter.getSendRecFromDate());
        }
        if (filter.getSendRecToDate() != null) {
            query.setParameter("toDate", filter.getSendRecToDate());
        }
        if (filter.getMessageTypes() != null && !filter.getMessageTypes().isEmpty()) {
            query.setParameter("mtCodes", filter.getMessageTypes());
        }
        if (filter.getSender() != null && !filter.getSender().isEmpty()) {
            query.setParameter("senderBics", filter.getSender());
        }
        if (filter.getReceiver() != null && !filter.getReceiver().isEmpty()) {
            query.setParameter("receiverBics", filter.getReceiver());
        }
        if (filter.getAccountNo() != null && !filter.getAccountNo().isEmpty()) {
            query.setParameter("accountId", filter.getAccountNo());
        }
        if (filter.getCurrency() != null && !filter.getCurrency().isEmpty()) {
            query.setParameter("currency", filter.getCurrency());
        }
        if (filter.getValueDateFrom() != null) {
            query.setParameter("valueDateFrom", filter.getValueDateFrom());
        }
        if (filter.getValueDateTo() != null) {
            query.setParameter("valueDateTo", filter.getValueDateTo());
        }
    }

    private String getValue(Object[] row, int index) {
        return (row[index] != null) ? row[index].toString() : "";
    }
}
