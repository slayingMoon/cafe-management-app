package com.example.cafebackend.rest;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.model.binding.bill.BillReportModel;
import com.example.cafebackend.service.BillService;
import com.example.cafebackend.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping("/generateReport")
    public ResponseEntity<String> generateReport(@RequestBody BillReportModel billReportModel) {

        try {
            return billService.generateReport(billReportModel);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getPdf")
    public ResponseEntity<?> getPdf(@RequestBody BillReportModel billReportModel) {
        try {
            return billService.getPdf(billReportModel);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getBills")
    public ResponseEntity<?> getBills() {
        try {
            return billService.getBills();
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        try {
            return billService.deleteBill(id);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
