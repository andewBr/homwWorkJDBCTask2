package by.javaguru.je.jdbc.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    boolean update(E e) throws SQLException;
    List<E> findAll();
    Optional<E> findById(K id);
    E save(E e) throws SQLException;
    boolean delete(K id) throws SQLException;
}
