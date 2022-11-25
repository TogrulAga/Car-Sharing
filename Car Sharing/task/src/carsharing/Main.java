package carsharing;

public class Main {

    public static void main(String[] args) {
        String dbName = null;

        if (args.length == 2 && "-databaseFileName".equals(args[0])) {
            dbName = args[1];
        }

        String defaultDbName = "carsharing";

        CarSharing carSharing = new CarSharing(dbName == null ? defaultDbName : dbName);
        carSharing.run();
    }
}