package com.example.cafebackend.utils;

import com.example.cafebackend.model.binding.product.ProductDetailsModel;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;

@Slf4j
public class CafeUtils {

    public static String getUUID() {
        Date date = new Date();
        long time = date.getTime();
        return String.format("BILL-%s", time);
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        return new JSONArray(data);
    }

    public static ProductDetailsModel getMapFromJson(String data) {
        return new Gson().fromJson(data, ProductDetailsModel.class);
    }

    public static Boolean isFileExisting(String path) {
        log.info("Inside isFileExisting {}", path);

        try {
            File file = new File(path);
            return file.exists();
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }

    public static ResponseEntity<String> getBillResponseEntity(String uuid, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"uuid\":\"" + uuid + "\"}", httpStatus);
    }
}
