/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.File;
import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO {

    // Guarda una nueva imagen
    public boolean guardarImagen(int alumnoId, String ruta) {
        String sql = "INSERT INTO imagenes (alumno_id, ruta) VALUES (?, ?)";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.setString(2, ruta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Recupera todas las imágenes de un alumno
    public List<Imagen> listarPorAlumno(int alumnoId) {
        List<Imagen> lista = new ArrayList<>();
        String sql = "SELECT id, alumno_id, ruta, fecha_subida FROM imagenes WHERE alumno_id = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Imagen img = new Imagen();
                img.setId(rs.getInt("id"));
                img.setAlumnoId(rs.getInt("alumno_id"));
                img.setRuta(rs.getString("ruta"));
                img.setFechaSubida(rs.getTimestamp("fecha_subida"));
                lista.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean eliminarImagen(int id, String contextPath) {
        String sqlSelect = "SELECT ruta FROM imagenes WHERE id = ?";
        String sqlDelete = "DELETE FROM imagenes WHERE id = ?";
        String ruta = null;
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps1 = con.prepareStatement(sqlSelect)) {

            ps1.setInt(1, id);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) return false;
            ruta = rs.getString("ruta");

            // 1) Primero eliminar registro BD
            try (PreparedStatement ps2 = con.prepareStatement(sqlDelete)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }

            // 2) Luego borrar el fichero
            // ruta almacena algo como "uploads/imagen123.jpg"
            File f = new File(contextPath, ruta);
            if (f.exists()) {
                f.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

