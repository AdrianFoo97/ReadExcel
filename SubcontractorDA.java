
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
public class SubcontractorDA {
    private Connection conn;
    
    public boolean save(Subcontractor s) {
        Statement stmt = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "INSERT INTO subcontractor(name, email) VALUES ('" + 
                    s.getName() + "', '" + s.getEmail() + "')";
            
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
    
    public boolean remove(String name, String email) {
        Statement stmt = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "DELETE FROM Subcontractor WHERE name='" + 
                    name + "' AND email='" + email + "'";
            
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
    
    public Subcontractor getSubcontractor(String name) {
        Statement stmt = null;
        Subcontractor s = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT name, email FROM subcontractor WHERE name='" + 
                    name + "'";
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                String subconName = rs.getString("name");
                String email = rs.getString("email");
                
                s = new Subcontractor(subconName, email);
            }
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
        return s;
    }
    
    //function to retrieve all the subcontractor
    public ArrayList<Subcontractor> getAllSubcontractor() {
        Statement stmt = null;
        Subcontractor s = null;
        ArrayList<Subcontractor> conList = new ArrayList<>();
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM subcontractor";
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                String subconName = rs.getString("name");
                String email = rs.getString("email");
                s = new Subcontractor(subconName, email);
                conList.add(s);
            }
            Collections.sort(conList);
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
        return conList;
    }
    
    public boolean setSubcontractor(String email, String name, String updatedEmail, 
            String updatedName) {
        Statement stmt = null;
        Subcontractor s = null;
        try {
            conn = MySQLDBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "UPDATE subcontractor SET name='" + 
                    updatedName + "', email='" + updatedEmail + "' WHERE email='" +
                    email + "' AND name='" + name + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            return true;
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
        return false;
    }
}
