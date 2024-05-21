package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.parkit.parkingsystem.util.ConsoleColorsUtil.*;

/**
 * This class is responsible for managing the interactive shell.
 */
public class InteractiveShell {

    private static final Logger logger = LogManager.getLogger("InteractiveShell");

    /**
     * This method is used to load the interface.
     */
    public static void loadInterface(){
        logger.info("App initialized!!!");
        System.out.println(colorString(" WELCOME TO PARKING SYSTEM ! ", PURPLE_BACKGROUND, BLACK_BOLD));

        boolean continueApp = true;
        InputReaderUtil inputReaderUtil = new InputReaderUtil();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        while(continueApp){
            loadMenu();
            int option = inputReaderUtil.readSelection();
            switch(option){
                case 1: {
                    parkingService.processIncomingVehicle();
                    break;
                }
                case 2: {
                    parkingService.processExitingVehicle();
                    break;
                }
                case 3: {
                    System.out.println("Exiting from the system!");
                    continueApp = false;
                    break;
                }
                default: System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
            }
        }
    }

    /**
     * This method is used to load the menu.
     */
    private static void loadMenu(){
        System.out.println("\n\n--------------------------------------------------");
        System.out.println("Please select an option. Simply enter the number to choose an action");
        System.out.println(colorString("1 New Vehicle Entering - Allocate Parking Space", PURPLE));
        System.out.println(colorString("2 Vehicle Exiting - Generate Ticket Price", PURPLE));
        System.out.println(colorString("3 Shutdown System", PURPLE));
    }

}
