package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.jwt.JwtFilter;
import com.example.cafebackend.model.binding.bill.BillReportModel;
import com.example.cafebackend.model.binding.product.ProductDetailsModel;
import com.example.cafebackend.model.entity.Bill;
import com.example.cafebackend.repository.BillRepository;
import com.example.cafebackend.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    JwtFilter jwtFilter;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ResponseEntity<String> generateReport(BillReportModel billReportModel) {
        log.info("Generating report...");

        try {
            String fileName;

            if (billReportModel.getUuid() != null && billRepository.findByUUID(billReportModel.getUuid()).isPresent()) {

                //if bill is present
                //get uuid
                fileName = billReportModel.getUuid();
            }else {
                //else generate uuid
                fileName = CafeUtils.getUUID();
                //save bill in DB
                generateBill(billReportModel, fileName);
            }

            String data = String.format("Name: %s%n" +
                    "Contact Number %s%n" +
                    "Email: %s%n" +
                    "Payment Method: %s", billReportModel.getName(), billReportModel.getContactNumber(),
                    billReportModel.getEmail(), billReportModel.getPaymentMethod());

            //CREATE THE BILL DOCUMENT
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));

            //ALWAYS OPEN FIRST AND CLOSE YOUR DOCUMENT AT THE END
            document.open();

            //CONSTRUCT RECTANGLE
            setRectangleInPdf(document);

            //DOCUMENT HEADER
            Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
            chunk.setAlignment(Element.ALIGN_CENTER);
            document.add(chunk);

            //DATA INFO
            Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
            document.add(paragraph);

            //CONSTRUCT TABLE
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table);

            //ADD DATA TO TABLE
            JSONArray jsonArray = CafeUtils.getJsonArrayFromString(billReportModel.getProductDetails());

            for (int i = 0; i < jsonArray.length(); i++) {
                String s = jsonArray.getString(i);
                addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
            }

            //ADD TABLE TO DOCUMENT
            document.add(table);

            //ADD FOOTER TO DOCUMENT
            //BUGFIX: formats total in PDF to second digit after decimal point
            Paragraph footer = new Paragraph(String.format("Total : %.2f%n" +
                    "Thank you for visiting. Please visit again!", new BigDecimal(billReportModel.getTotalAmount())), getFont("Data"));
            document.add(footer);

            //CLOSE DOCUMENT
            document.close();

            return CafeUtils.getBillResponseEntity(fileName, HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, ProductDetailsModel data) {
        log.info("Inside addRows");

            table.addCell(data.getName());
            table.addCell(data.getCategory());
            table.addCell(data.getQuantity());
            table.addCell(data.getPrice());
            //BUGFIX: formats total to second digit after decimal point
            table.addCell(String.format("%.2f", new BigDecimal(data.getTotal())));
        System.out.println();

    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");

        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    //add cell for each column header
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");

        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRactangleInPdf");
        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void generateBill(BillReportModel billReportModel, String fileName) {
        Bill newBill = new Bill();
        newBill.setUuid(fileName);
        newBill.setName(billReportModel.getName());
        newBill.setEmail(billReportModel.getEmail());
        newBill.setContactNumber(billReportModel.getContactNumber());
        newBill.setPaymentMethod(billReportModel.getPaymentMethod());
        newBill.setTotal(new BigDecimal(billReportModel.getTotalAmount()));
        newBill.setProductDetails(billReportModel.getProductDetails());
        newBill.setCreatedBy(jwtFilter.getCurrentUser());
        billRepository.save(newBill);
    }

    public ResponseEntity<?> getBills() {

        if (jwtFilter.isAdmin()) {
            return ResponseEntity.ok(em.createQuery("select b from Bill b order by b.id desc", Bill.class)
                    .getResultList());
        }else {
            return ResponseEntity.ok(em.createQuery("select b from Bill b " +
                    "where b.createdBy=:username order by b.id desc", Bill.class)
                    .setParameter("username", jwtFilter.getCurrentUser())
                    .getResultList());
        }

    }

    public ResponseEntity<?> getPdf(BillReportModel billReportModel) {
        log.info("Inside getPdf : billreportModel {}", billReportModel);

        try {
            byte[] byteArray = new byte[0];
            if (Objects.isNull(billReportModel.getUuid())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String filePath = String.format("%s\\%s.pdf", CafeConstants.STORE_LOCATION, billReportModel.getUuid());

            if (CafeUtils.isFileExisting(filePath)) {
                byteArray = getByteArray(filePath);
                return ResponseEntity.ok(byteArray);
            }else {
                generateReport(billReportModel);
                byteArray = getByteArray(filePath);
                return ResponseEntity.ok(byteArray);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity("File not found", HttpStatus.NOT_FOUND);
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    public ResponseEntity<?> deleteBill(Long id) {
        try {
            Optional<Bill> billOpt = billRepository.findById(id);

            if (billOpt.isPresent()) {
                billRepository.deleteById(id);
                return CafeUtils.getResponseEntity("Bill Deleted Successfully.", HttpStatus.OK);
            }

            return CafeUtils.getResponseEntity("Bill id does not exist.", HttpStatus.NOT_FOUND);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
