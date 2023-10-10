/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.uv.crudalumnos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author zarcorp
 */
public class ConexionDB {

    private static ConexionDB cx = null;
    private DataSource dataSource;

    private ConexionDB() {
        try {
            javax.naming.Context ctx = new javax.naming.InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/CrudAlumnos");
        } catch (javax.naming.NamingException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener la instancia del Singleton
    public static synchronized ConexionDB getInstance() {
        if (cx == null) {
            cx = new ConexionDB();
        }
        return cx;
    }

    // Método para obtener una conexión del DataSource
    public Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            throw new SQLException("El DataSource no está configurado.");
        }
    }

    public boolean execute() {
        try ( Connection con = getConnection()) {
            // Ejecuta tus operaciones aquí
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean execute(TransactionDB tbd) {
        try ( Connection con = getConnection()) {
            return tbd.execute(con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
