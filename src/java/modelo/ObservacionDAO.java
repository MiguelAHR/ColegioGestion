/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class ObservacionDAO {

    // Agregar una observación usando un Stored Procedure
    public boolean agregar(Observacion o) {
        String sql = "{CALL crear_observacion(?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, o.getCursoId());
            cs.setInt(2, o.getAlumnoId());
            cs.setString(3, o.getTexto());
            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al agregar observación");
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar una observación usando un Stored Procedure
    public boolean actualizar(Observacion o) {
        String sql = "{CALL actualizar_observacion(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, o.getId());
            cs.setInt(2, o.getAlumnoId());
            cs.setString(3, o.getTexto());
            cs.setInt(4, o.getCursoId());
            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar observación");
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una observación usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_observacion(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al eliminar observación");
            e.printStackTrace();
            return false;
        }
    }

    // Obtener una observación por ID usando un Stored Procedure
    public Observacion obtenerPorId(int id) {
        Observacion o = null;
        String sql = "{CALL obtener_observacion_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                o = new Observacion();
                o.setId(rs.getInt("id"));
                o.setCursoId(rs.getInt("curso_id"));
                o.setAlumnoId(rs.getInt("alumno_id"));
                o.setTexto(rs.getString("texto"));
                o.setAlumnoNombre(rs.getString("alumno_nombres") + " " + rs.getString("alumno_apellidos"));
                o.setCursoNombre(rs.getString("curso_nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    // Listar observaciones por alumno usando un Stored Procedure
    public List<Observacion> listarPorAlumno(int alumnoId) {
        List<Observacion> lista = new ArrayList<>();
        String sql = "{CALL obtener_observaciones_por_alumno(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, alumnoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Observacion o = new Observacion();
                o.setId(rs.getInt("id"));
                o.setCursoId(rs.getInt("curso_id"));
                o.setAlumnoId(rs.getInt("alumno_id"));
                o.setTexto(rs.getString("texto"));
                o.setCursoNombre(rs.getString("curso_nombre"));
                lista.add(o);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar observaciones por alumno");
            e.printStackTrace();
        }

        return lista;
    }

    // Listar observaciones por curso usando un Stored Procedure
    public List<Observacion> listarPorCurso(int cursoId) {
        List<Observacion> lista = new ArrayList<>();
        String sql = "{CALL obtener_observaciones_por_curso(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Observacion o = new Observacion();
                o.setId(rs.getInt("id"));
                o.setCursoId(rs.getInt("curso_id"));
                o.setAlumnoId(rs.getInt("alumno_id"));
                o.setTexto(rs.getString("texto"));
                o.setAlumnoNombre(rs.getString("alumno_nombres") + " " + rs.getString("alumno_apellidos"));
                lista.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}

