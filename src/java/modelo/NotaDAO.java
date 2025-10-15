/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class NotaDAO {

    // Agregar una nota usando un Stored Procedure
    public boolean agregar(Nota n) {
        String sql = "{CALL crear_nota(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, n.getCursoId());
            cs.setInt(2, n.getTareaId());
            cs.setInt(3, n.getAlumnoId());
            cs.setDouble(4, n.getNota());

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al agregar nota");
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar una nota usando un Stored Procedure
    public boolean actualizar(Nota n) {
        String sql = "{CALL actualizar_nota(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, n.getId());
            cs.setInt(2, n.getTareaId());
            cs.setInt(3, n.getAlumnoId());
            cs.setDouble(4, n.getNota());
            cs.setInt(5, n.getCursoId());

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar nota");
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una nota usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_nota(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar nota");
            e.printStackTrace();
            return false;
        }
    }

    // Obtener una nota por ID usando un Stored Procedure
    public Nota obtenerPorId(int id) {
        Nota n = null;
        String sql = "{CALL obtener_nota_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                n = new Nota();
                n.setId(rs.getInt("id"));
                n.setCursoId(rs.getInt("curso_id"));
                n.setTareaId(rs.getInt("tarea_id"));
                n.setAlumnoId(rs.getInt("alumno_id"));
                n.setNota(rs.getDouble("nota"));
                n.setAlumnoNombre(rs.getString("alumno_nombres") + " " + rs.getString("alumno_apellidos"));
                n.setTareaNombre(rs.getString("tarea_nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    // Listar notas por alumno usando un Stored Procedure
    public List<Nota> listarPorAlumno(int alumnoId) {
        List<Nota> lista = new ArrayList<>();
        String sql = "{CALL obtener_notas_por_alumno(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, alumnoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Nota n = new Nota();
                n.setId(rs.getInt("id"));
                n.setCursoId(rs.getInt("curso_id"));
                n.setTareaId(rs.getInt("tarea_id"));
                n.setAlumnoId(rs.getInt("alumno_id"));
                n.setNota(rs.getDouble("nota"));
                n.setTareaNombre(rs.getString("tarea_nombre"));
                n.setCursoNombre(rs.getString("curso_nombre"));
                lista.add(n);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar notas por alumno");
            e.printStackTrace();
        }

        return lista;
    }

    // Listar notas por curso usando un Stored Procedure
    public List<Nota> listarPorCurso(int cursoId) {
        List<Nota> lista = new ArrayList<>();
        String sql = "{CALL obtener_notas_por_curso(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Nota n = new Nota();
                n.setId(rs.getInt("id"));
                n.setCursoId(rs.getInt("curso_id"));
                n.setTareaId(rs.getInt("tarea_id"));
                n.setAlumnoId(rs.getInt("alumno_id"));
                n.setNota(rs.getDouble("nota"));
                n.setAlumnoNombre(rs.getString("alumno_nombres") + " " + rs.getString("alumno_apellidos"));
                n.setTareaNombre(rs.getString("tarea_nombre"));
                lista.add(n);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar notas por curso");
            e.printStackTrace();
        }

        return lista;
    }
}

