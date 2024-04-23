package by.javaguru.je.jdbc.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Ticket {

    private Long id;
    private String passportNo;
    private String passengerName;
    private Flight flight;
    private String seatNo;
    private BigDecimal cost;

    public Ticket() {
    }

    public Ticket(Long id, String passportNo, String passengerName, Flight flightId, String seatNo, BigDecimal cost) {
        this.id = id;
        this.passportNo = passportNo;
        this.passengerName = passengerName;
        this.flight = flightId;
        this.seatNo = seatNo;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Ticket{" +
               "id=" + id +
               ", passportNo='" + passportNo + '\'' +
               ", passengerName='" + passengerName + '\'' +
               ", flightNo=" + flight +
               ", seatNo='" + seatNo + '\'' +
               ", cost=" + cost +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return flight == ticket.flight && Objects.equals(id, ticket.id) && Objects.equals(passportNo, ticket.passportNo) && Objects.equals(passengerName, ticket.passengerName) && Objects.equals(seatNo, ticket.seatNo) && Objects.equals(cost, ticket.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, passportNo, passengerName, flight, seatNo, cost);
    }
}
