package carsharing;

import java.util.List;
import java.util.Optional;

public class CarDao implements Dao<Car> {
    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS CAR" +
            "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
            "NAME VARCHAR(30) UNIQUE NOT NULL, " +
            "COMPANY_ID INT NOT NULL, " +
            "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))";
    private static final String SELECT_ALL = "SELECT * FROM CAR";
    private static final String SELECT = "SELECT * FROM CAR WHERE ID = %d";
    private static final String INSERT_DATA = "INSERT INTO CAR(NAME, COMPANY_ID) VALUES ( '%s', %d )";
    private static final String UPDATE_DATA = "UPDATE CAR SET NAME = '%s' WHERE ID = %d";
    private static final String DELETE_DATA = "DELETE FROM CAR WHERE ID = %d";

    private final CarDbClient dbClient;

    public CarDao(String url) {
        dbClient = new CarDbClient(url);
    }

    @Override
    public Optional<Car> get(long id) {
        return Optional.ofNullable(dbClient.select(String.format(SELECT, id)));
    }

    @Override
    public List<Car> getAll() {
        return dbClient.selectForList(String.format(SELECT_ALL));
    }

    public List<Car> getAllForCompanyId(int companyId) {
        List<Car> cars = getAll();

        return cars.stream().filter(c -> c.getCompanyId() == companyId).toList();
    }

    @Override
    public boolean save(Car car) {
        List<Car> cars = getAll();

        if (cars.stream().anyMatch(c -> c.getName().equals(car.getName()))) {
            return false;
        }

        return dbClient.run(String.format(INSERT_DATA, car.getName(), car.getCompanyId()));
    }

    @Override
    public void update(Car car) {
        dbClient.run(String.format(
                UPDATE_DATA, car.getName(), car.getId()));
    }

    @Override
    public void delete(int id) {
        dbClient.run(String.format(DELETE_DATA, id));
    }

    @Override
    public void createTable() {
        dbClient.run(CREATE_DB);
    }
}
