package com.restfulbooker.utils;

import com.restfulbooker.models.Booking;
import com.restfulbooker.models.BookingDates;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BookingDataMapper {

    private BookingDataMapper() {
        // Prevent object creation
    }

    public static Booking toBooking(
            Map<String, String> excelData
    ) {

        BookingDates bookingDates = new BookingDates(
                excelData.get("Checkin"),
                excelData.get("Checkout")
        );

        return new Booking(
                excelData.get("Firstname"),
                excelData.get("Lastname"),
                parseInteger(excelData.get("TotalPrice")),
                Boolean.parseBoolean(
                        excelData.get("DepositPaid")
                ),
                bookingDates,
                excelData.get("AdditionalNeeds")
        );
    }

    public static Map<String, Object> toRequestMap(
            Map<String, String> excelData
    ) {

        Map<String, Object> bookingDates =
                new LinkedHashMap<>();

        bookingDates.put(
                "checkin",
                excelData.get("Checkin")
        );

        bookingDates.put(
                "checkout",
                excelData.get("Checkout")
        );

        Map<String, Object> bookingData =
                new LinkedHashMap<>();

        bookingData.put(
                "firstname",
                excelData.get("Firstname")
        );

        bookingData.put(
                "lastname",
                excelData.get("Lastname")
        );

        bookingData.put(
                "totalprice",
                parseInteger(excelData.get("TotalPrice"))
        );

        bookingData.put(
                "depositpaid",
                Boolean.parseBoolean(
                        excelData.get("DepositPaid")
                )
        );

        bookingData.put(
                "bookingdates",
                bookingDates
        );

        bookingData.put(
                "additionalneeds",
                excelData.get("AdditionalNeeds")
        );

        return bookingData;
    }

    public static void removeField(
            Map<String, Object> bookingData,
            String fieldName
    ) {

        if (
                fieldName.equalsIgnoreCase("checkin")
                || fieldName.equalsIgnoreCase("checkout")
        ) {

            Object bookingDatesObject =
                    bookingData.get("bookingdates");

            if (!(bookingDatesObject instanceof Map<?, ?>)) {
                throw new IllegalStateException(
                        "Booking dates were not available"
                );
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> bookingDates =
                    (Map<String, Object>) bookingDatesObject;

            bookingDates.remove(fieldName.toLowerCase());
            return;
        }

        String matchingKey = bookingData
                .keySet()
                .stream()
                .filter(
                        key -> key.equalsIgnoreCase(fieldName)
                )
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Unsupported field to remove: "
                                        + fieldName
                        )
                );

        bookingData.remove(matchingKey);
    }

    private static int parseInteger(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Numeric Excel value must not be blank"
            );
        }

        String normalizedValue =
                value.replace(",", "").trim();

        return (int) Double.parseDouble(normalizedValue);
    }
}