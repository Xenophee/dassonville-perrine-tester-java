package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * This class is responsible for managing the parking spot data access operations.
 */
public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

//    public int getNextAvailableSlot(ParkingType parkingType) {
//        Connection con = null;
//        int result = -1;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
//            ps.setString(1, parkingType.toString());
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                result = rs.getInt(1);
//                ;
//            }
//            dataBaseConfig.closeResultSet(rs);
//            dataBaseConfig.closePreparedStatement(ps);
//        } catch (Exception ex) {
//            logger.error("Error fetching next available slot", ex);
//        } finally {
//            dataBaseConfig.closeConnection(con);
//        }
//        return result;
//    }

    /**
     * This method is used to get the next available parking slot.
     *
     * @param parkingType The type of the parking spot.
     * @return An integer representing the next available parking slot.
     * @throws Exception If an error occurs while fetching the parking number from the database.
     */
    public int getNextAvailableSlot(ParkingType parkingType) throws Exception {
        int result = -1;
        try (
                Connection con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)
        ) {
            ps.setString(1, parkingType.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new Exception("Error fetching parking number from DB. Parking slots might be full");
        }
        return result;
    }

//    public boolean updateParking(ParkingSpot parkingSpot){
//        //update the availability fo that parking slot
//        Connection con = null;
//        try {
//            con = dataBaseConfig.getConnection();
//            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
//            ps.setBoolean(1, parkingSpot.isAvailable());
//            ps.setInt(2, parkingSpot.getId());
//            int updateRowCount = ps.executeUpdate();
//            dataBaseConfig.closePreparedStatement(ps);
//            return (updateRowCount == 1);
//        }catch (Exception ex){
//            logger.error("Error updating parking info",ex);
//            return false;
//        }finally {
//            dataBaseConfig.closeConnection(con);
//        }

    /**
     * This method is used to update the availability of a parking spot.
     *
     * @param parkingSpot The parking spot to be updated.
     * @return A boolean indicating whether the update was successful.
     */
    public boolean updateParking(ParkingSpot parkingSpot) {
        try (
                Connection connection = dataBaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)
        ) {

            statement.setBoolean(1, parkingSpot.isAvailable());
            statement.setInt(2, parkingSpot.getId());

            int updatedRows = statement.executeUpdate();

            return (updatedRows == 1);
        } catch (Exception ex) {
            logger.error("Error updating parking info", ex);
            return false;
        }
    }
}

