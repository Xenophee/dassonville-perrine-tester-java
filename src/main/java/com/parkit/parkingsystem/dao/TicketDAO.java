package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * The TicketDAO class is responsible for managing database operations related to tickets.
 */
public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

//    public boolean saveTicket(Ticket ticket) {
//        Connection con = null;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
//            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
//            //ps.setInt(1,ticket.getId());
//            ps.setInt(1, ticket.getParkingSpot().getId());
//            ps.setString(2, ticket.getVehicleRegNumber());
//            ps.setDouble(3, ticket.getPrice());
//            ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
//            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : Timestamp.valueOf(ticket.getOutTime()));
//            return ps.execute();
//        } catch (Exception ex) {
//            logger.error("Error fetching next available slot", ex);
//        } finally {
//            dataBaseConfig.closeConnection(con);
//            return false;
//        }
//    }

    /**
     * This method is used to save a ticket in the database.
     *
     * @param ticket The ticket to be saved.
     * @return A boolean indicating whether the operation was successful.
     */
    public boolean saveTicket(Ticket ticket) {
        try (
                Connection con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)
        ) {
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : Timestamp.valueOf(ticket.getOutTime()));
            return ps.execute();
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Error saving ticket info", ex);
            return false;
        }
    }

//    public int getNbTicket(String vehicleRegNumber) {
//        Connection con = null;
//        int nbTickets = 0;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NB_TICKETS);
//            ps.setString(1,vehicleRegNumber);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                nbTickets = rs.getInt(1);
//            }
//
//            dataBaseConfig.closeResultSet(rs);
//            dataBaseConfig.closePreparedStatement(ps);
//
//        } catch (Exception ex) {
//            logger.error("Error fetching ticket's number",ex);
//        } finally {
//            dataBaseConfig.closeConnection(con);
//            return nbTickets;
//        }
//    }

    /**
     * This method is used to get the number of tickets associated with a vehicle registration number.
     *
     * @param vehicleRegNumber The vehicle's registration number.
     * @return The number of tickets associated with the vehicle's registration number.
     */
    public int getNbTicket(String vehicleRegNumber) {
        int nbTickets = 0;
        try (
                Connection con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.GET_NB_TICKETS);
        ) {
            ps.setString(1, vehicleRegNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nbTickets = rs.getInt(1);
            }
        } catch (Exception ex) {
            logger.error("Error fetching ticket's number", ex);
        }
        return nbTickets;
    }


//    public Ticket getTicket(String vehicleRegNumber) {
//        Connection con = null;
//        Ticket ticket = null;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
//            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
//            ps.setString(1, vehicleRegNumber);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                ticket = new Ticket();
//                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
//                ticket.setParkingSpot(parkingSpot);
//                ticket.setId(rs.getInt(2));
//                ticket.setVehicleRegNumber(vehicleRegNumber);
//                ticket.setPrice(rs.getDouble(3));
//
//                Timestamp inTime = rs.getTimestamp(4);
//                ticket.setInTime((inTime != null) ? inTime.toLocalDateTime() : null);
//                Timestamp outTime = rs.getTimestamp(5);
//                ticket.setOutTime((outTime != null) ? outTime.toLocalDateTime() : null);
//            }
//            dataBaseConfig.closeResultSet(rs);
//            dataBaseConfig.closePreparedStatement(ps);
//        } catch (Exception ex) {
//            logger.error("Error fetching next available slot", ex);
//        } finally {
//            dataBaseConfig.closeConnection(con);
//            return ticket;
//        }
//    }

    /**
     * This method is used to get a ticket from a vehicle's registration number.
     *
     * @param vehicleRegNumber The vehicle's registration number.
     * @return The ticket associated with the vehicle's registration number.
     */
    public Ticket getTicket(String vehicleRegNumber) {
        Ticket ticket = null;
        try (
                Connection con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)
        ) {
            ps.setString(1, vehicleRegNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket = new Ticket();
                    ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setId(rs.getInt(2));
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(rs.getDouble(3));

                    Timestamp inTime = rs.getTimestamp(4);
                    ticket.setInTime((inTime != null) ? inTime.toLocalDateTime() : null);
                    Timestamp outTime = rs.getTimestamp(5);
                    ticket.setOutTime((outTime != null) ? outTime.toLocalDateTime() : null);
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Error fetching next available slot", ex);
        }
        return ticket;
    }

//    public boolean updateTicket(Ticket ticket) {
//        Connection con = null;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
//            ps.setDouble(1, ticket.getPrice());
//            ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
//            ps.setInt(3, ticket.getId());
//            ps.execute();
//            return true;
//        } catch (Exception ex) {
//            logger.error("Error saving ticket info", ex);
//        } finally {
//            dataBaseConfig.closeConnection(con);
//        }
//        return false;
//    }

    /**
     * This method is used to update a ticket in the database.
     *
     * @param ticket The ticket to be updated.
     * @return A boolean indicating whether the operation was successful.
     */
    public boolean updateTicket(Ticket ticket) {
        try (
                Connection con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)
        ) {

            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
            ps.setInt(3, ticket.getId());
            ps.execute();
            return true;

        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Error saving ticket info", ex);
        }
        return false;
    }
}
