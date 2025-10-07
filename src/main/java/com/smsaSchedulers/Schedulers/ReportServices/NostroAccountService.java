package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountProjection;
import com.smsaSchedulers.Schedulers.Repo.NostroAccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NostroAccountService {

    private static final Logger logger = LogManager.getLogger(NostroAccountService.class);

    private final NostroAccountRepository repository;

    @Autowired
    public NostroAccountService(NostroAccountRepository repository) {
        this.repository = repository;
    }

    public Page<NostroAccountProjection> getAccounts(NostroAccountFilterDto filter, Pageable pageable) {
        long startTime = System.currentTimeMillis();
        logger.info("Fetching Nostro Accounts | Page: {}, Size: {}, Filter: {}",
                pageable.getPageNumber(), pageable.getPageSize(), filter);

        // Normalize strings: convert empty -> null, add wildcards for LIKE
        String sender = normalizeLike(filter.getSender());
        String receiver = normalizeLike(filter.getReceiver());
        String accountNo = normalize(filter.getAccountNo());
        String currency = normalize(filter.getCurrency());
        String transactionReference = normalizeLike(filter.getTransactionReference());
        String relatedReference = normalizeLike(filter.getRelatedReference());
        String text = normalizeLike(filter.getText());

        // Normalize lists: empty -> defaults/null
        List<Integer> messageTypes = (filter.getMessageTypes() == null || filter.getMessageTypes().isEmpty())
                ? Arrays.asList(940, 950) // default
                : filter.getMessageTypes();

        List<String> geoIds = (filter.getGeography() == null || filter.getGeography().isEmpty())
                ? null
                : filter.getGeography();

        logger.info("Normalized Filters | Sender: {}, Receiver: {}, AccountNo: {}, Currency: {}, TxnRef: {}, RelRef: {}, Text: {}, MsgTypes: {}, Geo: {}",
                sender, receiver, accountNo, currency, transactionReference, relatedReference, text, messageTypes, geoIds);

        Page<NostroAccountProjection> result = repository.findAccounts(
                filter.getSendRecFromDate(),
                filter.getSendRecToDate(),
                filter.getValueDateFrom(),
                filter.getValueDateTo(),
                sender,
                receiver,
                accountNo,
                currency,
                transactionReference,
                relatedReference,
                text,
                messageTypes,
                geoIds,
                pageable
        );

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                result.getNumberOfElements(),
                result.getNumber() + 1,
                result.getTotalPages(),
                duration);

        return result;
    }

    private String normalize(String input) {
        return (input == null || input.trim().isEmpty()) ? null : input.trim();
    }

    private String normalizeLike(String input) {
        return (input == null || input.trim().isEmpty()) ? null : "%" + input.trim() + "%";
    }
}
