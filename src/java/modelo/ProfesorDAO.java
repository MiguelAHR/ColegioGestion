/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class ProfesorDAO {

    // Obtener un profesor por username usando un Stored Procedure
// En ProfesorDAO.java - método obtenerPorUsername
    public Profesor obtenerPorUsername(String username) {
        String sql = "{CALL obtener_profesor_por_username(?)}";
        System.out.println("🔍 [ProfesorDAO] Ejecutando: " + sql + " con username: " + username);

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, username);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                Profesor p = new Profesor();
                p.setId(rs.getInt("id"));
                p.setNombres(rs.getString("nombres"));
                p.setApellidos(rs.getString("apellidos"));
                p.setCorreo(rs.getString("correo"));
                p.setEspecialidad(rs.getString("especialidad"));
                System.out.println("✅ [ProfesorDAO] Profesor encontrado: " + p.getNombres() + " " + p.getApellidos() + " (ID: " + p.getId() + ")");
                return p;
            } else {
                System.out.println("❌ [ProfesorDAO] No se encontró profesor para username: " + username);
            }
        } catch (Exception e) {
            System.out.println("💥 [ProfesorDAO] Error al buscar profesor: " + username);
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos los profesores usando un Stored Procedure
    public List<Profesor> listar() {
        List<Profesor> lista = new ArrayList<>();
        String sql = "{CALL obtener_profesores()}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Profesor p = new Profesor();
                p.setId(rs.getInt("id"));
                p.setNombres(rs.getString("nombres"));
                p.setApellidos(rs.getString("apellidos"));
                p.setCorreo(rs.getString("correo"));
                p.setEspecialidad(rs.getString("especialidad"));
                lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Agregar un profesor usando un Stored Procedure
    public boolean agregar(Profesor p) {
        String sql = "{CALL crear_profesor(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, p.getNombres());
            cs.setString(2, p.getApellidos());
            cs.setString(3, p.getCorreo());
            cs.setString(4, p.getEspecialidad());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener un profesor por ID usando un Stored Procedure
    public Profesor obtenerPorId(int id) {
        Profesor p = null;
        String sql = "{CALL obtener_profesor_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                p = new Profesor();
                p.setId(rs.getInt("id"));
                p.setNombres(rs.getString("nombres"));
                p.setApellidos(rs.getString("apellidos"));
                p.setCorreo(rs.getString("correo"));
                p.setEspecialidad(rs.getString("especialidad"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }

    // Actualizar un profesor usando un Stored Procedure
    public boolean actualizar(Profesor p) {
        String sql = "{CALL actualizar_profesor(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, p.getId());
            cs.setString(2, p.getNombres());
            cs.setString(3, p.getApellidos());
            cs.setString(4, p.getCorreo());
            cs.setString(5, p.getEspecialidad());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un profesor usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_profesor(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
