package controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mysql.cj.jdbc.exceptions.SQLError;
import model.User;
import utils.Hashing;
import utils.Log;


public class UserController {

  
  
  

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"));

        // return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }



  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
            + user.getPassword()
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

    // REMEMBER!! - Check again for improvements (Will not block an uncreated user from login - check again!!!)
  public static String checkUser (User user){

      // Check for connection
      if (dbCon == null) {
          dbCon = new DatabaseController();
      }

      // Build the query for DB - can now take stored passwords which are hashed and start data which aren't hashed
      String sql1 = "SELECT * FROM cbsexam.user where email ='" + user.getEmail() + "' AND (password = '" + Hashing.sha(user.getPassword()) + "' OR password = '" + user.getPassword() + "')";

      // Actually do the query
      ResultSet rs = dbCon.query(sql1);
      User loginUser = null;

      try {
          // Get first object, since we only have one
          if (rs.next()) {

              loginUser = new User(
                      rs.getInt("id"),
                      rs.getString("first_name"),
                      rs.getString("last_name"),
                      rs.getString("password"),
                      rs.getString("email"),
                      rs.getLong("created_at"));

              String token = null;

              try {
                  // Creating and signing the token - Consider if the RSA is more secure to use as it is asymetric and has different keys
                  Algorithm algorithm = Algorithm.HMAC256("secret");
                  token = JWT.create().withIssuer("auth0").withClaim("userId", loginUser.id).sign(algorithm);
              } catch (JWTCreationException exception){
                  //Invalid Signing configuration / Couldn't convert Claims.
                  System.out.println("Something went wrong" + exception.getMessage());
              }

              // Verifying the token
/*

              try {
                  Algorithm algorithm = Algorithm.HMAC256("secret");
                  JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); //Reusable verifier instance
                  DecodedJWT jwt = verifier.verify(token);
              } catch (JWTVerificationException exception1){
                  //Invalid signature/claims

                  System.out.println("Something went wrong with verifying the token - " + exception1.getMessage());
              }
*/

              try {


                      String sql3 = "INSERT INTO tokens (user_id, token) VALUES ('" + loginUser.getId() + "', '" + token + "')";

                      dbCon.insert(sql3);

              }catch (Exception ex){


              }


              // Return the token directly
              return token;

          } else {
              System.out.println("No user found");
          }
      } catch (SQLException ex) {
          System.out.println(ex.getMessage());
      }

      // Return null
      return null;

  }

 /* public static String deleteUser (User user){

       // Check for connection
       if (dbCon == null) {
           dbCon = new DatabaseController();
       }

       String sql = "DELETE "

  }*/


}
