/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class TurnoDAO {

    public List<Turno> obtenerTurnosActivos() {
        List<Turno> lista = new ArrayList<>();
        String sql = "{CALL obtener_turnos_activos()}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            
            while (rs.next()) {
                Turno t = new Turno();
                t.setId(rs.getInt("id"));
                t.setNombre(rs.getString("nombre"));
                t.setHoraInicio(rs.getString("hora_inicio"));
                t.setHoraFin(rs.getString("hora_fin"));
                t.setActivo(true);
                lista.add(t);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener turnos activos");
            e.printStackTrace();
        }
        
        return lista;
    }
}