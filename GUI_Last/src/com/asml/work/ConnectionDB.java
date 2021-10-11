package com.asml.work;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectionDB {
	
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./database/db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "";
    private Connection connection;
    private Statement stmt;
    
    private static final String CREATE_TABLE = "CREATE TABLE asml_file"
            + "(id VARCHAR(255),"
            + " folder_directory VARCHAR(255))";
    
    private static final String DELETE_TABLE = "DROP TABLE asml_screenshot" ;
    
    private static final String INSERT_TABLE = "INSERT INTO asml_file VALUES('1',' ')";
                
    

    public Connection openConnection(){
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "asml_file", null);
            stmt = connection.createStatement();
            if (tables.next()) {
            	System.out.println("Table is already there");
               
            }
            else {
            	
                stmt.execute(CREATE_TABLE);
                stmt.executeUpdate(INSERT_TABLE);
              
            }
            
            return connection;
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(null,"Database_Connection ERROR\n" + ex );
        	return null;
        }
    }
}

