
package data_Base;
import java.sql.*;
import java.io.IOException;
import java.util.UUID;


public class MYSQLConnector {
    PreparedStatement pst;
    ResultSet rs;
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost/TomRater", "root", "");
                System.out.println("Connection Successful");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return con;
    }
    public static void main(String[] args) {
        getConnection();
    }

    //function to create user

    public void create_User(String name, String email, String username, String password) throws IOException {

        String query2 = "INSERT into user (id,name, email, uname, password) values (?,?,?,?,?)";

        try {
            pst = getConnection().prepareStatement(query2);
            pst.setString(1, String.valueOf(UUID.randomUUID()));
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, username);
            pst.setString(5, password);


            if (isExist(email)) {
                //if user already exists
                System.out.println("User already exist");
            } else {

                if (pst.executeUpdate() != 0) {
                    System.out.println("Registration Successful, please sign in");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    // function to check if user exist
    public boolean isExist(String email) throws IOException {
        String query = "SELECT * FROM `user` WHERE email = ?";
        boolean user_exist = false;
        try {
            pst = getConnection().prepareStatement(query);
            pst.setString(1, email);


            rs = pst.executeQuery();
            if (rs.next()) {
                user_exist = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user_exist;
    }
}

    //check user method for Login
    public void checkUser(String username, String password) throws IOException {
        String query = "SELECT * FROM `user` WHERE username = ? AND password = ?";
        try {
            pst = getConnection().prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            rs = pst.executeQuery();
            if (rs.next()) {
                //if login successful show a homepage
            }  else {
                // if login unsuccessful show error message
                System.out.println("Incorrect username or password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    // function to check if the fields are empty and password matches with confirm password
    public boolean checkFields(String name, String email, String uname, String password, String confirmPassword){
        if(name.isEmpty() || email.isEmpty() || uname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){

            System.out.println("One or more fields are empty!");
            return false;

        }
        else if(!password.equals(confirmPassword)){
            System.out.println("Password does not match!");
            return false;
        } else {
            return true;
        }

    }

}

