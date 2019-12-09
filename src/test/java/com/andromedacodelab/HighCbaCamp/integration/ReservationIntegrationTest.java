package com.andromedacodelab.HighCbaCamp.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.andromedacodelab.HighCbaCamp.TestUtil.parseFileToJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JSON_REQUEST_PATH = "src/test/resources/json/";

    @Test
    public void givenReservationValidRequest_whenCreateReservation_thenReturnReservationId() throws Exception {
        String jsonRequest = parseFileToJson(JSON_REQUEST_PATH + "create-reservation-request.json");
        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Reservation created successfully with bookingId: 1")));
    }

    @Test
    public void givenBookingId_whenFindReservationById_thenReturnReservation() throws Exception {
        String jsonRequest = parseFileToJson(JSON_REQUEST_PATH + "create-reservation-request.json");
        Integer bookingId = 1;
        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Reservation created successfully with bookingId: 1")));
        mockMvc.perform(get("/api/reservation?bookingId={bookingId}", bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId", is(bookingId)))
                .andExpect(jsonPath("$.arrival", is("2020-05-10 12:00:00 AM")))
                .andExpect(jsonPath("$.departure", is("2020-05-12 11:59:00 PM")));
    }

    @Test
    public void givenNewReservationStatus_whenUpdateReservationStatusOnly_thenReturnOkMessage() throws Exception {
        Integer bookingId = 1;
        String cancelledStatus = "CANCELLED";
        String newStatusJson = "{\"bookingId\": 1,\n \"statusId\": 4\n}";

        String jsonRequest = parseFileToJson(JSON_REQUEST_PATH + "create-reservation-request.json");
        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        mockMvc.perform(put("/api/reservation/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        is("Reservation with bookingId " + bookingId
                                + " status was updated to: " + cancelledStatus)));
    }

    @Test
    public void givenExistingBookingId_whenDeleteReservation_thenReturnOkMessage() throws Exception {
        Integer bookingId = 1;
        String jsonRequest = parseFileToJson(JSON_REQUEST_PATH + "create-reservation-request.json");
        mockMvc.perform(post("/api/reservation/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        mockMvc.perform(delete("/api/reservation/delete?bookingId={bookingId}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Deleted reservation with bookingId: " + bookingId)));
    }

    @Test
    public void givenValidDateRange_whenfindAvailability_thenReturnAvailableDatesMessage() throws Exception {
        mockMvc.perform(get("/api/availability?start=2020-05-10&end=2020-05-12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("The date range [arrival: 2020-05-10, departure: 2020-05-12] is available!")));
    }
}
