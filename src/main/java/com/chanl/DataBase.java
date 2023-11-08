package com.chanl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DataBase {
    private String NAME;
    private String PASSWORD;
    private String USER;
    private String PORT;
    private String HOST;
    private String URL;
    private static Connection connection;

    private final String PATH = "src/main/resources/db.properties";

    /** Проверка соединения
     * @return true/false
     */
    private boolean isExistConnection(){
        try{
            if(!connection.isValid(10)){
                return false;
            }
        }
        catch(Exception sex){
            return false;
        }
        return true;
    }

    /** Чтение параметров
     * @return DataBase
     */
    public DataBase readProp(){
        File file = new File(PATH);
        Properties prop = new Properties();
        try{
            prop.load(new FileReader(file));
        }
        catch(IOException ioex){
            System.out.println(ioex.getMessage());
            System.exit(1);
        }
        NAME = prop.getProperty("name");
        PASSWORD = prop.getProperty("password");
        USER = prop.getProperty("user");
        PORT = prop.getProperty("port");
        HOST = prop.getProperty("host");
        URL = String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, NAME);
        return this;
    }

    /** Соединение с СУБД
     * 
     */
    public void getConn(){
        if(isExistConnection()){
            return;
        }
        try{
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch(SQLException sex){
            System.out.println(sex.getMessage());
            System.exit(1);
        }
    }

    /** Закрыть соединение
     * 
     */
    public void closeConnection(){
        try{
            connection.close();
        }
        catch(SQLException sex){
            System.out.println(sex.getMessage());
            System.exit(1);
        }
    }

    /** Select
     * @param args - поля для селекта
     * @param table - таблица
     * @param condition - блок условия с where
     * @return Результат ключ-значение, где ключ = args
     */
    public synchronized String[][] querySelect(String[] args, String table, String condition){
        String[][] tableStruct = null;
        String query = "select ";
        for(int i = 0; i < args.length; i++){
            query += (i < args.length - 1 ? String.format("%s, ", args[i]) : args[i]);
        }
        query += String.format(" from %s %s;", table, condition);
        try{
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery(query);
            result.last();
            int cntRows = result.getRow();
            if(cntRows > 0){
                tableStruct = new String[cntRows][args.length];
                result.first();
                while(result.next()){
                    for(int numCol = 0; numCol < args.length; numCol++){
                        tableStruct[result.getRow() - 1][numCol] = result.getString(numCol + 1);
                    }
                }
            }
            result.close();
            state.close();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            closeConnection();
            System.exit(1);
        }
        return tableStruct == null ? new String[0][0] : tableStruct;
    }

    /** Update/Insert/Delete
     * @param query - полный запрос
     */
    public synchronized void queryUpdInsDel(String query){
        if(query.isEmpty()){
            return;
        }
        try{
            PreparedStatement state = connection.prepareStatement(query);
            state.executeUpdate();
            state.close();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            closeConnection();
            System.exit(1);
        }
    }
}
