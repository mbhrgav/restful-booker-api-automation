package com.restfulbooker.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PartialUpdateDataMapper {

    private PartialUpdateDataMapper() {
        // Prevent object creation
    }

    public static Map<String, Object> toPartialUpdateMap(
            Map<String, String> excelData
    ) {

        Map<String, Object> requestBody =
                new LinkedHashMap<>();

        putStringIfPresent(
                requestBody,
                "firstname",
                excelData.get("Firstname")
        );

        putStringIfPresent(
                requestBody,
                "lastname",
                excelData.get("Lastname")
        );

        String totalPrice =
                excelData.get("TotalPrice");

        if (isPresent(totalPrice)) {
            requestBody.put(
                    "totalprice",
                    parseInteger(totalPrice)
            );
        }

        String depositPaid =
                excelData.get("DepositPaid");

        if (isPresent(depositPaid)) {
            requestBody.put(
                    "depositpaid",
                    Boolean.parseBoolean(depositPaid)
            );
        }

        Map<String, Object> bookingDates =
                new LinkedHashMap<>();

        putStringIfPresent(
                bookingDates,
                "checkin",
                excelData.get("Checkin")
        );

        putStringIfPresent(
                bookingDates,
                "checkout",
                excelData.get("Checkout")
        );

        if (!bookingDates.isEmpty()) {
            requestBody.put(
                    "bookingdates",
                    bookingDates
            );
        }

        putStringIfPresent(
                requestBody,
                "additionalneeds",
                excelData.get("AdditionalNeeds")
        );

        return requestBody;
    }

    private static void putStringIfPresent(
            Map<String, Object> target,
            String key,
            String value
    ) {

        if (isPresent(value)) {
            target.put(key, value.trim());
        }
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private static int parseInteger(String value) {

        String normalizedValue =
                value.replace(",", "").trim();

        return (int) Double.parseDouble(normalizedValue);
    }
}