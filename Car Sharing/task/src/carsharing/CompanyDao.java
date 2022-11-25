package carsharing;

import java.util.List;
import java.util.Optional;

public class CompanyDao implements Dao<Company> {
    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS COMPANY" +
            "(ID INT PRIMARY KEY AUTO_INCREMENT, " +
            "NAME VARCHAR(30) UNIQUE NOT NULL)";
    private static final String SELECT_ALL = "SELECT * FROM COMPANY";
    private static final String SELECT = "SELECT * FROM COMPANY WHERE ID = %d";
    private static final String INSERT_DATA = "INSERT INTO COMPANY(NAME) VALUES ( '%s' )";
    private static final String UPDATE_DATA = "UPDATE COMPANY SET NAME = '%s' WHERE ID = %d";
    private static final String DELETE_DATA = "DELETE FROM COMPANY WHERE ID = %d";

    private final CompanyDbClient dbClient;

    public CompanyDao(String url) {
        dbClient = new CompanyDbClient(url);
    }

    @Override
    public Optional<Company> get(long id) {
        return Optional.ofNullable(dbClient.select(String.format(SELECT, id)));
    }

    @Override
    public List<Company> getAll() {
        return dbClient.selectForList(String.format(SELECT_ALL));
    }

    @Override
    public boolean save(Company company) {
        List<Company> companies = getAll();

        if (companies.stream().anyMatch(c -> c.getName().equals(company.getName()))) {
            return false;
        }

        return dbClient.run(String.format(INSERT_DATA, company.getName()));
    }

    @Override
    public void update(Company company) {
        dbClient.run(String.format(
                UPDATE_DATA, company.getName(), company.getId()));
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
