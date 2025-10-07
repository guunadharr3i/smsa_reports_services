///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.smsaSchedulers.Schedulers.ReportSchedulers;
//
///**
// *
// * @author abcom
// */
//import com.smsaSchedulers.Schedulers.Pojo.NostroNegativeReportDto;
//import com.smsaSchedulers.Schedulers.ReportServices.NostroReportService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.sql.Date;
//import java.util.Calendar;
//import java.util.List;
//
//@Component
//public class NostroNegativeReportScheduler {
//
//    private final NostroReportService reportService;
//
//    public NostroNegativeReportScheduler(NostroReportService reportService) {
//        this.reportService = reportService;
//    }
//
//    // Run every day at 2 AM
//    @Scheduled(cron = "0 0 2 * * ?")
//    public void runReportJob() {
//        try {
//            // Example: last 1 day range
//            Calendar cal = Calendar.getInstance();
//            java.util.Date today = cal.getTime();
//            Date sqlToDate = new Date(today.getTime());
//
//            cal.add(Calendar.DATE, -1);
//            java.util.Date yesterday = cal.getTime();
//            Date sqlFromDate = new Date(yesterday.getTime());
//
//            List<NostroNegativeReportDto> results
//                    = reportService.fetchReport("940,950", "IN,US", sqlFromDate, sqlToDate);
//
//            System.out.println("=== Negative Balance Report ===");
//            results.forEach(System.out::println);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
