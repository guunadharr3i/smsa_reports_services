package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.NostroTurnoverProjection;
import com.smsaSchedulers.Schedulers.ReportServices.NostroTurnoverService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nostro")
public class NostroTurnoverController {

    private final NostroTurnoverService service;

    private static final Logger logger = LogManager.getLogger(NostroTurnoverController.class);

    public NostroTurnoverController(NostroTurnoverService service) {
        this.service = service;
    }

    @PostMapping("/turnover")
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
