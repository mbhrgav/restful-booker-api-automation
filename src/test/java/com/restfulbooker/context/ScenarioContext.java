package com.restfulbooker.context;

import io.restassured.response.Response;
import java.util.LinkedHashSet;
import java.util.Set;

public class ScenarioContext {

    private Response response;
    private String token;
    private Integer bookingId;
    private final Set<Integer> createdBookingIds =
            new LinkedHashSet<>();

    public void registerCreatedBooking(Response createResponse) {

        int statusCode = createResponse.statusCode();

        // Failed creation responses normally contain no created booking.
        if (statusCode < 200 || statusCode >= 300) {
            return;
        }

        Integer createdId = createResponse.jsonPath()
                .getObject("bookingid", Integer.class);

        if (createdId != null && createdId > 0) {
            createdBookingIds.add(createdId);
        }
    }

    public Set<Integer> getCreatedBookingIds() {
        return new LinkedHashSet<>(createdBookingIds);
    }

    public void removeCreatedBookingId(Integer bookingId) {
        createdBookingIds.remove(bookingId);
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {this.bookingId = bookingId;}
}