/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yazlab.pkg1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ffeki
 */
public class DbConnect {

    Connection con;
    Statement st;
    ResultSet rs;
    PreparedStatement pst;
    String username;
    int ID;

    public DbConnect() {

        try {

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false", "root", "123456");
            st = con.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int isRated(int id) {
        DbConnect db = new DbConnect();
        db.ID = id;
        String sql = "SELECT COUNT(ID) as total FROM ratings WHERE ID=" + id;
        int total=0;
        try {
            rs = db.st.executeQuery(sql);
            rs.next();
                total = rs.getInt("total");

        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return total;
    }

    int isItRated(String ISBN, int ID) {
        String sql = "SELECT * FROM ratings WHERE ISBN='" + ISBN + "' AND ID=" + ID;
        //System.out.println(sql);
        int result = 0;
        try {
            rs = st.executeQuery(sql);
            while (rs.next()) {
                result = rs.getInt("Rating");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    void Rate(String ISBN, int ID, int Rate) {
        String sql = "INSERT INTO ratings VALUES(" + ID + ", '" + ISBN + "', " + Rate + ")";
        try {
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Oy verildi.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata!");
        }
    }

    void createUser(String username, String password, int age, String location) {
        try {
            String sql = "INSERT INTO users (Username, Password, Location, Age) VALUES ('" + username + "', '" + password + "', '" + location + "', '" + age + "')";
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Kullanıcı başarıyla oluşturuldu.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata!");
        }

    }

    boolean Login(String username, String password) {
        String sql = "SELECT * from  users WHERE Username=? AND Password=?";
        boolean status = false;
        try {
            pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            rs = pst.executeQuery();
            if (rs.next()) {
                status = true;
                this.username = username;
                this.ID = rs.getInt("ID");
                
            } else {
                status = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
            //status=false;;
        }
        //System.out.println(status);
        return status;
    }

    boolean isAdmin(String username) {
        if (username.equals("ffe")) {
            return true;
        } else {
            return false;
        }
    }

    boolean isAdmin2(String username) {
        String sql = "SELECT * from  users WHERE Username=? AND Permission=1";
        boolean status = false;

        try {
            pst = con.prepareStatement(sql);

            pst.setString(1, username);

            rs = pst.executeQuery();
            // System.out.println(pst.executeQuery());
            if (rs.next()) {
                status = true;
                this.username = username;
                System.out.println(username);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
            //status=false;;
        }
        System.out.println(status);
        return status;
    }

    void UserAdd(String username, String password, int age, String location) {
        try {
            String sql = "INSERT INTO users (Username, Password, Age, Location) VALUES ('" + username + "', '" + password + "', " + age + ", '" + location + "')";
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Kullanıcı başarıyla oluşturuldu.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
            ex.printStackTrace();
            
        }
    }

    void UserUpdate(int ID, String username, String password, int age, String location) {
        try {
            String sql = "UPDATE users SET Username=?, Password=?, Age=?, Location=? WHERE ID=?";
            pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setInt(3, age);
            pst.setString(4, location);
            pst.setInt(5, ID);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Güncelleme başarılı!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
            ex.printStackTrace();
            
        }
    }

    void UserDelete(int ID) {
        try {
            String sql = "DELETE from users WHERE ID=?";
            pst = con.prepareStatement(sql);

            pst.setInt(1, ID);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Silme başarılı!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
        }
    }

    void BookAdd(String isbn, String title, String author, int year, String publisher, String imgS, String imgM, String imgL) {
        try {
            String sql = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            pst = con.prepareStatement(sql);

            pst.setString(1, isbn);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setInt(4, year);
            pst.setString(5, publisher);
            pst.setString(6, imgS);
            pst.setString(7, imgM);
            pst.setString(8, imgL);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Kitap başarıyla eklendi.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
            ex.printStackTrace();  
        }
    }

    void BookUpdate(String isbn, String title, String author, int year, String publisher, String imgS, String imgM, String imgL, String oldIsbn) {
        try {
            String sql = "UPDATE books SET ISBN=?, Title=?, Author=?, Year=?, Publisher=?, ImgS=?, ImgM=?, ImgL=? WHERE ISBN=?";
            pst = con.prepareStatement(sql);

            pst.setString(1, isbn);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setInt(4, year);
            pst.setString(5, publisher);
            pst.setString(6, imgS);
            pst.setString(7, imgM);
            pst.setString(8, imgL);
            pst.setString(9, oldIsbn);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Güncelleme başarılı!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
            ex.printStackTrace();    
        }
    }

    void BookDelete(String isbn) {
        try {
            String sql = "DELETE from books WHERE ISBN=?";
            pst = con.prepareStatement(sql);

            pst.setString(1, isbn);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Silme başarılı!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Hata! "+ex);
            ex.printStackTrace();  
        }
    }

}
