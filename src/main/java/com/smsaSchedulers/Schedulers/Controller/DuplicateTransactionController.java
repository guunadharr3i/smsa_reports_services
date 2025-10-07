package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionProjection;
import com.smsaSchedulers.Schedulers.ReportServices.DuplicateTransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detDuplicateTransactionReport")
public class DuplicateTransactionController {

    private static final Logger logger = LogManager.getLogger(DuplicateTransactionController.class);

    @Autowired
    private DuplicateTransactionService service;

    @PostMapping
    public ResponseEntity<?> getDuplicates(
            @RequestBody DuplicateTransactionFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Received request for Duplicate Transaction Report. Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DuplicateTransactionProjection> result = service.getDuplicates(filter, pageable);

            logger.info("Successfully fetched Duplicate Transaction Report. Records returned: {}", result.getTotalElements());
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input provided for Duplicate Transaction Report: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input data. Please verify your request parameters.");

        } catch (Exception e) {
            logger.error("Error occurred while fetching Duplicate Transaction Report: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request. Please try again later.");
        }
    }
}
