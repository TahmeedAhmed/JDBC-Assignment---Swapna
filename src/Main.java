import javax.sound.midi.SysexMessage;
import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner intScanner = new Scanner(System.in);
    static Scanner stringScanner = new Scanner(System.in);
    static Scanner doubleScanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Connection conn = getConnection();
        System.out.println("\nMENU");
        System.out.println("1. Insert New Book\n2. Update A Book\n3. Delete A Book\n4. Display All Books\n5. Search A Book By ID\n6. Exit\n\nEnter an option: ");
        Scanner scanner = new Scanner(System.in);
        int ans = 1;
        while (ans >=1 && ans <=6 ){
            ans = scanner.nextInt();
            if (ans == 1) insertBook(conn);
            else if (ans == 2) updateBook(conn);
            else if (ans == 3) deleteBook(conn);
            else if (ans == 4) displayAllBooks(conn);
            else if (ans == 5) displayABookById(conn);
            else {
                System.out.println("Exiting");
                conn.close();
                System.exit(0);
            }

            System.out.println("MENU");
            System.out.println("1. Insert New Book\n2. Update A Book\n3. Delete A Book\n4. Display All Books\n5. Search A Book By ID\n6. Exit\n\nEnter an option: ");
        }
    }

    public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:ebookshop.db");
            System.out.println("Connected to DB ...\n");

        } catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }


    public static boolean insertBook(Connection conn) throws SQLException {
        int id = inputInt("Enter unique id of book: ");

        try {
            checkIfBookExistsByID(conn, id);
        }catch (InvalidBookIdException e){

            String title = inputString("Enter title of book: ");
            String author = inputString("Enter author of book: ");
            double price = inputDouble("Enter price of book: ");
            int quantity = inputInt("Enter quantity of book: ");

            String sqlStmt = "INSERT INTO books(id,title,author,price,quantity) VALUES(?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sqlStmt);
            pstmt.setInt(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setDouble(4, price);
            pstmt.setInt(5, quantity);

            pstmt.execute();
            return true;

        }catch (Exception e){
            System.out.println("Error in supplied data");
            return false;
        }
        System.out.println("Id already exists !");
        return false;

    }

    public static boolean updateBook(Connection conn){
        int id = inputInt("Enter id of a book to change: ");
        double price = inputDouble("Enter new price of book: ");
        int quantity = inputInt("Enter new quantity of book: ");

        String sqlStmt = "UPDATE books SET price = ?, quantity = ? WHERE id = ?";

        try {
            checkIfBookExistsByID(conn, id);
            PreparedStatement pstmt = conn.prepareStatement(sqlStmt);
            pstmt.setDouble(1, price);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, id);
            pstmt.execute();
            return true;

        }catch (InvalidBookIdException e){
            System.out.println(e.getMessage());
            return false;
        }catch (Exception e){
            System.out.println("Error");
            return false;
        }
    }

    public static boolean deleteBook(Connection conn){
        int id = inputInt("Enter id of a book to DELETE: ");
        String sqlStmt = "DELETE FROM books WHERE id = ?";

        try {
            checkIfBookExistsByID(conn, id);
            PreparedStatement pstmt = conn.prepareStatement(sqlStmt);
            pstmt.setInt(1, id);

            pstmt.execute();
            return true;

        }catch (InvalidBookIdException e){
            System.out.println(e.getMessage());
            return false;
        }catch (Exception e){
            System.out.println("Error");
            return false;
        }
    }

    public static void displayAllBooks(Connection conn){
        String query = "SELECT * FROM books";

        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                System.out.println(
                        "ID: " + resultSet.getInt("id") +  "\t" +
                        "TITLE: " + resultSet.getString("title") + "\t" +
                        "AUTHOR: " + resultSet.getString("author") + "\t" +
                        "PRICE: " + resultSet.getDouble("price") + "\t" +
                        "QUANTITY: " + resultSet.getInt("quantity")
                );
            }

        }catch (Exception e){
            System.out.println("Error occurred");
        }
    }

    public static void displayABookById(Connection conn){
        int id = inputInt("Enter ID of a book to view: ");
        String query = "SELECT * FROM books WHERE id = " + id;

        try {
            checkIfBookExistsByID(conn, id);
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            System.out.println(
                    "ID: " + resultSet.getInt("id") +  "\t" +
                            "TITLE: " + resultSet.getString("title") + "\t" +
                            "AUTHOR: " + resultSet.getString("author") + "\t" +
                            "PRICE: " + resultSet.getDouble("price") + "\t" +
                            "QUANTITY: " + resultSet.getInt("quantity")
            );

        }catch (InvalidBookIdException e){
            System.out.println(e.getMessage());
            return;
        }catch (Exception e){
            System.out.println("Error occurred from here");
        }
    }


    public static boolean checkIfBookExistsByID(Connection conn, int id) throws InvalidBookIdException, SQLException {
        String query = "SELECT COUNT(*) AS count FROM books WHERE id = " + id;

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);
        resultSet.next();
        int count = resultSet.getInt("count");
        resultSet.close();

        if (count == 1) return true;
        else throw new InvalidBookIdException("No books exists with given id");
    }

    public static String inputString(String message){
        System.out.println(message);
        return stringScanner.nextLine();
    }
    public static int inputInt(String message){
        System.out.println(message);
        return intScanner.nextInt();
    }
    public static double inputDouble(String message){
        System.out.println(message);
        return doubleScanner.nextDouble();
    }
}
