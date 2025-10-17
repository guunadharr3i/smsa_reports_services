package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.DailyValidationFilter;
import com.smsaSchedulers.Schedulers.Pojo.DuplicateTransactionFilter;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
import com.smsaSchedulers.Schedulers.Repo.SwiftGeoMasterRepository;
import com.smsaSchedulers.Schedulers.ReportServices.DailyValidationService;
import com.smsaSchedulers.Schedulers.ReportServices.DuplicateTransactionService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroAccountService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroClosingBalanceService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroNegativeBalanceService;
import com.smsaSchedulers.Schedulers.ReportServices.NostroTurnoverService;
import com.smsaSchedulers.Schedulers.Utils.ExcelExportUtilAspose;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportsDownloadController {

    private static final Logger log = LogManager.getLogger(ReportsDownloadController.class);

    @Autowired
    private DailyValidationService dailyValidationService;

    @Autowired
    DuplicateTransactionService duplicateTransactionService;

    @Autowired
    NostroClosingBalanceService nostroClosingBalanceService;

    @Autowired
    private NostroNegativeBalanceService negatveBalanceService;

    @Autowired
    private NostroAccountService accountService;

    @Autowired
    private NostroTurnoverService service;

    @Autowired
    public SwiftGeoMasterRepository swiftGeoMasterRepository;

    @PostMapping("/DailyValidationDownload")
    public ResponseEntity<?> downloadDailyValidationReport(
            @RequestBody DailyValidationFilter filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            // Step 1: Get data from DB/service
            if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeography());
                filter.getGeography().clear();
                filter.setGeography(geoCodes);
            }
            List<Map<String, String>> dataMapList = dailyValidationService.filterDownloadValidationFilterData(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Daily_Validation_Report", "CSV");
                    fileName = "Daily_Validation_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Daily_Validation_Report", "XLSB");
                    fileName = "Daily_Validation_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }

    @PostMapping("/DuplicateTransactionDownload")
    public ResponseEntity<?> downloadDuplicateTransactionReport(
            @RequestBody DuplicateTransactionFilter filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            // Step 1: Get data from DB/service
            if (filter.getGeographies() != null && !filter.getGeographies().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeographies());
                filter.getGeographies().clear();
                filter.setGeographies(geoCodes);
            }
            List<Map<String, String>> dataMapList = duplicateTransactionService.filterDownloadDuplicateTransactionFilterData(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Duplicate_Transaction_Report", "CSV");
                    fileName = "Duplicate_Transaction_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Duplicate_Transaction_Report", "XLSB");
                    fileName = "Duplicate_Transaction_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }

    @PostMapping("/NostroClosingDownload")
    public ResponseEntity<?> downloadNostroClosingReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            // Step 1: Get data from DB/service
            List<Map<String, String>> dataMapList = nostroClosingBalanceService.downloadNostroClosingBalanceData(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Closing_Report", "CSV");
                    fileName = "Nostro_Closing_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Closing_Report", "XLSB");
                    fileName = "Nostro_Closing_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }

    @PostMapping("/NostroNegativeDownload")
    public ResponseEntity<?> downloadNostroNegativeReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            // Step 1: Get data from DB/service
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            List<Map<String, String>> dataMapList = negatveBalanceService.downloadNostroClosingBalanceData(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Negative_Report", "CSV");
                    fileName = "Nostro_Negative_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Negative_Report", "XLSB");
                    fileName = "Nostro_Negative_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }

    @PostMapping("/NostroReportDownload")
    public ResponseEntity<?> downloadNostroReportReport(
            @RequestBody NostroAccountFilterDto filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            // Step 1: Get data from DB/service
            if (filter.getGeography() != null && !filter.getGeography().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeography());
                filter.getGeography().clear();
                filter.setGeography(geoCodes);
            }
            List<Map<String, String>> dataMapList = accountService.filterNostroAccountData(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Report", "CSV");
                    fileName = "Nostro_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_Report", "XLSB");
                    fileName = "Nostro_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }

    @PostMapping("/NostroTurnOverDownload")
    public ResponseEntity<?> downloadNostroTurnOverReport(
            @RequestBody NostroClosingBalanceFilter filter,
            @RequestParam String downloadType) {

        log.info("Received report download request | Type: {} | Filter: {}", downloadType, filter);

        try {
            // Step 1: Get data from DB/service
            if (filter.getGeoIds() != null && !filter.getGeoIds().isEmpty()) {
                List<String> geoCodes = swiftGeoMasterRepository.findGeoCodesByGeoNames(filter.getGeoIds());
                filter.getGeoIds().clear();
                filter.setGeoIds(geoCodes);
            }
            List<Map<String, String>> dataMapList = service.downloadNostroTurnOverReport(filter);
            log.info("Fetched {} records for report generation", dataMapList != null ? dataMapList.size() : 0);

            // Step 2: Handle empty data
            if (dataMapList == null || dataMapList.isEmpty()) {
                log.warn("No data found for given filters.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No data found for the given filters.");
            }

            byte[] fileBytes;
            String fileName;
            MediaType contentType;

            // Step 3: Generate file based on download type
            switch (downloadType.toUpperCase()) {
                case "CSV":
                    log.info("Generating CSV report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_TurnOver_Report", "CSV");
                    fileName = "Nostro_TurnOver_Report.csv";
                    contentType = MediaType.TEXT_PLAIN;
                    break;

                case "XLSX":
                    log.info("Generating XLSB report...");
                    fileBytes = ExcelExportUtilAspose.exportData(dataMapList, "Nostro_TurnOver_Report", "XLSB");
                    fileName = "Nostro_TurnOver_Report.xlsb";
                    contentType = MediaType.APPLICATION_OCTET_STREAM;
                    break;

                default:
                    log.error("Invalid download type provided: {}", downloadType);
                    return ResponseEntity.badRequest()
                            .body("Invalid download type. Please use 'CSV' or 'XLSB'.");
            }

            // Step 4: Return file as attachment
            log.info("Report generated successfully. Sending response...");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            log.error("Error occurred while generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("An error occurred while generating the report. Please try again later.");
        }
    }
}
