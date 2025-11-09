package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jts.movie_ticket_booking_system.request.ShowRequest;
import com.jts.movie_ticket_booking_system.request.ShowSeatRequest;
import com.jts.movie_ticket_booking_system.services.ShowService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/show")
public class ShowController {

    @Autowired
    private ShowService showService;

    @PostMapping("/addNew")
    public ResponseEntity<String> addShow(@RequestBody ShowRequest showRequest) {
        try {
            String result = showService.addShow(showRequest);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/addMultiple")
    public ResponseEntity<Map<String, Object>> addMultipleShows(@RequestBody List<ShowRequest> showRequests) {
        try {
            Map<String, Object> response = new HashMap<>();
            List<String> results = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;

            for (ShowRequest showRequest : showRequests) {
                try {
                    String result = showService.addShow(showRequest);
                    results.add("Success: " + showRequest.getShowStartTime() + " - " + result);
                    successCount++;
                } catch (Exception e) {
                    results.add("Error: " + showRequest.getShowStartTime() + " - " + e.getMessage());
                    errorCount++;
                }
            }

            response.put("status", "completed");
            response.put("successCount", successCount);
            response.put("errorCount", errorCount);
            response.put("results", results);
            response.put("message", "Processed " + showRequests.size() + " shows");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to process shows: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/associateSeats")
    public ResponseEntity<String> associateShowSeats(@RequestBody ShowSeatRequest showSeatRequest) {
        try {
            String result = showService.associateShowSeats(showSeatRequest);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}