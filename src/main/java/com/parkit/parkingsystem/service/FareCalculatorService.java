package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if(ticket.getOutTime() == null || ticket.getOutTime().isBefore(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        Duration nanoDuration = Duration.between(inTime, outTime);
        double duration = (double) nanoDuration.toMillis() / (60 * 60 * 1000);
//        double duration = duration2.toMillis() / (60 * 60 * 1000.0);

        duration = BigDecimal.valueOf(duration)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        if (duration <= 0.5) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }


    }
}