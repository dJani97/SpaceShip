/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.List;

/**
 * @author djani
 * @param <Type> Type of the DAO implementation
 */
public interface Dao <Type, PrimaryKey> {
    public List<Type> listAllRecords();
    public void createRecaord(Type t);
    public void readRecord(PrimaryKey k);
    public void updateRecord(Type t);
    public void deleteRecord(Type t);
}
