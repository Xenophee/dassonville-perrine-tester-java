package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.parkit.parkingsystem.constants.Discount.MAX_HOURS_FREE_PARKING;
import static com.parkit.parkingsystem.constants.Discount.RECURRING_USER_DISCOUNT_PERCENTAGE;
import static com.parkit.parkingsystem.util.NumbersUtil.roundDecimals;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

    public void calculateFare(Ticket ticket, boolean discount) {
        if (ticket.getOutTime() == null || ticket.getOutTime().isBefore(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        long durationInMinutes = Duration.between(inTime, outTime).toMinutes();
        double durationInHours = roundDecimals(durationInMinutes / 60.0, 2);

        if (durationInHours <= MAX_HOURS_FREE_PARKING) return;

        double price;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                price = durationInHours * Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                price =  durationInHours * Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }

        if (discount) price *= RECURRING_USER_DISCOUNT_PERCENTAGE;

        ticket.setPrice(roundDecimals(price, 2));
    }
}