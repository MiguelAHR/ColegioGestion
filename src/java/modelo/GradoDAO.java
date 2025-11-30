/*
 * DAO PARA GESTION DE GRADOS ACADEMICOS
 * 
 * Funcionalidades:
 * - CRUD completo de grados academicos
 * - Consulta por niveles educativos
 * - Integracion con stored procedures
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class GradoDAO {

    /**
     * LISTAR TODOS LOS GRADOS ACADEMICOS
     * 
     * @return Lista completa de grados disponibles
     */
    public List<Grado> listar() {
        List<Grado> lista = new ArrayList<>();
        String sql = "{CALL obtener_grados()}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Grado g = new Grado();
                g.setId(rs.getInt("id"));
                g.setNombre(rs.getString("nombre"));
                g.setNivel(rs.getString("nivel"));
                lista.add(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * AGREGAR NUEVO GRADO ACADEMICO
     * 
     * @param g Objeto Grado con datos del nuevo grado
     * @return true si la creacion fue exitosa
     */
    public boolean agregar(Grado g) {
        String sql = "{CALL crear_grado(?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, g.getNombre());
            cs.setString(2, g.getNivel());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * OBTENER GRADO POR ID
     * 
     * @param id Identificador unico del grado
     * @return Objeto Grado o null si no existe
     */
    public Grado obtenerPorId(int id) {
        Grado g = null;
        String sql = "{CALL obtener_grado_por_id(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                g = new Grado();
                g.setId(rs.getInt("id"));
                g.setNombre(rs.getString("nombre"));
                g.setNivel(rs.getString("nivel"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return g;
    }

    /**
     * ACTUALIZAR DATOS DE GRADO EXISTENTE
     * 
     * @param g Objeto Grado con datos actualizados
     * @return true si la actualizacion fue exitosa
     */
    public boolean actualizar(Grado g) {
        String sql = "{CALL actualizar_grado(?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, g.getId());
            cs.setString(2, g.getNombre());
            cs.setString(3, g.getNivel());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ELIMINAR GRADO POR ID
     * 
     * @param id Identificador del grado a eliminar
     * @return true si la eliminacion fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_grado(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}