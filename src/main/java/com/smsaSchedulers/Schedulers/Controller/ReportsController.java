/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
import com.smsaSchedulers.Schedulers.Repo.SwiftGeoMasterRepository;
import com.smsaSchedulers.Schedulers.ReportServices.*;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Reports Controller Handles all report-related API requests with pagination
 * and filters. Each API includes detailed logging for debugging and performance
 * tracking.
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
    private NostroTurnoverService turnoverService;
    @Autowired
    private NostroAccountService accountService;

    @Autowired
    public SwiftGeoMasterRepository swiftGeoMasterRepository;

    /**
     * Daily Validation Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/nostro-validation")
    public ResponseEntity<?> getDailyValidation(
            @RequestBody DailyValidationFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Daily Validation Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeography());
                filter.getGeography().clear();
                filter.setGeography(geoCodes);
            }

            Page<Map<String, String>> dailyData = dailyValidationService.getDailyValidationFilterData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Daily Validation Report | Records: {}, Page: {}/{} | Time: {} ms",
                    dailyData.getNumberOfElements(), dailyData.getNumber() + 1, dailyData.getTotalPages(), duration);

            return ResponseEntity.ok(dailyData);

        } catch (IllegalArgumentException e) {
            logger.error("[BAD_REQUEST] Daily Validation Report | Reason: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input provided. Please check your request data.");
        } catch (Exception e) {
            logger.error("[ERROR] Daily Validation Report | Unexpected Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Daily Validation Report processing completed.");
        }
    }

    /**
     * Duplicate Transaction Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/detDuplicateTransactionReport")
    public ResponseEntity<?> getDuplicates(
            @RequestBody DuplicateTransactionFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Duplicate Transaction Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            if (filter.getGeographies() != null && !filter.getGeographies().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeographies());
                filter.getGeographies().clear();
                filter.setGeographies(geoCodes);
            }
            Page<Map<String, String>> result = duplicateTransactionService.filterDuplicateTransactionData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Duplicate Transaction Report | Records: {}, Page: {}/{} | Time: {} ms",
                    result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages(), duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.error("[BAD_REQUEST] Duplicate Transaction Report | Reason: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input data. Please verify your request parameters.");
        } catch (Exception e) {
            logger.error("[ERROR] Duplicate Transaction Report | Unexpected Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Duplicate Transaction Report processing completed.");
        }
    }

    /**
     * Nostro Accounts Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/nostro/accounts")
    public ResponseEntity<?> getAccounts(
            @RequestBody NostroAccountFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Nostro Accounts Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeography());
                filter.getGeography().clear();
                filter.setGeography(geoCodes);
            }
            Page<Map<String, String>> result = accountService.filterNostroAccountData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Nostro Accounts Report | Records: {}, Page: {}/{} | Time: {} ms",
                    result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages(), duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            logger.error("[BAD_REQUEST] Nostro Accounts Report | Reason: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("[ERROR] Nostro Accounts Report | Unexpected Error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Nostro Accounts Report processing completed.");
        }
    }

    /**
     * Nostro Closing Balance Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/nostro/closing-balance")
    public ResponseEntity<?> getClosingBalance(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Nostro Closing Balance Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            Page<Map<String, String>> result = closingBalanceService.filterNostroClosingBalanceData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Nostro Closing Balance Report | Records: {}, Page: {}/{} | Time: {} ms",
                    result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages(), duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            logger.error("[BAD_REQUEST] Nostro Closing Balance Report | Reason: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("[ERROR] Nostro Closing Balance Report | Unexpected Error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Nostro Closing Balance Report processing completed.");
        }
    }

    /**
     * Nostro Negative Balance Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/nostro/negative-balance")
    public ResponseEntity<?> getNegativeBalanceReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Nostro Negative Balance Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            Page<Map<String, String>> result = negatveBalanceService.filterNostroNegativeBalanceData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Nostro Negative Balance Report | Records: {}, Page: {}/{} | Time: {} ms",
                    result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages(), duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            logger.error("[BAD_REQUEST] Nostro Negative Balance Report | Reason: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("[ERROR] Nostro Negative Balance Report | Unexpected Error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Nostro Negative Balance Report processing completed.");
        }
    }

    /**
     * Nostro Turnover Report
     *
     * @param filter
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/nostro/turnover")
    public ResponseEntity<?> getTurnoverReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("[START] Nostro Turnover Report | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
              if (filter.getGeoIds()!= null && !filter.getGeoIds().isEmpty()) {
                List<String>  geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            Page<Map<String, String>> result = turnoverService.filterNostroReportData(filter, pageable);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[SUCCESS] Nostro Turnover Report | Records: {}, Page: {}/{} | Time: {} ms",
                    result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages(), duration);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            logger.error("[BAD_REQUEST] Nostro Turnover Report | Reason: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("[ERROR] Nostro Turnover Report | Unexpected Error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        } finally {
            logger.info("[END] Nostro Turnover Report processing completed.");
        }
    }
}
