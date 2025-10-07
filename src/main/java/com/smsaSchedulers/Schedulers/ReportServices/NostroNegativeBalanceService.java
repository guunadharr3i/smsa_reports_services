package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.NostroNegativeBalanceProjection;
import com.smsaSchedulers.Schedulers.Repo.NostroNegativeBalanceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NostroNegativeBalanceService {

    private static final Logger logger = LogManager.getLogger(NostroNegativeBalanceService.class);

    private final NostroNegativeBalanceRepository repository;

    public NostroNegativeBalanceService(NostroNegativeBalanceRepository repository) {
        this.repository = repository;
    }

    public Page<NostroNegativeBalanceProjection> getNegativeBalances(
            NostroClosingBalanceFilter filter, Pageable pageable) {

        long startTime = System.currentTimeMillis();
        logger.info("Fetching Nostro Negative Balances | Page: {}, Size: {}, Filter: {}",
                pageable.getPageNumber(), pageable.getPageSize(), filter);

        // ✅ Default MT codes [940,950] if none provided
        List<Integer> mtCodes = (filter.getMtCodes() == null || filter.getMtCodes().isEmpty())
                ? Arrays.asList(940, 950)
                : new ArrayList<>(filter.getMtCodes());

        // ✅ Dates (toDate inclusive handling)
        LocalDate fromDate = filter.getFromDate();
        LocalDate toDate = (filter.getToDate() != null) ? filter.getToDate().plusDays(1) : null;

        // ✅ Geo IDs
        List<String> geoIds = (filter.getGeoIds() == null || filter.getGeoIds().isEmpty())
                ? null
                : filter.getGeoIds();

        logger.info("Normalized Filters | FromDate: {}, ToDate: {}, MT Codes: {}, GeoIds: {}, AccountNo: {}",
                fromDate, toDate, mtCodes, geoIds, filter.getAccountNumber());

        Page<NostroNegativeBalanceProjection> result
                = repository.findNegativeBalances(fromDate, toDate, mtCodes, geoIds, filter.getAccountNumber(), pageable);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Fetched {} records | Current Page: {} / Total Pages: {} | Execution Time: {} ms",
                result.getNumberOfElements(),
                result.getNumber() + 1,
                result.getTotalPages(),
                duration);

        return result;
    }
}
