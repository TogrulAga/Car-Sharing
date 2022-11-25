package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDbClient {
    private final String url;

    public CompanyDbClient(String url) {
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

    public Company select(String query) {
        List<Company> companies = selectForList(query);
        if (companies.size() == 1) {
            return companies.get(0);
        } else if (companies.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }

    public List<Company> selectForList(String query) {
        List<Company> companies = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
             Statement statement = con.createStatement();
             ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("ID");
                String name = resultSetItem.getString("NAME");
                Company company = new Company(id, name);
                companies.add(company);
            }

            return companies;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }
}
