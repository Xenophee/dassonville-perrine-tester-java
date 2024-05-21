package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;






@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @BeforeEach
    public void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            Ticket ticket = new Ticket();
            ticket.setInTime(LocalDateTime.now().minusHours(1));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            lenient().when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @AfterEach
    public void printTestDivider() {
        System.out.println("\n******************************************************************");
    }


    @Test
    public void testProcessIncomingVehicle() throws Exception {
        parkingService.processIncomingVehicle();

        verify(inputReaderUtil).readVehicleRegistrationNumber();
        verify(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
        verify(ticketDAO).saveTicket(any(Ticket.class));
    }


    @Test
    public void processExitingVehicleTest() {
        parkingService.processExitingVehicle();

        verify(ticketDAO).getTicket(anyString());
        verify(ticketDAO).getNbTicket(anyString());
        verify(ticketDAO).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processExitingVehicleFreeParkingTest() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now().minusMinutes(15));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

        parkingService.processExitingVehicle();

        verify(ticketDAO).getTicket(anyString());
        verify(ticketDAO).getNbTicket(anyString());
        verify(ticketDAO).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void processExitingVehicleTestUnableUpdateTicket() {
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
        verify(ticketDAO).getTicket(anyString());
        verify(ticketDAO).getNbTicket(anyString());
        verify(ticketDAO).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testGetNextParkingNumberIfAvailable() {
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot.getId()).isEqualTo(1);
        assertThat(parkingSpot.isAvailable()).isTrue();
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() throws Exception {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot).isNull();
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        when(inputReaderUtil.readSelection()).thenReturn(3);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot).isNull();
    }

}
