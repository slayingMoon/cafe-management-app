package com.example.cafebackend.model.binding.bill;

import com.example.cafebackend.model.binding.product.ProductDetailsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillReportModel {

    @NotBlank
    private String fileName;

    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;

    @NotBlank
    private String contactNumber;

    @NotBlank
    private String paymentMethod;

    @NotNull
    private String totalAmount;

    @NotBlank
    private String productDetails;

    private String uuid;
}
