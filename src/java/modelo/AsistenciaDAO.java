/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class AsistenciaDAO {

    public boolean registrarAsistencia(Asistencia a) {
        String sql = "{CALL registrar_asistencia(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, a.getAlumnoId());
            cs.setInt(2, a.getCursoId());
            cs.setInt(3, a.getTurnoId());
            cs.setString(4, a.getFecha());
            cs.setString(5, a.getHoraClase());
            cs.setString(6, a.getEstado());
            cs.setString(7, a.getObservaciones());
            cs.setInt(8, a.getRegistradoPor());

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al registrar asistencia");
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarAsistenciaGrupal(int cursoId, int turnoId, String fecha,
            String horaClase, String alumnosJson, int registradoPor) {
        String sql = "{CALL registrar_asistencia_grupal(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, cursoId);
            cs.setInt(2, turnoId);
            cs.setString(3, fecha);
            cs.setString(4, horaClase);
            cs.setString(5, alumnosJson);
            cs.setInt(6, registradoPor);

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al registrar asistencia grupal");
            e.printStackTrace();
            return false;
        }
    }

    public List<Asistencia> obtenerAsistenciasPorCursoTurnoFecha(int cursoId, int turnoId, String fecha) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "{CALL obtener_asistencias_por_curso_turno_fecha(?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, cursoId);
            cs.setInt(2, turnoId);
            cs.setString(3, fecha);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                a.setCursoId(rs.getInt("curso_id"));
                a.setTurnoId(rs.getInt("turno_id"));
                a.setFecha(rs.getString("fecha"));
                a.setHoraClase(rs.getString("hora_clase"));
                a.setEstado(rs.getString("estado"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setRegistradoPor(rs.getInt("registrado_por"));
                a.setAlumnoNombre(rs.getString("alumno_nombre"));
                a.setProfesorNombre(rs.getString("profesor_nombre"));
                a.setTurnoNombre(rs.getString("turno_nombre"));
                lista.add(a);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener asistencias por curso, turno y fecha");
            e.printStackTrace();
        }

        return lista;
    }

    public List<Asistencia> obtenerAsistenciasPorAlumnoTurno(int alumnoId, int turnoId, int mes, int anio) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "{CALL obtener_asistencias_por_alumno_turno(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            cs.setInt(2, turnoId);
            cs.setInt(3, mes);
            cs.setInt(4, anio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                a.setCursoId(rs.getInt("curso_id"));
                a.setTurnoId(rs.getInt("turno_id"));
                a.setFecha(rs.getString("fecha"));
                a.setHoraClase(rs.getString("hora_clase"));
                a.setEstado(rs.getString("estado"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setRegistradoPor(rs.getInt("registrado_por"));
                a.setCursoNombre(rs.getString("curso_nombre"));
                a.setTurnoNombre(rs.getString("turno_nombre"));

                a.setGradoNombre(rs.getString("grado_nombre"));

                lista.add(a);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener asistencias por alumno y turno");
            e.printStackTrace();
        }

        return lista;
    }

    public Map<String, Object> obtenerResumenAsistenciaAlumnoTurno(int alumnoId, int turnoId, int mes, int anio) {
        Map<String, Object> resumen = new HashMap<>();
        String sql = "{CALL obtener_resumen_asistencia_alumno_turno(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            cs.setInt(2, turnoId);
            cs.setInt(3, mes);
            cs.setInt(4, anio);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                resumen.put("totalClases", rs.getInt("total_clases"));
                resumen.put("presentes", rs.getInt("presentes"));
                resumen.put("tardanzas", rs.getInt("tardanzas"));
                resumen.put("ausentes", rs.getInt("ausentes"));
                resumen.put("justificados", rs.getInt("justificados"));
                resumen.put("porcentajeAsistencia", rs.getDouble("porcentaje_asistencia"));
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener resumen de asistencia");
            e.printStackTrace();
        }

        return resumen;
    }
}
