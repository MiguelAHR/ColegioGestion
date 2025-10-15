/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class TareaDAO {

    // Agregar una tarea usando un Stored Procedure
    public boolean agregar(Tarea t) {
        String sql = "{CALL crear_tarea(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, t.getNombre());
            cs.setString(2, t.getDescripcion());
            cs.setString(3, t.getFechaEntrega());
            cs.setBoolean(4, t.isActivo());
            cs.setInt(5, t.getCursoId());

            return cs.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error al agregar tarea");
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar una tarea usando un Stored Procedure
    public boolean actualizar(Tarea t) {
        String sql = "{CALL actualizar_tarea(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, t.getId());
            cs.setString(2, t.getNombre());
            cs.setString(3, t.getDescripcion());
            cs.setString(4, t.getFechaEntrega());
            cs.setBoolean(5, t.isActivo());
            cs.setInt(6, t.getCursoId());

            return cs.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar tarea");
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una tarea usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_tarea(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar tarea");
            e.printStackTrace();
            return false;
        }
    }

    // Obtener una tarea por ID usando un Stored Procedure
    public Tarea obtenerPorId(int id) {
        Tarea t = null;
        String sql = "{CALL obtener_tarea_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                t = new Tarea();
                t.setId(rs.getInt("id"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setFechaEntrega(rs.getString("fecha_entrega"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCursoId(rs.getInt("curso_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    // Listar tareas por alumno usando un Stored Procedure
    public List<Tarea> listarPorAlumno(int alumnoId) {
        List<Tarea> lista = new ArrayList<>();
        String sql = "{CALL obtener_tareas_por_alumno(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, alumnoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getInt("id"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setFechaEntrega(rs.getString("fecha_entrega"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCursoNombre(rs.getString("curso_nombre"));
                lista.add(t);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar tareas por alumno");
            e.printStackTrace();
        }

        return lista;
    }

    // Listar tareas por curso usando un Stored Procedure
    public List<Tarea> listarPorCurso(int cursoId) {
        List<Tarea> lista = new ArrayList<>();
        String sql = "{CALL obtener_tareas_por_curso(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getInt("id"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setFechaEntrega(rs.getString("fecha_entrega"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCursoId(rs.getInt("curso_id"));
                lista.add(t);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar tareas por curso");
            e.printStackTrace();
        }

        return lista;
    }
}

