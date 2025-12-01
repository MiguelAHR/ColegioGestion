package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class CursoDAO {

    /**
     * LISTAR CURSOS POR GRADO ACADEMICO
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
     * VERIFICAR SI UN CURSO ESTÁ ASIGNADO A UN PROFESOR
     */
    public boolean isCursoAssignedToProfesor(int cursoId, int profesorId) {
        String sql = "SELECT COUNT(*) as count FROM cursos WHERE id = ? AND profesor_id = ?";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cursoId);
            ps.setInt(2, profesorId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (Exception e) {
            System.out.println("Error al verificar asignación curso-profesor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * VERIFICAR EXISTENCIA DE CURSO
     */
    public boolean existeCurso(int cursoId) {
        String sql = "SELECT COUNT(*) as count FROM cursos WHERE id = ?";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cursoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (Exception e) {
            System.out.println("Error al verificar existencia de curso: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}