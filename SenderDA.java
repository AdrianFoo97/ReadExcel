
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class SenderDA {
    private Connection conn;
    
    public boolean save(Sender s) {
        Statement stmt = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "INSERT INTO sender(username, email, password) VALUES ('" + 
                    s.getUsername() + "', '" + s.getEmail() + "', '" + 
                    s.getPassword() + "')";
            
            stmt.executeUpdate(sql);
            stmt.close();
            return true;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    public boolean remove(String username, String email) {
        Statement stmt = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "DELETE FROM sender WHERE email='" + 
                    email + "' AND username='" + username + "'";
            
            stmt.executeUpdate(sql);
            stmt.close();
            return true;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    public ArrayList<Sender> getAllSender() {
        Statement stmt = null;
        Sender s = null;
        ArrayList<Sender> senderList = new ArrayList<>();
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM sender";
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                s = new Sender(username, email, password);
                senderList.add(s);
            }
            Collections.sort(senderList);
            rs.close();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return senderList;
    }
    
    
}
