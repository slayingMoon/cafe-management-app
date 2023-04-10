package com.example.cafebackend.service;

import com.example.cafebackend.model.binding.bill.BillReportModel;
import org.springframework.http.ResponseEntity;

public interface BillService {
    ResponseEntity<String> generateReport(BillReportModel billReportModel);
    ResponseEntity<?> getBills();
    ResponseEntity<?> getPdf(BillReportModel billReportModel);
    ResponseEntity<?> deleteBill(Long id);
}
