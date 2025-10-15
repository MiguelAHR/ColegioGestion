/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class AlumnoDAO {

    // Listar los alumnos por grado
    public List<Alumno> listarPorGrado(int gradoId) {
        List<Alumno> lista = new ArrayList<>();
        String sql = "{CALL obtener_alumnos_por_grado(?)}";
        
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, gradoId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Alumno a = new Alumno();
                a.setId(rs.getInt("id"));
                a.setNombres(rs.getString("nombres"));
                a.setApellidos(rs.getString("apellidos"));
                a.setCorreo(rs.getString("correo"));
                a.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Listar todos los alumnos
    public List<Alumno> listar() {
        List<Alumno> lista = new ArrayList<>();
        String sql = "{CALL obtener_alumnos()}";
        
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Alumno a = new Alumno();
                a.setId(rs.getInt("id"));
                a.setNombres(rs.getString("nombres"));
                a.setApellidos(rs.getString("apellidos"));
                a.setCorreo(rs.getString("correo"));
                a.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Agregar un alumno
    public boolean agregar(Alumno a) {
        String sql = "{CALL crear_alumno(?, ?, ?, ?, ?)}";
        
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, a.getNombres());
            cs.setString(2, a.getApellidos());
            cs.setString(3, a.getCorreo());
            cs.setString(4, a.getFechaNacimiento());
            cs.setInt(5, a.getGradoId());
            cs.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener un alumno por ID
    public Alumno obtenerPorId(int id) {
        Alumno a = new Alumno();
        String sql = "{CALL obtener_alumno_por_id(?)}";
        
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                a.setId(rs.getInt("id"));
                a.setNombres(rs.getString("nombres"));
                a.setApellidos(rs.getString("apellidos"));
                a.setCorreo(rs.getString("correo"));
                a.setFechaNacimiento(rs.getString("fecha_nacimiento"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    // Actualizar un alumno
    public boolean actualizar(Alumno a) {
        String sql = "{CALL actualizar_alumno(?, ?, ?, ?, ?, ?)}";
        
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, a.getId());
            cs.setString(2, a.getNombres());
            cs.setString(3, a.getApellidos());
            cs.setString(4, a.getCorreo());
            cs.setString(5, a.getFechaNacimiento());
            cs.setInt(6, a.getGradoId());
            cs.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un alumno
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_alumno(?)}";
        
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
