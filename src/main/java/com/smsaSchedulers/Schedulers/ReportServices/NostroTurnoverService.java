package com.smsaSchedulers.Schedulers.ReportServices;

import com.smsaSchedulers.Schedulers.Entity.NostroClosingBalanceFilter;
import com.smsaSchedulers.Schedulers.Pojo.NostroTurnoverProjection;
import com.smsaSchedulers.Schedulers.Repo.NostroTurnoverRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class NostroTurnoverService {

    private static final Logger logger = LogManager.getLogger(NostroTurnoverService.class);

    private final NostroTurnoverRepository repository;

    public NostroTurnoverService(NostroTurnoverRepository repository) {
        this.repository = repository;
    }

    public Page<NostroTurnoverProjection> getTurnover(
            NostroClosingBalanceFilter filter, Pageable pageable) {

        long startTime = System.currentTimeMillis();
        logger.info("Fetching Nostro Turnover | Page: {}, Size: {}, Filter: {}",
                pageable.getPageNumber(), pageable.getPageSize(), filter);

        // ✅ Default MT codes [940,950] if none provided
        List<Integer> mtCodes = (filter.getMtCodes() == null || filter.getMtCodes().isEmpty())
                ? Arrays.asList(940, 950)
                : filter.getMtCodes();

        // ✅ Avoid Oracle IN () errors by converting empty list to null
        List<String> geoIds = (filter.getGeoIds() == null || filter.getGeoIds().isEmpty())
                ? null
                : filter.getGeoIds();

        LocalDate fromDate = filter.getFromDate();
        LocalDate toDate = filter.getToDate() != null ? filter.getToDate().plusDays(1) : null;

        logger.info("Normalized Filters | FromDate: {}, ToDate: {}, MT Codes: {}, GeoIds: {}, AccountNo: {}",
                fromDate, toDate, mtCodes, geoIds, emptyToNull(filter.getAccountNumber()));

        Page<NostroTurnoverProjection> result = repository.findTurnover(
                fromDate,
                toDate,
                mtCodes,
                geoIds,
                emptyToNull(filter.getAccountNumber()),
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

    private String normalize(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    // Small helper
    private String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }
}
