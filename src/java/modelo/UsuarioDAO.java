package modelo;

import conexion.Conexion;
import util.PasswordUtils;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    public boolean verificarCredencialesConHash(String username, String hashedPasswordFromFrontend) {
        String sql = "SELECT password FROM usuarios WHERE username = ? AND activo = TRUE";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPasswordInDB = rs.getString("password");

                // ✅ COMPARACIÓN DIRECTA SHA256 (ambos en SHA256)
                boolean coincide = hashedPasswordFromFrontend.equals(hashedPasswordInDB);
                System.out.println("🔐 Verificación SHA256 " + username + ": " + (coincide ? "✅ Correctas" : "❌ Incorrectas"));
                return coincide;
            } else {
                System.out.println("⚠️ Usuario no encontrado o inactivo: " + username);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar credenciales con hash para " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean verificarCredenciales(String username, String plainPassword) {
        String sql = "SELECT password FROM usuarios WHERE username = ? AND activo = TRUE";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                boolean coincide = PasswordUtils.checkPassword(plainPassword, hashedPassword);
                System.out.println("🔐 Verificación credenciales " + username + ": " + (coincide ? "✅ Correctas" : "❌ Incorrectas"));
                return coincide;
            } else {
                System.out.println("⚠️ Usuario no encontrado o inactivo: " + username);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar credenciales para " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "{CALL obtener_usuarios()}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Usuario u = mapearUsuario(rs);
                lista.add(u);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public boolean agregar(Usuario u) {
        System.out.println("🔍 Intentando agregar usuario: " + u.getUsername());

        String sql = "{CALL crear_usuario(?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            String hashedPassword = u.getPassword();
            System.out.println("🔐 Usando contraseña ya encriptada del frontend para: " + u.getUsername());

            cs.setString(1, u.getUsername());
            cs.setString(2, hashedPassword);
            cs.setString(3, u.getRol());

            int resultado = cs.executeUpdate();
            System.out.println("✅ Usuario registrado: " + u.getUsername() + " - Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error SQL al agregar usuario " + u.getUsername() + ": " + e.getMessage());

            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("duplicate")
                    || e.getMessage().contains("UNIQUE") || e.getErrorCode() == 1062) {
                System.err.println("⚠️ Usuario duplicado detectado: " + u.getUsername());
                return false;
            }

            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error general al agregar usuario " + u.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeUsuario(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("🔍 Verificación existencia usuario " + username + ": " + (count > 0 ? "EXISTE" : "NO EXISTE"));
                return count > 0;
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar existencia de usuario " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public Usuario obtenerPorId(int id) {
        Usuario u = null;
        String sql = "{CALL obtener_usuario_por_id(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                u = mapearUsuario(rs);
                System.out.println("✅ Usuario encontrado ID " + id + ": " + u.getUsername());
            } else {
                System.out.println("⚠️ Usuario no encontrado ID: " + id);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return u;
    }

    public Usuario obtenerPorUsername(String username) {
        Usuario u = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = TRUE";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = mapearUsuario(rs);
                System.out.println("✅ Usuario obtenido por username: " + username);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario por username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return u;
    }

    public Usuario obtenerDatosBloqueo(String username) {
        Usuario u = null;
        String sql = "SELECT intentos_fallidos, fecha_bloqueo, activo, ultima_conexion FROM usuarios WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setUsername(username);
                u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                u.setFechaBloqueo(rs.getTimestamp("fecha_bloqueo"));
                u.setActivo(rs.getBoolean("activo"));
                u.setUltimaConexion(rs.getTimestamp("ultima_conexion"));
                System.out.println("📊 Datos bloqueo obtenidos para: " + username + " - Intentos: " + u.getIntentosFallidos() + ", Activo: " + u.isActivo());
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener datos bloqueo para " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return u;
    }

    public boolean estaBloqueado(String username) {
        String sql = "SELECT activo, fecha_bloqueo FROM usuarios WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean activo = rs.getBoolean("activo");
                Timestamp fechaBloqueo = rs.getTimestamp("fecha_bloqueo");
                
                // Usuario está bloqueado si activo = false Y tiene fecha de bloqueo
                boolean bloqueado = !activo && fechaBloqueo != null;
                System.out.println("🔒 Estado bloqueo " + username + ": activo=" + activo + ", fechaBloqueo=" + fechaBloqueo + ", BLOQUEADO=" + bloqueado);
                return bloqueado;
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar bloqueo para " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean incrementarIntentoFallido(String username) {
        String sql = "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1, ultima_conexion = NOW() WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("📈 Intento fallido incrementado para: " + username + " - Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.err.println("❌ Error al incrementar intento fallido para " + username + ": " + e.getMessage());
            
            // Fallback: intentar con el procedimiento almacenado
            try {
                String sqlFallback = "{CALL incrementar_intento_fallido(?)}";
                try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sqlFallback)) {
                    cs.setString(1, username);
                    cs.executeUpdate();
                    System.out.println("🔄 Intento fallido incrementado via procedimiento para: " + username);
                    return true;
                }
            } catch (Exception ex) {
                System.err.println("❌ Error crítico incrementando intentos: " + ex.getMessage());
            }
            
            return false;
        }
    }

    public boolean bloquearUsuario(String username) {
        String sql = "UPDATE usuarios SET activo = 0, fecha_bloqueo = NOW(), intentos_fallidos = 3 WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("🚫 Usuario bloqueado: " + username + " - Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.err.println("❌ Error al bloquear usuario " + username + ": " + e.getMessage());
            
            // Fallback: intentar con el procedimiento almacenado
            try {
                String sqlFallback = "{CALL bloquear_usuario(?)}";
                try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sqlFallback)) {
                    cs.setString(1, username);
                    cs.executeUpdate();
                    System.out.println("🔄 Usuario bloqueado via procedimiento: " + username);
                    return true;
                }
            } catch (Exception ex) {
                System.err.println("❌ Error crítico bloqueando usuario: " + ex.getMessage());
            }
            
            return false;
        }
    }

    public boolean resetearIntentosUsuario(String username) {
        String sql = "UPDATE usuarios SET intentos_fallidos = 0, fecha_bloqueo = NULL, activo = 1 WHERE username = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("🔄 Intentos reseteados para: " + username + " - Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.err.println("❌ Error al resetear intentos para " + username + ": " + e.getMessage());
            
            // Fallback: intentar con el procedimiento almacenado
            try {
                String sqlFallback = "{CALL resetear_intentos_usuario(?)}";
                try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sqlFallback)) {
                    cs.setString(1, username);
                    cs.executeUpdate();
                    System.out.println("🔄 Intentos reseteados via procedimiento para: " + username);
                    return true;
                }
            } catch (Exception ex) {
                System.err.println("❌ Error crítico reseteando intentos: " + ex.getMessage());
            }
            
            return false;
        }
    }

    public boolean desbloquearUsuariosExpirados(int minutosBloqueo) {
        String sql = "UPDATE usuarios SET intentos_fallidos = 0, fecha_bloqueo = NULL, activo = 1 WHERE activo = 0 AND fecha_bloqueo IS NOT NULL AND fecha_bloqueo < DATE_SUB(NOW(), INTERVAL ? MINUTE)";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, minutosBloqueo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("🔄 Usuarios desbloqueados automáticamente: " + filas + " (expiración: " + minutosBloqueo + " min)");
            }
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al desbloquear usuarios expirados: " + e.getMessage());
            
            // Fallback: intentar con el procedimiento almacenado
            try {
                String sqlFallback = "{CALL desbloquear_usuarios_expirados(?)}";
                try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sqlFallback)) {
                    cs.setInt(1, minutosBloqueo);
                    int filas = cs.executeUpdate();
                    if (filas > 0) {
                        System.out.println("🔄 Usuarios desbloqueados via procedimiento: " + filas);
                    }
                    return true;
                }
            } catch (Exception ex) {
                System.err.println("❌ Error crítico desbloqueando usuarios: " + ex.getMessage());
            }
            
            return false;
        }
    }

    public boolean actualizar(Usuario u) {
        System.out.println("🔍 Actualizando usuario ID: " + u.getId() + ", Username: " + u.getUsername());

        String sql = "{CALL actualizar_usuario(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            String password = u.getPassword();

            if (password == null || password.isEmpty()) {
                Usuario usuarioActual = obtenerPorId(u.getId());
                if (usuarioActual != null) {
                    password = usuarioActual.getPassword();
                    System.out.println("🔄 Manteniendo contraseña existente del usuario");
                } else {
                    System.err.println("❌ No se pudo obtener el usuario actual para mantener la contraseña");
                    return false;
                }
            }

            cs.setInt(1, u.getId());
            cs.setString(2, u.getUsername());
            cs.setString(3, password);
            cs.setString(4, u.getRol());

            int resultado = cs.executeUpdate();
            System.out.println("✅ Usuario actualizado: " + u.getUsername() + " - Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error SQL al actualizar usuario " + u.getUsername() + ": " + e.getMessage());
            System.err.println("💡 Código de error SQL: " + e.getErrorCode());
            System.err.println("📝 Estado SQL: " + e.getSQLState());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error general al actualizar usuario " + u.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_usuario(?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            int resultado = cs.executeUpdate();
            System.out.println("🗑️ Usuario eliminado ID: " + id + " - Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar usuario ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarConexion() {
        try (Connection con = Conexion.getConnection()) {
            boolean isConnected = con != null && !con.isClosed();
            System.out.println("🔌 Verificación conexión BD: " + (isConnected ? "✅ CONECTADO" : "❌ DESCONECTADO"));
            return isConnected;
        } catch (Exception e) {
            System.err.println("❌ Error de conexión a BD: " + e.getMessage());
            return false;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));

        try {
            u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
            u.setFechaBloqueo(rs.getTimestamp("fecha_bloqueo"));
            u.setUltimaConexion(rs.getTimestamp("ultima_conexion"));
            u.setActivo(rs.getBoolean("activo"));
        } catch (SQLException e) {
            u.setIntentosFallidos(0);
            u.setActivo(true);
        }

        return u;
    }
}