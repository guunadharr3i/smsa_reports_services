package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationProjection;
import com.smsaSchedulers.Schedulers.Repo.DailyValidationRepository;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DailyValidationService {

    private static final Logger logger = LogManager.getLogger(DailyValidationService.class);

    @Autowired
    private DailyValidationRepository repository;

    public Page<DailyValidationProjection> getDailyValidation(
            DailyValidationFilter filter, Pageable pageable) {

        logger.info("Starting Daily Validation fetch with filter: {}", filter);

        try {
            // ✅ Normalize input parameters
            List<Integer> mtCodes = (filter.getMessageType() == null || filter.getMessageType().isEmpty())
                    ? Arrays.asList(535, 940, 950, 608)
                    : filter.getMessageType();

            List<String> geoGraphy = (filter.getGeography() == null || filter.getGeography().isEmpty())
                    ? null
                    : filter.getGeography();

            String sender = normalize(filter.getSender());
            String receiver = normalize(filter.getReceiver());
            String transactionReference = normalize(filter.getTransactionReference());
            String accountNo = normalize(filter.getAccountNo());
            String currency = normalize(filter.getCurrency());
            String text = normalize(filter.getText());

            logger.debug("Normalized filter values - MT Codes: {}, Geography: {}, Sender: {}, Receiver: {}, TxnRef: {}, AccountNo: {}, Currency: {}, Text: {}, FromDate: {}, ToDate: {}",
                    mtCodes, geoGraphy, sender, receiver, transactionReference, accountNo, currency, text,
                    filter.getFromDate(), filter.getToDate());

            // ✅ Execute DB call
            Page<DailyValidationProjection> result = repository.findDailyValidation(
                    mtCodes,
                    geoGraphy,
                    sender,
                    receiver,
                    transactionReference,
                    accountNo,
                    currency,
                    filter.getFromDate(),
                    filter.getToDate(),
                    text,
                    pageable
            );

            logger.info("Daily Validation fetch successful. Total records found: {}", result.getTotalElements());
            return result;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input provided for Daily Validation fetch: {}", e.getMessage(), e);
            throw e; // rethrow so controller can return BAD_REQUEST
        } catch (Exception e) {
            logger.error("Error occurred while fetching Daily Validation data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch Daily Validation data. Please try again later.", e);
        }
    }

    // Helper method to safely trim & nullify empty strings
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
