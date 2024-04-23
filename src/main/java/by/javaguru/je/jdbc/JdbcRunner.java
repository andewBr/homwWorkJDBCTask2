package by.javaguru.je.jdbc;

import by.javaguru.je.jdbc.dao.FlightDao;
import by.javaguru.je.jdbc.dao.TicketDao;
import by.javaguru.je.jdbc.dto.TicketFilter;
import by.javaguru.je.jdbc.entity.Ticket;
import by.javaguru.je.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) {
        var ticketDao = TicketDao.getInstance();
        System.out.println(ticketDao.findById(3l));

//        var flightDao = FlightDao.getInstance();
//        System.out.println(flightDao.findAll());
    }

    private static void executeRequest() throws SQLException {
        System.out.println(getTickersByFlightID(8L));

        System.out.println(getFlightsBetween(
                LocalDate.of(2020, 04, 01).atStartOfDay(),
                LocalDate.of(2020, 8, 01).atStartOfDay()
        ));
    }

    public static List<Long> getTickersByFlightID(long flightId) throws SQLException {
        ArrayList<Long> tickets = new ArrayList<>();
        String sql = """
                SELECT * from ticket
                where flight_id = ?;
                """;

        try (Connection connection = ConnectionManager.open()) {
            Statement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                tickets.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        ArrayList<Long> flights = new ArrayList<>();
        String sql = """
                select * from flight
                where departure_date between ? and ?;
                """;

        try (Connection connection = ConnectionManager.open()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));
            var result = statement.executeQuery();
            while (result.next()) {
                flights.add(result.getLong("id"));
            }
        }
        return flights;
    }

    public static void checkMetaData() throws SQLException {
        try (Connection connection = ConnectionManager.open()) {
            var metaData = connection.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
                System.out.println(catalogs.getString(1));
            }
        }
    }

}
