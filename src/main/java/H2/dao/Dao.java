
package H2.dao;

import java.sql.*;
import java.util.*;

public interface Dao<T, K> {

    T findOne(K key) throws SQLException;

    List<T> findAll() throws SQLException;
    
    void saveOrUpdate(T object) throws SQLException;

    void delete(K key) throws SQLException;
}
