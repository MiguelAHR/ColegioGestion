/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class GradoDAO {

    // Listar todos los grados usando un Stored Procedure
    public List<Grado> listar() {
        List<Grado> lista = new ArrayList<>();
        String sql = "{CALL obtener_grados()}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Grado g = new Grado();
                g.setId(rs.getInt("id"));
                g.setNombre(rs.getString("nombre"));
                g.setNivel(rs.getString("nivel"));
                lista.add(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Agregar un grado usando un Stored Procedure
    public boolean agregar(Grado g) {
        String sql = "{CALL crear_grado(?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, g.getNombre());
            cs.setString(2, g.getNivel());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener un grado por ID usando un Stored Procedure
    public Grado obtenerPorId(int id) {
        Grado g = null;
        String sql = "{CALL obtener_grado_por_id(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                g = new Grado();
                g.setId(rs.getInt("id"));
                g.setNombre(rs.getString("nombre"));
                g.setNivel(rs.getString("nivel"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return g;
    }

    // Actualizar un grado usando un Stored Procedure
    public boolean actualizar(Grado g) {
        String sql = "{CALL actualizar_grado(?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, g.getId());
            cs.setString(2, g.getNombre());
            cs.setString(3, g.getNivel());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un grado usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_grado(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


