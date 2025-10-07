package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationProjection;
import com.smsaSchedulers.Schedulers.ReportServices.DailyValidationService;
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
@RequestMapping("/api/nostro-validation")
public class DailyValidationReportController {

    private static final Logger logger = LogManager.getLogger(DailyValidationReportController.class);

    @Autowired
    private DailyValidationService service;

    @PostMapping
    public ResponseEntity<?> getDailyValidation(
            @RequestBody DailyValidationFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Received request for Daily Validation Report. Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DailyValidationProjection> result = service.getDailyValidation(filter, pageable);

            logger.info("Successfully fetched Daily Validation data. Records returned: {}", result.getTotalElements());
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request parameters: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input provided. Please check your request data.");

        } catch (Exception e) {
            logger.error("Unexpected error while fetching Daily Validation Report: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }
}
