package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final String vehicleRegNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterEach
    public void printDividerPerTest() {
        System.out.println("\n******************************************************************");
    }

    @AfterAll
    public static void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }


    @Test
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        // Récupère le ticket de la base de données
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifie que le ticket est bien enregistré dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getParkingSpot().getId()).isInstanceOf(Integer.class);
        assertThat(ticket.getVehicleRegNumber()).isEqualTo(vehicleRegNumber);
        assertThat(ticket.getPrice()).isEqualTo(0);
        assertThat(ticket.getInTime()).isNotNull();
        assertThat(ticket.getOutTime()).isNull();

        // Vérifie que la disponibilité du parking est mise à jour correctement
        boolean parkingSpotFree = dataBasePrepareService.getParkingSpotAvailableStatus();
        assertThat(parkingSpotFree).isFalse();
    }


    @Test
    public void testParkingLotExit() {
        dataBasePrepareService.insertACar(1);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Récupère le ticket de la base de données
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifie que le ticket est bien mis à jour dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
        assertThat(ticket.getPrice()).isPositive();

        // Vérifie que la disponibilité du parking est mise à jour correctement
        boolean parkingSpotFree = dataBasePrepareService.getParkingSpotAvailableStatus();
        assertThat(parkingSpotFree).isTrue();
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        dataBasePrepareService.insertACar(2);
        dataBasePrepareService.updateACarExit();

        dataBasePrepareService.insertACar(1);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Récupère le ticket de la base de données
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifie que le ticket est bien mis à jour dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
        assertThat(ticket.getPrice()).isLessThan(Fare.CAR_RATE_PER_HOUR);

        // Vérifie que la disponibilité du parking est mise à jour correctement
        boolean parkingSpotFree = dataBasePrepareService.getParkingSpotAvailableStatus();
        assertThat(parkingSpotFree).isTrue();
    }
}
