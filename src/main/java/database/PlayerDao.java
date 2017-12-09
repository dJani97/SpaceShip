/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Player;

/**
 *
 * @author djani
 */
public class PlayerDao implements Dao<Player, String> {

    String tableName = "PLAYERS";
    Connection connection;

    public PlayerDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Player> listAllRecords() {

        List<Player> people = new ArrayList<>();
        String sqlStatement = "SELECT * FROM " + tableName;

        if (connection != null) {
            try (
                    Statement s = connection.createStatement();
                    ResultSet resultSet = s.executeQuery(sqlStatement)) {

                String name;
                int score;

                while (resultSet.next()) {
                    name = resultSet.getString("name");
                    score = resultSet.getInt("score");

                    people.add(new Player(name, score));
                }

            } catch (Exception ex) {
                Logger.getLogger(PlayerDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return people;
    }

    @Override
    public void createRecaord(Player t) {
        String sqlStatement = "INSERT INTO " + tableName + " VALUES(?, ?)";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement)) {
            prepStmt.setString(1, t.getName());
            prepStmt.setInt(2, t.getScore());
            prepStmt.executeUpdate();

        } catch (Exception ex) {
            Logger.getLogger(PlayerDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void readRecord(String k) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRecord(Player t) {
        String sqlStatement = "UPDATE " + tableName + " SET score = ? where name = ?";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement)) {
            prepStmt.setInt(1, t.getScore());
            prepStmt.setString(2, t.getName());
            prepStmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(PlayerDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteRecord(Player t) {
        String sqlStatement = "DELETE FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement);) {
            prepStmt.setString(1, t.getName());
            prepStmt.executeUpdate();

        } catch (Exception ex) {
            Logger.getLogger(PlayerDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
