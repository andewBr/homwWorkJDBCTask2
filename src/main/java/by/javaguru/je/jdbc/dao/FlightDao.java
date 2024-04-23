package by.javaguru.je.jdbc.dao;

import by.javaguru.je.jdbc.entity.Flight;
import by.javaguru.je.jdbc.entity.FlightStatus;
import by.javaguru.je.jdbc.exception.DaoException;
import by.javaguru.je.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.javaguru.je.jdbc.utils.ConnectionManager.open;

public class FlightDao implements Dao<Long, Flight> {

    private final static FlightDao INSTANCE = new FlightDao();

    private final static String FIND_ALL_SQL = """
            SELECT id, flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, aircraft_id, status
            FROM flight
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    public final static String UPDATE_BY_ID_SQL = """
            UPDATE flight SET
                  flight_no = ?,
                departure_date = ?,
                departure_airport_code = ?,
                arrival_date = ?,
                arrival_airport_code = ?,
                aircraft_id = ?,
                status = ?
            where id = ?
            """;

    public final static String DELETE_BY_ID_SQL = """
            DELETE FROM flight where id = ?;
            """;

    public final static String SAVE_FLIGHT_SQL = """
            INSERT INTO flight (flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, aircraft_id, status)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

    @Override
    public boolean update(Flight flight) throws SQLException {
        try (Connection connection = open()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
            PreparedStatement statementBuild = setFlight(statement, flight);
            return statementBuild.executeUpdate() > 1;
        }
    }

    @Override
    public List<Flight> findAll() {
        try (Connection connection = open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ArrayList<Flight> flights = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                flights.add(
                        buildFlight(result)
                );
            }
            return flights;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Flight buildFlight(ResultSet result) throws SQLException {
        return new Flight(
                result.getLong("id"),
                result.getString("flight_no"),
                result.getTimestamp("departure_date").toLocalDateTime(),
                result.getString("departure_airport_code"),
                result.getTimestamp("arrival_date").toLocalDateTime(),
                result.getString("id"),
                result.getInt("id"),
                FlightStatus.valueOf(result.getString("status"))
        );
    }

    private PreparedStatement setFlight(PreparedStatement statement, Flight flight) throws SQLException {
        statement.setString(1, flight.getFlight_no());
        statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
        statement.setString(3, flight.getDepartureAirportCode());
        statement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
        statement.setString(5, flight.getArrivalAirportCode());
        statement.setInt(6, flight.getAircraftId());
        statement.setString(7, String.valueOf(flight.getStatus()));
        return statement;
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionManager.open()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            Flight flight = null;
            if (result.next())
                flight = buildFlight(result);
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Flight save(Flight flight) throws SQLException {

        try (Connection connection = open()) {
            PreparedStatement statement = connection.prepareStatement(SAVE_FLIGHT_SQL);
            PreparedStatement resultStatement = setFlight(statement, flight);
            return resultStatement.executeUpdate() > 1 ? flight : null;
        }
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        try (Connection connection = open()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL);
            statement.setLong(1, id);

            return statement.executeUpdate() > 1;
        }
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    private FlightDao() {
    }
}
