/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package org.uv.hello;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uv.crudalumnos.Alumno;
import org.uv.crudalumnos.ConexionDB;
import org.uv.crudalumnos.TransactionDB;

/**
 *
 * @author zarcorp
 */
@Named(value = "login")
@SessionScoped
public class login implements Serializable {

    private String correo;
    private String telefono;

    public String iniciar() {
        ConexionDB cx = ConexionDB.getInstance();
        Alumno p = new Alumno();
        TransactionDB tbd = new TransactionDB<Alumno>(p) {
            @Override
            public boolean execute(Connection con) {
                try ( PreparedStatement psm = con.prepareStatement("SELECT * FROM alumnos WHERE correo = ? AND telefono = ?")) {
                    psm.setString(1, correo);
                    psm.setString(2, telefono);

                    try ( ResultSet rs = psm.executeQuery()) {
                        if (rs.next()) {
                            p.setId(rs.getInt("id"));
                            p.setNombre(rs.getString("nombre"));
                            p.setDireccion(rs.getString("direccion"));
                            p.setTelefono(rs.getString("telefono"));
                            p.setCorreo(rs.getString("correo"));
                            
                            return true;
                        }  else {
                            Logger.getLogger(Alumno.class.getName()).log(Level.SEVERE, "Alumno no encontrado");
                            return false;
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Alumno.class.getName()).log(Level.SEVERE, "Error en el inicio de sesión", ex);
                    return false;
                }
            }
        };

        boolean resp = cx.execute(tbd);

        if (resp) {
            return "crud?faces-redirect=false"; // Devuelve el objeto Alumno si se encontró una coincidencia
        } else {
            return "index"; // Devuelve null si no se encontró una coincidencia o hubo un error
        }
    }
    
    

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    
    
}
