/*
 * DAO PARA GESTION DE AULAS
 * 
 * Funcionalidades:
 * - Consulta de aulas por sede
 * - Obtencion de informacion de capacidad y ubicacion
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class AulaDAO {

    /**
     * OBTENER AULAS POR SEDE ESPECIFICA
     * 
     * @param sedeId Identificador de la sede
     * @return Lista de aulas pertenecientes a la sede
     */
    public List<Aula> obtenerAulasPorSede(int sedeId) {
        List<Aula> lista = new ArrayList<>();
        String sql = "{CALL obtener_aulas_por_sede(?)}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, sedeId);
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                Aula a = new Aula();
                a.setId(rs.getInt("id"));
                a.setNombre(rs.getString("nombre"));
                a.setCapacidad(rs.getInt("capacidad"));
                a.setSedeId(rs.getInt("sede_id"));
                a.setSedeNombre(rs.getString("sede_nombre"));
                a.setActivo(true);
                lista.add(a);
            }
            
        } catch (Exception e) {
            System.out.println("Error al obtener aulas por sede");
            e.printStackTrace();
        }
        
        return lista;
    }
}