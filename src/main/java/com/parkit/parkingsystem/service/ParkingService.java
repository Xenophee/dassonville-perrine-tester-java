package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

import static com.parkit.parkingsystem.constants.Discount.MINIMUM_VISITS_FOR_DISCOUNT;
import static com.parkit.parkingsystem.util.ConsoleColorsUtil.*;
import static com.parkit.parkingsystem.util.DatesUtil.formatDate;

/**
 * This class is responsible for managing the parking service.
 */
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private final InputReaderUtil inputReaderUtil;
    private final ParkingSpotDAO parkingSpotDAO;
    private final TicketDAO ticketDAO;

    /**
     * Constructor for ParkingService.
     *
     * @param inputReaderUtil An instance of InputReaderUtil.
     * @param parkingSpotDAO  An instance of ParkingSpotDAO.
     * @param ticketDAO       An instance of TicketDAO.
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * This method is used to process incoming vehicles.
     * <p>
     * It performs the following operations:
     * <ul>
     *     <li>It gets the next available parking spot with {@link #getNextParkingNumberIfAvailable()}.</li>
     *     <li>It gets the vehicle registration number with {@link #getVehicleRegNumber()}.</li>
     *     <li>It updates the parking spot's availability with {@link ParkingSpotDAO#updateParking(ParkingSpot)}.</li>
     *     <li>It saves the ticket information with {@link TicketDAO#saveTicket(Ticket)}.</li>
     *     <li>If the vehicle is a recurring user, it displays a message for announcing the discount.</li>
     *     <li>It displays the parking spot number and the in-time.</li>
     * </ul>
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null) {
                String vehicleRegNumber = getVehicleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                LocalDateTime inTime = LocalDateTime.now();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);

                if (ticketDAO.getNbTicket(vehicleRegNumber) > MINIMUM_VISITS_FOR_DISCOUNT) {
                    System.out.println("\n" + colorString(" Welcome back ! As a recurring user of our parking lot, you'll benefit from a 5% discount. ", BLUE_BACKGROUND, BLACK_BOLD));
                }

                System.out.println("\nGenerated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number : " + colorIntNumber(parkingSpot.getId(), BLUE));
                System.out.println("Recorded in-time for vehicle number : " + colorString(vehicleRegNumber, BLUE) + " is : " + colorString(formatDate(inTime), BLUE));
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * This method is used to get the vehicle registration number.
     *
     * @return A string representing the vehicle registration number.
     * @throws Exception If an error occurs while reading the vehicle registration number.
     */
    private String getVehicleRegNumber() throws Exception {
        System.out.println("\nPlease type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * This method is used to get the next available parking number.
     * It calls the following methods:
     * <ul>
     *     <li>{@link #getVehicleType()} to get the type of the vehicle.</li>
     *     <li>{@link ParkingSpotDAO#getNextAvailableSlot(ParkingType)} to get the next available parking slot.</li>
     * </ul>
     *
     * @return An instance of ParkingSpot representing the next available parking spot.
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehicleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    /**
     * This method is used to displays a menu to the user to select the vehicle type.
     * It reads the user's selection with {@link InputReaderUtil#readSelection()}.
     *
     * @return An instance of ParkingType representing the type of the vehicle based on the user's selection.
     * @throws IllegalArgumentException If an invalid input is provided.
     */
    public ParkingType getVehicleType() {
        System.out.println("\nPlease select vehicle type from menu");
        System.out.println(colorString("1 CAR", PURPLE));
        System.out.println(colorString("2 BIKE", PURPLE));
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    /**
     * This method is used to process exiting vehicles.
     * It performs the following operations:
     * <ul>
     *     <li>It gets the vehicle registration number with {@link #getVehicleRegNumber()}.</li>
     *     <li>It gets the ticket information with {@link TicketDAO#getTicket(String)}.</li>
     *     <li>It calculates the fare with {@link FareCalculatorService#calculateFare(Ticket, boolean)}.</li>
     *     <li>It updates the ticket information with {@link TicketDAO#updateTicket(Ticket)}.</li>
     *     <li>It updates the parking spot's availability with {@link ParkingSpotDAO#updateParking(ParkingSpot)}.</li>
     *     <li>If the parking fare is 0, it displays a message to inform the user that the parking time is less than 30 minutes.</li>
     *     <li>It displays the parking fare to pay and the out-time.</li>
     * </ul>
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            LocalDateTime outTime = LocalDateTime.now();
            ticket.setOutTime(outTime);

            boolean discount = ticketDAO.getNbTicket(vehicleRegNumber) > MINIMUM_VISITS_FOR_DISCOUNT;
            fareCalculatorService.calculateFare(ticket, discount);

            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);

                if ((ticket.getPrice() == 0)) {
                    System.out.println("\n" + colorString(" Parking time less than 30 minutes is free ", BLUE_BACKGROUND, BLACK_BOLD));
                } else {
                    System.out.println("\nPlease pay the parking fare : " + colorDoubleNumber(ticket.getPrice(), BLUE));
                }

                System.out.println("Recorded out-time for vehicle number : " + colorString(ticket.getVehicleRegNumber(), BLUE) + " is : " + colorString(formatDate(outTime), BLUE));

            } else {
                System.err.println("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }
}
