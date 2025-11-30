/*
 * DAO PARA GESTION DE ALUMNOS
 * 
 * Funcionalidades:
 * - CRUD completo de alumnos
 * - Consultas por grado y curso
 * - Integracion con sistema academico
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class AlumnoDAO {

    /**
     * LISTAR ALUMNOS POR GRADO ACADEMICO
     * 
     * @param gradoId Identificador del grado
     * @return Lista de alumnos pertenecientes al grado especificado
     */
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
                a.setGradoId(rs.getInt("grado_id"));
                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * LISTAR TODOS LOS ALUMNOS REGISTRADOS
     * 
     * @return Lista completa de alumnos
     */
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
                a.setGradoId(rs.getInt("grado_id"));
                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * AGREGAR NUEVO ALUMNO
     * 
     * @param a Objeto Alumno con datos del nuevo alumno
     * @return true si el registro fue exitoso
     */
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

    /**
     * OBTENER ALUMNO POR ID
     * 
     * @param id Identificador unico del alumno
     * @return Objeto Alumno con datos completos
     */
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
                a.setGradoId(rs.getInt("grado_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * ACTUALIZAR DATOS DE ALUMNO EXISTENTE
     * 
     * @param a Objeto Alumno con datos actualizados
     * @return true si la actualizacion fue exitosa
     */
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

    /**
     * ELIMINAR ALUMNO POR ID
     * 
     * @param id Identificador del alumno a eliminar
     * @return true si la eliminacion fue exitosa
     */
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

    /**
     * OBTENER ALUMNOS POR CURSO ESPECIFICO
     * 
     * @param cursoId Identificador del curso
     * @return Lista de alumnos matriculados en el curso
     */
    public List<Alumno> obtenerAlumnosPorCurso(int cursoId) {
        List<Alumno> lista = new ArrayList<>();
        String sql = "{CALL obtener_alumnos_por_curso(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, cursoId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Alumno a = new Alumno();
                a.setId(rs.getInt("id"));
                a.setNombres(rs.getString("nombres"));
                a.setApellidos(rs.getString("apellidos"));
                a.setCorreo(rs.getString("correo"));
                a.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                a.setGradoId(rs.getInt("grado_id"));
                lista.add(a);
            }

            System.out.println("Alumnos encontrados para curso " + cursoId + ": " + lista.size());

        } catch (SQLException e) {
            System.out.println("Error SQL al obtener alumnos por curso:");
            System.out.println("   Codigo: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error general al obtener alumnos por curso");
            e.printStackTrace();
        }

        return lista;
    }
}