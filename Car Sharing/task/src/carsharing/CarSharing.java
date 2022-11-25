package carsharing;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CarSharing {
    private final Scanner scanner = new Scanner(System.in);
    private static Dao<Company> companyDao;
    private static Dao<Car> carDao;
    private static Dao<Customer> customerDao;
    public CarSharing(String dbName) {
        String dbUrl = "jdbc:h2:./src/carsharing/db/".concat(dbName);

        customerDao = new CustomerDao(dbUrl);
        carDao = new CarDao(dbUrl);
        companyDao = new CompanyDao(dbUrl);

        companyDao.createTable();
        carDao.createTable();
        customerDao.createTable();
    }

    public void run() {
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            System.out.println("""
                            1. Log in as a manager
                            2. Log in as a customer
                            3. Create a customer
                            0. Exit""");

            String answer = scanner.nextLine();

            switch (answer) {
                case "0":
                    return;
                case "1":
                    managerMenu();
                    break;
                case "2":
                    customerMenu();
                    break;
                case "3":
                    createCustomer();
                    break;
                default:
            }
        }
    }

    private void createCustomer() {
        System.out.println("\nEnter the customer name:");
        String name = scanner.nextLine();

        if (customerDao.save(new Customer(0, name, 0))) {
            System.out.println("The customer was added!\n");
        } else {
            System.out.println("Customer already exists\n");
        }
    }

    private void customerMenu() {
        List<Customer> customers = customerDao.getAll();

        if (customers.size() == 0) {
            System.out.println("\nThe customer list is empty!\n");
            return;
        }

        System.out.println("\nChoose a customer:");

        for (int i = 1; i <= customers.size(); i++) {
            System.out.printf("%d. %s%n", i, customers.get(i - 1).getName());
        }
        System.out.println("0. Back");

        String answer = scanner.nextLine();

        if ("0".equals(answer)) {
            System.out.println();
            return;
        }
        System.out.println();

        Customer customer = customers.get(Integer.parseInt(answer) - 1);

        while (true) {
            System.out.println("""
                    1. Rent a car
                    2. Return a rented car
                    3. My rented car
                    0. Back""");
            switch (scanner.nextLine()) {
                case "1" -> rentACar(customer);
                case "2" -> returnCar(customer);
                case "3" -> printRentedCar(customer);
                case "0" -> {
                    System.out.println();
                    return;
                }
                default -> {
                }
            }
        }
    }

    private void returnCar(Customer customer) {
        Optional<Car> car = carDao.get(customer.getRentedCarId());

        if (car.isEmpty()) {
            System.out.println("\nYou didn't rent a car!\n");
            return;
        }

        customer.setRentedCarId(0);
        customerDao.update(customer);

        System.out.println("\nYou've returned a rented car!\n");
    }

    private void printRentedCar(Customer customer) {
        Optional<Car> car = carDao.get(customer.getRentedCarId());

        if (car.isEmpty()) {
            System.out.println("\nYou didn't rent a car!\n");
            return;
        }

        Optional<Company> company = companyDao.get(car.get().getCompanyId());

        System.out.println("\nYour rented car:");
        System.out.println(car.get().getName());
        System.out.println("Company:");
        System.out.println(company.isPresent() ? company.get().getName() : "Unknown");
        System.out.println();
    }

    private void rentACar(Customer customer) {
        Optional<Car> car = carDao.get(customer.getRentedCarId());

        if (car.isPresent()) {
            System.out.println("\nYou've already rented a car!\n");
            return;
        }

        List<Company> companies = companyDao.getAll();

        if (companies.isEmpty()) {
            System.out.println("\nThe company list is empty!\n");
            return;
        }

        System.out.println("\nChoose a company:");
        for (int i = 1; i <= companies.size(); i++) {
            System.out.printf("%d. %s%n", i, companies.get(i - 1).getName());
        }
        System.out.println("0. Back");

        String companyId = scanner.nextLine();

        if ("0".equals(companyId)) {
            System.out.println();
            return;
        }

        List<Car> cars = ((CarDao) carDao).getAllForCompanyId(companies.get(Integer.parseInt(companyId) - 1).getId());

        if (cars.isEmpty()) {
            System.out.printf("%nNo available cars in the '%s' company%n%n", companies.get(Integer.parseInt(companyId) - 1).getName());
            carDao.getAll().forEach(x -> System.out.println(x.getCompanyId()));
            return;
        }

        List<Integer> rentedCarIds = customerDao.getAll().stream().map(Customer::getRentedCarId).toList();

        System.out.println("\nChoose a car:");
        for (int i = 1, id = 1; i <= cars.size(); i++) {
            Car carToRent = cars.get(i - 1);
            if (!rentedCarIds.contains(carToRent.getId())) {
                System.out.printf("%d. %s%n", id, cars.get(i - 1).getName());
                id++;
            }
        }
        System.out.println("0. Back");

        String carId = scanner.nextLine();

        if ("0".equals(carId)) {
            System.out.println();
            return;
        }

        Car selectedCar = cars.get(Integer.parseInt(carId) - 1);
        customer.setRentedCarId(selectedCar.getId());
        customerDao.update(customer);

        System.out.printf("%nYou rented '%s'%n%n", selectedCar.getName());
    }

    private void managerMenu() {
        while (true) {
            System.out.println("""
                    1. Company list
                    2. Create a company
                    0. Back""");

            String answer = scanner.nextLine();

            switch (answer) {
                case "0":
                    return;
                case "1":
                    printCompanyList();
                    break;
                case "2":
                    createCompany();
                    break;
                default:
            }
        }
    }

    private void printCompanyList() {
        System.out.println();

        List<Company> companies = companyDao.getAll();

        if (companies.size() == 0) {
            System.out.println("The company list is empty!\n");
            return;
        }

        for (int i = 1; i <= companies.size(); i++) {
            System.out.printf("%d. %s%n", i, companies.get(i - 1).getName());
        }
        System.out.println("0. Back");

        while (true) {
            String answer = scanner.nextLine();

            if ("0".equals(answer)) {
                System.out.println();
                return;
            }

            Company company = companies.get(Integer.parseInt(answer) - 1);
            
            if (company != null) {
                companyMenu(company);
                break;
            } else {
                System.out.println("Wrong company ID");
            }
        }

        System.out.println();
    }

    private void companyMenu(Company company) {
        System.out.printf("%n'%s' company%n", company.getName());

        while (true) {
            System.out.println("""
                1. Car list
                2. Create a car
                0. Back""");
            
            switch (scanner.nextLine()) {
                case "1":
                    printCarList(company);
                    break;
                case "2":
                    createCar(company);
                    break;
                case "0":
                    return;
                default:
            }
        }
    }

    private void createCar(Company company) {
        System.out.println("\nEnter the car name:");
        String name = scanner.nextLine();

        if (carDao.save(new Car(0, name, company.getId()))) {
            System.out.println("The car was added!\n");
        } else {
            System.out.println("Car already exists\n");
        }
    }

    private void printCarList(Company company) {
        List<Car> cars = ((CarDao) carDao).getAllForCompanyId(company.getId());

        if (cars.size() == 0) {
            System.out.println("\nThe car list is empty!\n");
            return;
        }

        System.out.println("\nCar list:");

        for (int i = 1; i <= cars.size(); i++) {
            System.out.printf("%d. %s%n", i, cars.get(i - 1).getName());
        }

        System.out.println();
    }

    private void createCompany() {
        System.out.println("\nEnter the company name:");
        String name = scanner.nextLine();

        boolean result = companyDao.save(new Company(0, name));
        if (result) {
            System.out.println("The company was created!\n");
        } else {
            System.out.println("Company already exists.\n");
        }
    }
}
