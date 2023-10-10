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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.uv.crudalumnos.Alumno;
import org.uv.crudalumnos.ConexionDB;
import org.uv.crudalumnos.TransactionDB;

/**
 *
 * @author zarcorp
 */
@Named(value = "daoAlumno")
@ViewScoped
public class DaoAlumno implements Serializable {

    private Alumno alumno = new Alumno();
    private List<Alumno> alumnosL = findAll();
    private Alumno selectedAlumno;

    public Alumno getSelectedAlumno() {
        return selectedAlumno;
    }

    public void setSelectedAlumno(Alumno selectedAlumno) {
        this.selectedAlumno = selectedAlumno;
    }
   

    public List<Alumno> getAlumnosL() {
        return alumnosL;
    }

    public void setAlumnosL(List<Alumno> alumnos) {
        this.alumnosL = alumnos;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String create() {
        Alumno p = this.alumno;
        ConexionDB cx = ConexionDB.getInstance();
        TransactionDB tbd = new TransactionDB<Alumno>(p) {
            @Override
            public boolean execute(Connection con) {
                try ( PreparedStatement psm = con.prepareStatement("INSERT INTO alumnos(id, nombre, direccion, telefono, correo) VALUES (?, ?, ?, ?, ?)")) {
                    psm.setInt(1, p.getId());
                    psm.setString(2, p.getNombre());
                    psm.setString(3, p.getDireccion());
                    psm.setString(4, p.getTelefono());
                    psm.setString(5, p.getCorreo());
                    psm.execute();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Guardado Correctamente"));
                    alumno.setId(0);
                    alumno.setNombre(null);
                    alumno.setDireccion(null);
                    alumno.setTelefono(null);
                    alumno.setCorreo(null);
                    return true;
                } catch (SQLException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso", "No se pudo guardar"));
                    return false;
                }
            }
        };
        cx.execute(tbd);

        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return viewId + "?faces-redirect=true";
    }
    
    public String delete(final int alumnoId) {
    ConexionDB cx = ConexionDB.getInstance();
    TransactionDB tbd = new TransactionDB<Alumno>(null) {
        @Override
        public boolean execute(Connection con) {
            try (PreparedStatement psm = con.prepareStatement("DELETE FROM alumnos WHERE id = ?")) {
                psm.setInt(1, alumnoId); // Establecer el ID del alumno a eliminar
                int rowsAffected = psm.executeUpdate();
                if (rowsAffected > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Alumno eliminado correctamente"));
                    return true;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No se encontró el alumno con ID: " + alumnoId));
                    return false;
                }
            } catch (SQLException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "No se pudo eliminar el alumno"));
                return false;
            }
        }
    };
    cx.execute(tbd);

    // Actualiza la lista de alumnos después de eliminar
    alumnosL = findAll();

    String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
    return viewId + "?faces-redirect=true";
}
    public String update(final Alumno alumno) {
    ConexionDB cx = ConexionDB.getInstance();
    TransactionDB tbd = new TransactionDB<Alumno>(alumno) {
        @Override
        public boolean execute(Connection con) {
            try (PreparedStatement psm = con.prepareStatement("UPDATE alumnos SET nombre = ?, direccion = ?, telefono = ?, correo = ? WHERE id = ?")) {
                psm.setString(1, alumno.getNombre());
                psm.setString(2, alumno.getDireccion());
                psm.setString(3, alumno.getTelefono());
                psm.setString(4, alumno.getCorreo());
                psm.setInt(5, alumno.getId());

                int rowsAffected = psm.executeUpdate();
                if (rowsAffected > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Alumno actualizado correctamente"));
                    return true;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No se encontró el alumno con ID: " + alumno.getId()));
                    return false;
                }
            } catch (SQLException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "No se pudo actualizar el alumno"));
                return false;
            }
        }
    };
    cx.execute(tbd);

    // Actualiza la lista de alumnos después de la actualización
    alumnosL = findAll();

    String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
    return viewId + "?faces-redirect=true";
}




    
    public List<Alumno> findAll() {
        final List<Alumno> alumnos = new ArrayList<>();
        ConexionDB cx = ConexionDB.getInstance();
        TransactionDB tbd = new TransactionDB<List<Alumno>>(alumnos) {
            @Override
            public boolean execute(Connection con) {
                try ( PreparedStatement psm = con.prepareStatement("SELECT * FROM alumnos");  ResultSet rs = psm.executeQuery()) {

                    while (rs.next()) {
                        Alumno alumno = new Alumno();
                        alumno.setId(rs.getInt("id"));
                        alumno.setNombre(rs.getString("nombre"));
                        alumno.setDireccion(rs.getString("direccion"));
                        alumno.setTelefono(rs.getString("telefono"));
                        alumno.setCorreo(rs.getString("correo"));
                        alumnos.add(alumno);
                    }
                    return true;
                } catch (SQLException ex) {
                    return false;
                } catch (NullPointerException ex) {
                    return false;
                }
            }
        };
        boolean resp = cx.execute(tbd);
        if (resp) {
            return alumnos;
        } else {
            return null;
        }
    }

}
