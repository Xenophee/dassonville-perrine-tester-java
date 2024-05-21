package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public void updateInTimeTicket(Ticket ticket) {
        try (
                Connection con = dataBaseTestConfig.getConnection();
                PreparedStatement ps = con.prepareStatement("update ticket set IN_TIME=? where ID=?")
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(ticket.getInTime()));
            ps.setInt(2, ticket.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
