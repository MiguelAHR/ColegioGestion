/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class SedeDAO {

    public List<Sede> obtenerSedesActivas() {
        List<Sede> lista = new ArrayList<>();
        String sql = "{CALL obtener_sedes_activas()}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            
            while (rs.next()) {
                Sede s = new Sede();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setDireccion(rs.getString("direccion"));
                s.setTelefono(rs.getString("telefono"));
                s.setActivo(true);
                lista.add(s);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener sedes activas");
            e.printStackTrace();
        }
        
        return lista;
    }
}