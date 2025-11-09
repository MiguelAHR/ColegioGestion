package modelo;

import conexion.Conexion;
import util.PasswordUtils;
import util.ValidacionContraseña;
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
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // Agregar un usuario usando BCrypt - CON VALIDACIÓN DE CONTRASEÑA FUERTE
    public boolean agregar(Usuario u) {
        System.out.println("🔍 Intentando agregar usuario: " + u.getUsername());
        
        // ✅ VALIDAR CONTRASEÑA FUERTE ANTES DE REGISTRAR
        if (!ValidacionContraseña.esPasswordFuerte(u.getPassword())) {
            System.out.println("❌ Contraseña débil - No se puede registrar usuario: " + u.getUsername());
            return false;
        }
        
        String sql = "{CALL crear_usuario(?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // ENCRIPTAR LA CONTRASEÑA ANTES DE GUARDAR
            String hashedPassword = PasswordUtils.hashPassword(u.getPassword());
            System.out.println("🔐 Contraseña hasheada para: " + u.getUsername());
            
            cs.setString(1, u.getUsername());
            cs.setString(2, hashedPassword);
            cs.setString(3, u.getRol());
            
            int resultado = cs.executeUpdate();
            System.out.println("✅ Usuario registrado con contraseña fuerte: " + u.getUsername() + " - Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error SQL al agregar usuario " + u.getUsername() + ": " + e.getMessage());
            
            // ✅ CORREGIDO: Manejo mejorado de usuario duplicado
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

    // ✅ NUEVO MÉTODO: Verificar si un usuario ya existe
    public boolean existeUsuario(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
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
                boolean coincide = PasswordUtils.checkPassword(password, hashedPassword);
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
                System.out.println("✅ Usuario obtenido por username: " + username);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario por username " + username + ": " + e.getMessage());
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
                System.out.println("📊 Datos bloqueo obtenidos para: " + username);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al obtener datos bloqueo para " + username + ": " + e.getMessage());
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
                boolean bloqueado = rs.getBoolean("bloqueado");
                System.out.println("🔒 Usuario " + username + " bloqueado: " + bloqueado);
                return bloqueado;
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ Error al verificar bloqueo para " + username + ": " + e.getMessage());
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
            System.out.println("📈 Intento fallido incrementado para: " + username);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al incrementar intento fallido para " + username + ": " + e.getMessage());
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
            System.out.println("🚫 Usuario bloqueado: " + username);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al bloquear usuario " + username + ": " + e.getMessage());
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
            System.out.println("🔄 Intentos reseteados para: " + username);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al resetear intentos para " + username + ": " + e.getMessage());
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
            int filas = cs.executeUpdate();
            System.out.println("🔄 Usuarios desbloqueados: " + filas + " (expiración: " + minutosBloqueo + " min)");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al desbloquear usuarios expirados: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // CORREGIDO: Actualizar un usuario - CON ENCRIPTACIÓN DE CONTRASEÑA
    public boolean actualizar(Usuario u) {
        System.out.println("🔍 Actualizando usuario ID: " + u.getId() + ", Username: " + u.getUsername());
        
        // ✅ CORREGIDO: Usar solo 4 parámetros que coincidan con el stored procedure
        String sql = "{CALL actualizar_usuario(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // VERIFICAR SI LA CONTRASEÑA NECESITA SER ENCRIPTADA
            String password = u.getPassword();
            
            if (password != null && !password.isEmpty() && !password.startsWith("$2a$")) {
                // Es una nueva contraseña (no encriptada) - Se encripta
                password = PasswordUtils.hashPassword(password);
                System.out.println("🔐 Nueva contraseña hasheada para actualización");
            } else if (password == null || password.isEmpty()) {
                // Si la contraseña está vacía, mantenemos la actual
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

    // Eliminar un usuario usando un Stored Procedure
    public boolean eliminar(int id) {
        String sql = "{CALL eliminar_usuario(?)}";

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

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

    // ✅ NUEVO MÉTODO: Verificar conexión a la BD
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