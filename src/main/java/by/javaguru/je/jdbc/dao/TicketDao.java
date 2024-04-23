package by.javaguru.je.jdbc.dao;

import by.javaguru.je.jdbc.dto.TicketFilter;
import by.javaguru.je.jdbc.entity.Flight;
import by.javaguru.je.jdbc.entity.FlightStatus;
import by.javaguru.je.jdbc.entity.Ticket;
import by.javaguru.je.jdbc.exception.DaoException;
import by.javaguru.je.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static by.javaguru.je.jdbc.utils.ConnectionManager.open;

public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    private static final FlightDao flightDao = FlightDao.getInstance();

    private final static String SAVE_SQL = """
            INSERT INTO ticket(passport_no, passenger_name, flight_id, seat_no, cost) 
            values (?,?,?,?,?) 
            """;

    private final static String DELETE_SQL = """
            DELETE from ticket where id = ?
            """;


    private final static String FIND_ALL_SQL = """
            SELECT t.id, t.passport_no, t.passenger_name, t.flight_id, t.seat_no, t.cost,
            f.flight_no, f.departure_date, f.departure_airport_code, f.arrival_date, f.arrival_airport_code, f.aircraft_id, f.status
            from ticket t
            JOIN flight f on f.id = t.flight_id
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE t.id = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE ticket
            set passport_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ? 
                where id = ?
            """;


    private TicketDao() {
    }

    private static Ticket buildTicket(ResultSet result) throws SQLException {
        /*var fligth = new Flight(
                result.getLong("flight_id"),
                result.getString("flight_no"),
                result.getTimestamp("departure_date").toLocalDateTime(),
                result.getString("departure_airport_code"),
                result.getTimestamp("arrival_date").toLocalDateTime(),
                result.getString("id"),
                result.getInt("id"),
                FlightStatus.valueOf(result.getString("status"))
        );*/

        return new Ticket(result.getLong("id"),
                result.getString("passport_no"),
                result.getString("passenger_name"),
                flightDao.findById(
                        result.getLong("flight_id"),
                        result.getStatement().getConnection()
                ).orElse(null),
                result.getString("seat_no"),
                result.getBigDecimal("cost")
        );
    }

    public boolean update(Ticket ticket) {
        try (Connection connection = open();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, ticket.getPassportNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlight().getId());
            statement.setString(4, ticket.getSeatNo());
            statement.setBigDecimal(5, ticket.getCost());
            statement.setLong(6, ticket.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> findAll() {
        try (Connection connection = open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ArrayList<Ticket> tickets = new ArrayList<>();
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                tickets.add(
                        buildTicket(result)
                );
            }
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> findAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.passengerName() != null) {
            parameters.add(filter.passengerName());
            whereSql.add("passenger_name = ?");
        }
        if (filter.seatNo() != null) {
            parameters.add("%" + filter.seatNo() + "%");
            whereSql.add("seat_no like ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());

        var where = whereSql.stream().collect(Collectors.joining(
                " AND ",
                parameters.size() > 2 ? " WHERE " : " ",
                " LIMIT ? OFFSET ? "
        ));
        String sql = FIND_ALL_SQL + where;
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(sql)) {
            ArrayList<Ticket> tickets = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            System.out.println(statement);
            var result = statement.executeQuery();
            while (result.next()) {
                tickets.add(
                        buildTicket(result)
                );
            }
            return tickets;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Ticket> findById(Long id) {
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();

            Ticket ticket = null;
            if (result.next())
                ticket = buildTicket(result);
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ticket save(Ticket ticket) {
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS);) {

            statement.setString(1, ticket.getPassportNo());
            statement.setString(2, ticket.getPassengerName());
            statement.setLong(3, ticket.getFlight().getId());
            statement.setString(4, ticket.getSeatNo());
            statement.setBigDecimal(5, ticket.getCost());

            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next())
                ticket.setId(keys.getLong("id"));

            return ticket;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Long id) {
        try (var connection = open();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }


}
