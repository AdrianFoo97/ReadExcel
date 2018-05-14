
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */

public class MySQLDBConnection {
    private static MySQLDBConnection instance = new MySQLDBConnection();
    private static String DB_URL = "jdbc:mysql://localhost/bis?userSSL=false&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "1234";
    
    private Connection createConnection() {
        Connection conn = null;
        try {
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } 
        catch (SQLException se) {
            se.printStackTrace();
        }
        return conn;
    }
    
    public static Connection getConnection() {
        return instance.createConnection();
    }
}


