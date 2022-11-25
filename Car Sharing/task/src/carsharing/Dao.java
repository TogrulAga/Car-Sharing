package carsharing;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(long id);

    List<T> getAll();

    boolean save(T t);

    void update(T t);

    void delete(int id);

    void createTable();
}