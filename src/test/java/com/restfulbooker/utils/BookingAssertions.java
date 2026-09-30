package com.restfulbooker.utils;

import com.restfulbooker.models.Booking;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public final class BookingAssertions {

    private BookingAssertions() {
        // Prevent object creation.
    }

    public static void validateBookingDetails(
            Response response,
            Booking expectedBooking,
            String rootPath
    ) {

        Assert.assertNotNull(
                response,
                "Booking response was not available"
        );

        Assert.assertNotNull(
                expectedBooking,
                "Expected booking data was not available"
        );

        Assert.assertNotNull(
                expectedBooking.getBookingdates(),
                "Expected booking dates were not available"
        );

        String prefix = rootPath == null || rootPath.isBlank()
                ? ""
                : rootPath.trim() + ".";

        JsonPath jsonPath = response.jsonPath();
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(
                jsonPath.getString(prefix + "firstname"),
                expectedBooking.getFirstname(),
                "Firstname did not match"
        );

        softAssert.assertEquals(
                jsonPath.getString(prefix + "lastname"),
                expectedBooking.getLastname(),
                "Lastname did not match"
        );

        softAssert.assertEquals(
                jsonPath.getObject(prefix + "totalprice", Integer.class),
                Integer.valueOf(expectedBooking.getTotalprice()),
                "Total price did not match"
        );

        softAssert.assertEquals(
                jsonPath.getObject(prefix + "depositpaid", Boolean.class),
                Boolean.valueOf(expectedBooking.isDepositpaid()),
                "Deposit-paid value did not match"
        );

        softAssert.assertEquals(
                jsonPath.getString(prefix + "bookingdates.checkin"),
                expectedBooking.getBookingdates().getCheckin(),
                "Check-in date did not match"
        );

        softAssert.assertEquals(
                jsonPath.getString(prefix + "bookingdates.checkout"),
                expectedBooking.getBookingdates().getCheckout(),
                "Check-out date did not match"
        );

        softAssert.assertEquals(
                jsonPath.getString(prefix + "additionalneeds"),
                expectedBooking.getAdditionalneeds(),
                "Additional needs did not match"
        );

        softAssert.assertAll();
    }
}
