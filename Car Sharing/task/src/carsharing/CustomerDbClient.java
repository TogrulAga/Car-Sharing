package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDbClient {
    private final String url;

    public CustomerDbClient(String url) {
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

    public Customer select(String query) {
        List<Customer> customers = selectForList(query);
        if (customers.size() == 1) {
            return customers.get(0);
        } else if (customers.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }

    public List<Customer> selectForList(String query) {
        List<Customer> customers = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
             Statement statement = con.createStatement();
             ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("ID");
                String name = resultSetItem.getString("NAME");
                int rentedCarId = resultSetItem.getInt("RENTED_CAR_ID");
                Customer customer = new Customer(id, name, rentedCarId);
                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }
}
