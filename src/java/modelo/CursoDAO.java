/*
 * DAO PARA GESTION DE CURSOS ACADEMICOS
 * 
 * Funcionalidades:
 * - CRUD completo de cursos
 * - Consultas por grado y profesor
 * - Gestion de asignacion de creditos
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class CursoDAO {

    /**
     * LISTAR CURSOS POR GRADO ACADEMICO
     * 
     * @param gradoId Identificador del grado
     * @return Lista de cursos pertenecientes al grado especificado
     */
    public List<Curso> listarPorGrado(int gradoId) {
        List<Curso> lista = new ArrayList<>();
        String sql = "{CALL obtener_cursos_por_grado(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, gradoId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Curso c = new Curso();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setCreditos(rs.getInt("creditos"));
                c.setGradoId(rs.getInt("grado_id"));
                c.setProfesorId(rs.getInt("profesor_id"));
                c.setGradoNombre(rs.getString("grado_nombre"));
                c.setProfesorNombre(rs.getString("profesor_nombre"));
                lista.add(c);
            }
        } catch (Exception e) {
            System.out.println("Error al listar cursos por grado");
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * LISTAR CURSOS ASIGNADOS A PROFESOR ESPECIFICO
     * 
     * @param profesorId Identificador del profesor
     * @return Lista de cursos asignados al profesor
     */
    public List<Curso> listarPorProfesor(int profesorId) {
        List<Curso> lista = new ArrayList<>();
        String sql = "{CALL obtener_cursos_por_profesor(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, profesorId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Curso c = new Curso();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setGradoId(rs.getInt("grado_id"));
                c.setProfesorId(rs.getInt("profesor_id"));
                c.setGradoNombre(rs.getString("grado_nombre"));
                lista.add(c);
                
                System.out.println("Curso encontrado: " + c.getNombre() + " - Grado: " + c.getGradoNombre());
            }

            System.out.println("Total cursos encontrados para profesor " + profesorId + ": " + lista.size());

        } catch (SQLException e) {
            System.out.println("Error SQL en listarPorProfesor:");
            System.out.println("   Codigo: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error general en listarPorProfesor:");
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * LISTAR TODOS LOS CURSOS REGISTRADOS
     * 
     * @return Lista completa de cursos
     */
    public List<Curso> listar() {
        List<Curso> lista = new ArrayList<>();
        String sql = "{CALL obtener_cursos()}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Curso c = new Curso();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setCreditos(rs.getInt("creditos"));
                c.setGradoId(rs.getInt("grado_id"));
                c.setProfesorId(rs.getInt("profesor_id"));
                c.setGradoNombre(rs.getString("grado_nombre"));
                c.setProfesorNombre(rs.getString("profesor_nombre"));
                lista.add(c);
            }

        } catch (Exception e) {
            System.out.println("Error al listar cursos");
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * OBTENER CURSO POR ID
     * 
     * @param id Identificador unico del curso
     * @return Objeto Curso con datos completos o null si no existe
     */
    public Curso obtenerPorId(int id) {
        Curso c = null;
        String sql = "{CALL obtener_curso_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                c = new Curso();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setCreditos(rs.getInt("creditos"));
                c.setGradoId(rs.getInt("grado_id"));
                c.setProfesorId(rs.getInt("profesor_id"));
                c.setGradoNombre(rs.getString("grado_nombre") + " - " + rs.getString("nivel"));
            }

        } catch (Exception e) {
            System.out.println("Error al obtener curso por ID");
            e.printStackTrace();
        }

        return c;
    }

    /**
     * AGREGAR NUEVO CURSO
     * 
     * @param c Objeto Curso con datos del nuevo curso
     * @return true si la creacion fue exitosa
     */
    public boolean agregar(Curso c) {
        String sql = "{CALL crear_curso(?, ?, ?, ?)}";
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, c.getNombre());
            cs.setInt(2, c.getGradoId());
            cs.setInt(3, c.getProfesorId());
            cs.setInt(4, c.getCreditos());

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al agregar curso");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ACTUALIZAR DATOS DE CURSO EXISTENTE
     * 
     * @param c Objeto Curso con datos actualizados
     * @return true si la actualizacion fue exitosa
     */
    public boolean actualizar(Curso c) {
        String sql = "{CALL actualizar_curso(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, c.getId());
            cs.setString(2, c.getNombre());
            cs.setInt(3, c.getGradoId());
            cs.setInt(4, c.getProfesorId());
            cs.setInt(5, c.getCreditos());

            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar curso");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ELIMINAR CURSO POR ID
     * 
     * @param id Identificador del curso a eliminar
     * @return true si la eliminacion fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_curso(?)}";
        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            return cs.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al eliminar curso");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * METODO ALTERNATIVO PARA OBTENER CURSOS POR PROFESOR
     * 
     * @param profesorId Identificador del profesor
     * @return Lista de cursos asignados al profesor
     */
    public List<Curso> obtenerCursosPorProfesor(int profesorId) {
        return listarPorProfesor(profesorId);
    }
    
    /**
     * METODO ALTERNATIVO PARA OBTENER CURSO POR ID
     * 
     * @param cursoId Identificador del curso
     * @return Objeto Curso con datos completos
     */
    public Curso obtenerCursoPorId(int cursoId) {
        return obtenerPorId(cursoId);
    }
}