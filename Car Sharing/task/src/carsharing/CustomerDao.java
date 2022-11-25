package carsharing;

import java.util.List;
import java.util.Optional;

public class CustomerDao implements Dao<Customer> {
    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
            "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
            "NAME VARCHAR(30) UNIQUE NOT NULL, " +
            "RENTED_CAR_ID INT, " +
            "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))";
    private static final String SELECT_ALL = "SELECT * FROM CUSTOMER";
    private static final String SELECT = "SELECT * FROM CUSTOMER WHERE ID = %d";
    private static final String INSERT_DATA = "INSERT INTO CUSTOMER(NAME, RENTED_CAR_ID) VALUES ( '%s', %s )";
    private static final String UPDATE_DATA = "UPDATE CUSTOMER SET RENTED_CAR_ID = %s WHERE ID = %d";
    private static final String DELETE_DATA = "DELETE FROM CUSTOMER WHERE ID = %d";

    private final CustomerDbClient dbClient;

    public CustomerDao(String url) {
        dbClient = new CustomerDbClient(url);
    }

    @Override
    public Optional<Customer> get(long id) {
        return Optional.ofNullable(dbClient.select(String.format(SELECT, id)));
    }

    @Override
    public List<Customer> getAll() {
        return dbClient.selectForList(String.format(SELECT_ALL));
    }

    @Override
    public boolean save(Customer customer) {
        List<Customer> customers = getAll();

        if (customers.stream().anyMatch(c -> c.getName().equals(customer.getName()))) {
            return false;
        }

        int rentedCarId = customer.getRentedCarId();
        return dbClient.run(String.format(INSERT_DATA, customer.getName(), rentedCarId != 0 ? String.valueOf(rentedCarId) : "NULL"));
    }

    @Override
    public void update(Customer customer) {
        String rentedCarId = customer.getRentedCarId() == 0 ? "NULL" : String.valueOf(customer.getRentedCarId());
        dbClient.run(String.format(
                UPDATE_DATA, rentedCarId, customer.getId()));
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
