/*
 * DAO PARA GESTION DE HORARIOS DE CLASES
 * 
 * Funcionalidades:
 * - Consulta de horarios por curso y profesor
 * - Creacion de nuevos horarios academicos
 * - Integracion con turnos y aulas
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class HorarioClaseDAO {

    /**
     * OBTENER HORARIOS POR CURSO Y TURNO
     * 
     * @param cursoId Identificador del curso
     * @param turnoId Identificador del turno
     * @return Lista de horarios que coinciden con los criterios
     */
    public List<HorarioClase> obtenerHorariosPorCursoTurno(int cursoId, int turnoId) {
        List<HorarioClase> lista = new ArrayList<>();
        String sql = "{CALL obtener_horarios_por_curso_turno(?, ?)}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, cursoId);
            cs.setInt(2, turnoId);
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                HorarioClase h = new HorarioClase();
                h.setId(rs.getInt("id"));
                h.setCursoId(rs.getInt("curso_id"));
                h.setTurnoId(rs.getInt("turno_id"));
                h.setDiaSemana(rs.getString("dia_semana"));
                h.setHoraInicio(rs.getString("hora_inicio"));
                h.setHoraFin(rs.getString("hora_fin"));
                h.setAulaId(rs.getInt("aula_id"));
                h.setActivo(rs.getBoolean("activo"));
                h.setCursoNombre(rs.getString("curso_nombre"));
                h.setTurnoNombre(rs.getString("turno_nombre"));
                h.setAulaNombre(rs.getString("aula_nombre"));
                h.setSedeNombre(rs.getString("sede_nombre"));
                lista.add(h);
            }
            
        } catch (Exception e) {
            System.out.println("Error al obtener horarios por curso y turno");
            e.printStackTrace();
        }
        
        return lista;
    }

    /**
     * OBTENER HORARIOS POR PROFESOR Y TURNO
     * 
     * @param profesorId Identificador del profesor
     * @param turnoId Identificador del turno
     * @return Lista de horarios asignados al profesor en el turno especificado
     */
    public List<HorarioClase> obtenerHorariosPorProfesorTurno(int profesorId, int turnoId) {
        List<HorarioClase> lista = new ArrayList<>();
        String sql = "{CALL obtener_horarios_por_profesor_turno(?, ?)}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, profesorId);
            cs.setInt(2, turnoId);
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                HorarioClase h = new HorarioClase();
                h.setId(rs.getInt("id"));
                h.setCursoId(rs.getInt("curso_id"));
                h.setTurnoId(rs.getInt("turno_id"));
                h.setDiaSemana(rs.getString("dia_semana"));
                h.setHoraInicio(rs.getString("hora_inicio"));
                h.setHoraFin(rs.getString("hora_fin"));
                h.setAulaId(rs.getInt("aula_id"));
                h.setCursoNombre(rs.getString("curso_nombre"));
                h.setGradoNombre(rs.getString("grado_nombre"));
                h.setAulaNombre(rs.getString("aula_nombre"));
                h.setTurnoNombre(rs.getString("turno_nombre"));
                lista.add(h);
            }
            
        } catch (Exception e) {
            System.out.println("Error al obtener horarios por profesor y turno");
            e.printStackTrace();
        }
        
        return lista;
    }

    /**
     * CREAR NUEVO HORARIO DE CLASE
     * 
     * @param h Objeto HorarioClase con todos los datos necesarios
     * @return true si la creacion fue exitosa
     */
    public boolean crearHorarioClase(HorarioClase h) {
        String sql = "{CALL crear_horario_clase(?, ?, ?, ?, ?, ?)}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, h.getCursoId());
            cs.setInt(2, h.getTurnoId());
            cs.setString(3, h.getDiaSemana());
            cs.setString(4, h.getHoraInicio());
            cs.setString(5, h.getHoraFin());
            cs.setInt(6, h.getAulaId());
            
            return cs.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.out.println("Error al crear horario de clase");
            e.printStackTrace();
            return false;
        }
    }
}