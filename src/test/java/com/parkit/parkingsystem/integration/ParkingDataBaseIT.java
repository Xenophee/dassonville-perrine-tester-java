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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final String vehicleRegNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception {
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
    public void printTestDivider() {
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

        // Récupérer le ticket de la base de données
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifier que le ticket est bien enregistré dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getParkingSpot().getId()).isInstanceOf(Integer.class);
        assertThat(ticket.getVehicleRegNumber()).isEqualTo(vehicleRegNumber);
        assertThat(ticket.getPrice()).isEqualTo(0);
        assertThat(ticket.getInTime()).isNotNull();
        assertThat(ticket.getOutTime()).isNull();

        // Vérifier que la disponibilité du parking est mise à jour correctement
        assertThat(ticket.getParkingSpot().isAvailable()).isFalse();
    }


    @Test
    public void testParkingLotExit() {
        testParkingACar();

        // Récupération du ticket de la base de données et modification de l'heure d'entrée
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        ticket.setInTime(ticket.getInTime().minusHours(1));
        dataBasePrepareService.updateInTimeTicket(ticket);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Récupérer le ticket final de la base de données
        ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifier que le ticket est bien mis à jour dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
        assertThat(ticket.getPrice()).isPositive();
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        testParkingLotExit();
        testParkingACar();

        // Récupération du ticket de la base de données et modification de l'heure d'entrée
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        ticket.setInTime(ticket.getInTime().minusHours(1));
        dataBasePrepareService.updateInTimeTicket(ticket);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Récupérer le ticket final de la base de données
        ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Vérifier que le ticket est bien mis à jour dans la base de données
        assertThat(ticket).isNotNull();
        assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
        assertThat(ticket.getPrice()).isLessThan(Fare.CAR_RATE_PER_HOUR);
    }
}
