package com.example.cafebackend.model.mapper;

import com.example.cafebackend.model.binding.bill.BillReportModel;
import com.example.cafebackend.model.entity.Bill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillMapper {
    Bill billDTOtoBillEntity(BillReportModel billReportModel);
}
