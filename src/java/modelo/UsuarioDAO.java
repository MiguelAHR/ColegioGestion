package modelo;

import conexion.Conexion;
import util.PasswordUtils;  // Importar la clase de utilidades
import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    // Listar todos los usuarios usando un Stored Procedure
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "{CALL obtener_usuarios()}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Usuario u = mapearUsuario(rs);
                lista.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Agregar un usuario usando BCrypt
    public boolean agregar(Usuario u) {
        String sql = "{CALL crear_usuario(?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // ENCRIPTAR LA CONTRASEÑA ANTES DE GUARDAR
            String hashedPassword = PasswordUtils.hashPassword(u.getPassword());
            
            cs.setString(1, u.getUsername());
            cs.setString(2, hashedPassword); // Guardar contraseña encriptada
            cs.setString(3, u.getRol());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener un usuario por ID usando un Stored Procedure
    public Usuario obtenerPorId(int id) {
        Usuario u = null;
        String sql = "{CALL obtener_usuario_por_id(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                u = mapearUsuario(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return u;
    }

    // NUEVO MÉTODO: Verificar credenciales con BCrypt
    public boolean verificarCredenciales(String username, String password) {
        String sql = "SELECT password FROM usuarios WHERE username = ? AND activo = TRUE";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // VERIFICAR CONTRASEÑA CON BCRYPT
                return PasswordUtils.checkPassword(password, hashedPassword);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    // Obtener usuario por username (para login)
    public Usuario obtenerPorUsername(String username) {
        Usuario u = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = TRUE";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = mapearUsuario(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return u;
    }

    // Obtener datos de bloqueo de usuario
    public Usuario obtenerDatosBloqueo(String username) {
        Usuario u = null;
        String sql = "{CALL obtener_datos_bloqueo_usuario(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, username);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setUsername(username);
                u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                u.setFechaBloqueo(rs.getTimestamp("fecha_bloqueo"));
                u.setActivo(rs.getBoolean("activo"));
                u.setUltimaConexion(rs.getTimestamp("ultima_conexion"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return u;
    }

    // Verificar si usuario está bloqueado (versión simplificada)
    public boolean estaBloqueado(String username) {
        String sql = "{CALL verificar_usuario_bloqueado(?)}";
        
        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, username);
            ResultSet rs = cs.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("bloqueado");
            }
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Incrementar intento fallido
    public boolean incrementarIntentoFallido(String username) {
        String sql = "{CALL incrementar_intento_fallido(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, username);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Bloquear usuario
    public boolean bloquearUsuario(String username) {
        String sql = "{CALL bloquear_usuario(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, username);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Resetear intentos y desbloquear
    public boolean resetearIntentosUsuario(String username) {
        String sql = "{CALL resetear_intentos_usuario(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, username);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Desbloquear usuarios expirados
    public boolean desbloquearUsuariosExpirados(int minutosBloqueo) {
        String sql = "{CALL desbloquear_usuarios_expirados(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, minutosBloqueo);
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar un usuario - CON ENCRIPTACIÓN SI CAMBIA CONTRASEÑA
    public boolean actualizar(Usuario u) {
        String sql = "{CALL actualizar_usuario(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // VERIFICAR SI LA CONTRASEÑA NECESITA SER ENCRIPTADA
            String password = u.getPassword();
            // Si la contraseña no empieza con el patrón de BCrypt, encriptarla
            if (password != null && !password.startsWith("$2a$")) {
                password = PasswordUtils.hashPassword(password);
            }

            cs.setInt(1, u.getId());
            cs.setString(2, u.getUsername());
            cs.setString(3, password);
            cs.setString(4, u.getRol());
            cs.setInt(5, u.getIntentosFallidos());
            cs.setBoolean(6, u.isActivo());
            cs.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un usuario usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_usuario(?)}";

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

    // Método auxiliar para mapear el ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        
        // Nuevos campos
        try {
            u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
            u.setFechaBloqueo(rs.getTimestamp("fecha_bloqueo"));
            u.setUltimaConexion(rs.getTimestamp("ultima_conexion"));
            u.setActivo(rs.getBoolean("activo"));
        } catch (SQLException e) {
            // Si las columnas no existen, usar valores por defecto
            u.setIntentosFallidos(0);
            u.setActivo(true);
        }
        
        return u;
    }
}