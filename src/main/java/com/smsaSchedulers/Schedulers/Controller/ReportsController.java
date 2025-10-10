/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationProjection;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionProjection;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountProjection;
import com.smsaSchedulers.Schedulers.Pojo.NostroClosingBalanceProjection;
import com.smsaSchedulers.Schedulers.Pojo.NostroNegativeBalanceProjection;
import com.smsaSchedulers.Schedulers.Pojo.NostroTurnoverProjection;
import com.smsaSchedulers.Schedulers.ReportServices.DailyValidationService;
import com.smsaSchedulers.Schedulers.ReportServices.DuplicateTransactionService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroAccountService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroClosingBalanceService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroNegativeBalanceService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroTurnoverService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author abcom
 */
@RestController
@RequestMapping("/api")
public class ReportsController {

    private static final Logger logger = LogManager.getLogger(ReportsController.class);
    @Autowired
    private DailyValidationService dailyValidationService;
    @Autowired
    private DuplicateTransactionService duplicateTransactionService;

    @Autowired
    private NostroClosingBalanceService closingBalanceService;

    @Autowired
    private NostroNegativeBalanceService negatveBalanceService;

    @Autowired
    private NostroTurnoverService service;

    @Autowired
    private NostroAccountService accountService;

    @PostMapping("/nostro-validation")
    public ResponseEntity<?> getDailyValidation(
            @RequestBody DailyValidationFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Received request for Daily Validation Report. Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DailyValidationProjection> result = dailyValidationService.getDailyValidation(filter, pageable);

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

    @PostMapping("/detDuplicateTransactionReport")
    public ResponseEntity<?> getDuplicates(
            @RequestBody DuplicateTransactionFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Received request for Duplicate Transaction Report. Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<DuplicateTransactionProjection> result = duplicateTransactionService.getDuplicates(filter, pageable);

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

    @PostMapping("/nostro/accounts")
    public ResponseEntity<?> getAccounts(
            @RequestBody NostroAccountFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("Request received for Nostro Accounts | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NostroAccountProjection> result = accountService.getAccounts(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                    result.getNumberOfElements(),
                    result.getNumber() + 1,
                    result.getTotalPages(),
                    duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Bad request while fetching Nostro Accounts | Execution Time: {} ms | Error: {}", duration, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Unexpected error while fetching Nostro Accounts | Execution Time: {} ms", duration, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }

    @PostMapping("/nostro/closing-balance")
    public ResponseEntity<?> getClosingBalance(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long startTime = System.currentTimeMillis();
        logger.info("Request received for Nostro Closing Balances | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            PageRequest pageable = PageRequest.of(page, size);
            Page<NostroClosingBalanceProjection> result = closingBalanceService.getClosingBalances(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                    result.getNumberOfElements(),
                    result.getNumber() + 1,
                    result.getTotalPages(),
                    duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Bad request while fetching Nostro Closing Balances | Execution Time: {} ms | Error: {}", duration, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Unexpected error while fetching Nostro Closing Balances | Execution Time: {} ms", duration, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }

    @PostMapping("/nostro/negative-balance")
    public ResponseEntity<?> getNegativeBalanceReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("Request received for Nostro Negative Balance Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NostroNegativeBalanceProjection> result = negatveBalanceService.getNegativeBalances(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                    result.getNumberOfElements(),
                    result.getNumber() + 1,
                    result.getTotalPages(),
                    duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Bad request while fetching Nostro Negative Balance Report | Execution Time: {} ms | Error: {}", duration, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Unexpected error while fetching Nostro Negative Balance Report | Execution Time: {} ms", duration, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }

    @PostMapping("/nostro/turnover")
    public ResponseEntity<?> getTurnoverReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("Request received for Nostro Turnover Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NostroTurnoverProjection> result = service.getTurnover(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                    result.getNumberOfElements(),
                    result.getNumber() + 1,
                    result.getTotalPages(),
                    duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Bad request while fetching Nostro Turnover Report | Execution Time: {} ms | Error: {}", duration, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Unexpected error while fetching Nostro Turnover Report | Execution Time: {} ms", duration, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }

}
