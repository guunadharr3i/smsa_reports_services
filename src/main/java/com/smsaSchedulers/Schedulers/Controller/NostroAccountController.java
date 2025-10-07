package com.smsaSchedulers.Schedulers.Controller;

import com.smsaSchedulers.Schedulers.Pojo.NostroAccountFilterDto;
import com.smsaSchedulers.Schedulers.Pojo.NostroAccountProjection;
import com.smsaSchedulers.Schedulers.ReportServices.NostroAccountService;
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
@RequestMapping("/api/nostro/accounts")
public class NostroAccountController {

    private final NostroAccountService service;

    private static final Logger logger = LogManager.getLogger(NostroAccountController.class);

    @Autowired
    public NostroAccountController(NostroAccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> getAccounts(
            @RequestBody NostroAccountFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        long startTime = System.currentTimeMillis();
        logger.info("Request received for Nostro Accounts | Page: {}, Size: {}, Filter: {}", page, size, filter);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NostroAccountProjection> result = service.getAccounts(filter, pageable);

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
}
