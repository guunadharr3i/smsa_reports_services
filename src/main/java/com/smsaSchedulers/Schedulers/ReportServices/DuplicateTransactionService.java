package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionProjection;
import com.smsaSchedulers.Schedulers.Repo.DuplicateTransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DuplicateTransactionService {

    private static final Logger logger = LogManager.getLogger(DuplicateTransactionService.class);

    @Autowired
    private DuplicateTransactionRepository repository;

    public Page<DuplicateTransactionProjection> getDuplicates(DuplicateTransactionFilter filter, Pageable pageable) {

        logger.info("Starting Duplicate Transaction fetch with filter: {}", filter);

        try {
            // ✅ Normalize inputs to avoid invalid DB calls
            if (filter == null) {
                logger.warn("Received null filter — initializing default filter object.");
                filter = new DuplicateTransactionFilter();
            }

            filter.setGeographies(normalizeList(filter.getGeographies()));
            filter.setReferenceNo(normalize(filter.getReferenceNo()));
            filter.setRelatedReferenceNo(normalize(filter.getRelatedReferenceNo()));
            filter.setCurrency(normalize(filter.getCurrency()));

            logger.debug("Normalized filter values - Geographies: {}, ReferenceNo: {}, RelatedRefNo: {}, Currency: {}, FromDate: {}, ToDate: {}",
                    filter.getGeographies(), filter.getReferenceNo(), filter.getRelatedReferenceNo(),
                    filter.getCurrency(), filter.getFromDate(), filter.getToDate());

            // ✅ Call repository
            Page<DuplicateTransactionProjection> result = repository.findDuplicateMessages(
                    filter.getGeographies(),
                    filter.getReferenceNo(),
                    filter.getRelatedReferenceNo(),
                    filter.getCurrency(),
                    filter.getFromDate(),
                    filter.getToDate(),
                    pageable
            );

            logger.info("Duplicate Transaction fetch successful. Total records found: {}", result.getTotalElements());
            return result;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input provided for Duplicate Transaction fetch: {}", e.getMessage(), e);
            throw e; // Will be handled by controller as BAD_REQUEST
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching Duplicate Transaction data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch Duplicate Transaction data. Please try again later.", e);
        }
    }

    // Helper method to normalize strings (trim + nullify empty)
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    // Helper method to normalize lists (set null if empty)
    private <T> java.util.List<T> normalizeList(java.util.List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }
}
