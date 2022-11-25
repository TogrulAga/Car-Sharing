package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDbClient {
    private final String url;

    public CarDbClient(String url) {
        this.url = url;

        try {
            Class.forName ("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean run(String str) {
        try (Connection con = DriverManager.getConnection(url); // Statement creation
             Statement statement = con.createStatement()
        ) {
            statement.executeUpdate(str);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Car select(String query) {
        List<Car> cars = selectForList(query);
        if (cars.size() == 1) {
            return cars.get(0);
        } else if (cars.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }

    public List<Car> selectForList(String query) {
        List<Car> cars = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
             Statement statement = con.createStatement();
             ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("ID");
                String name = resultSetItem.getString("NAME");
                int companyId = resultSetItem.getInt("COMPANY_ID");
                Car car = new Car(id, name, companyId);
                cars.add(car);
            }

            return cars;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }
}
