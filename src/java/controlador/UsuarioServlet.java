package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Usuario;
import modelo.UsuarioDAO;
import util.ValidacionContraseña;
import util.PasswordUtils;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            // 📋 LISTAR TODOS LOS USUARIOS
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            return;
        }

        switch (accion) {
            case "nuevo":
                // 🆕 CARGAR FORMULARIO DE NUEVO USUARIO
                request.getRequestDispatcher("usuarioForm.jsp").forward(request, response);
                break;
                
            case "editar":
                // ✏️ CARGAR FORMULARIO DE EDICIÓN
                try {
                    int idEditar = Integer.parseInt(request.getParameter("id"));
                    Usuario u = dao.obtenerPorId(idEditar);
                    if (u != null) {
                        request.setAttribute("usuario", u);
                        request.getRequestDispatcher("usuarioForm.jsp").forward(request, response);
                    } else {
                        session.setAttribute("error", "Usuario no encontrado");
                        response.sendRedirect("UsuarioServlet");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de usuario inválido");
                    response.sendRedirect("UsuarioServlet");
                }
                break;

            case "eliminar":
                // 🗑️ ELIMINAR USUARIO
                try {
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    if (dao.eliminar(idEliminar)) {
                        session.setAttribute("mensaje", "Usuario eliminado exitosamente");
                    } else {
                        session.setAttribute("error", "No se pudo eliminar el usuario");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de usuario inválido");
                }
                response.sendRedirect("UsuarioServlet");
                break;

            default:
                response.sendRedirect("UsuarioServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // ✅ VERIFICAR CONEXIÓN A LA BD PRIMERO
        if (!dao.verificarConexion()) {
            session.setAttribute("error", "Error de conexión a la base de datos. Contacte al administrador.");
            response.sendRedirect("UsuarioServlet");
            return;
        }

        // Obtener parámetros del formulario
        String idParam = request.getParameter("id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");

        System.out.println("🔍 Datos recibidos - ID: " + idParam + ", Username: " + username + ", Rol: " + rol);

        // Validar campos obligatorios
        if (username == null || username.trim().isEmpty() || rol == null || rol.trim().isEmpty()) {
            session.setAttribute("error", "Nombre de usuario y rol son obligatorios");
            response.sendRedirect("UsuarioServlet");
            return;
        }

        int id = 0;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "ID de usuario inválido");
                response.sendRedirect("UsuarioServlet");
                return;
            }
        }

        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername(username.trim());
        u.setRol(rol.trim());

        try {
            if (id == 0) {
                // 🆕 REGISTRAR NUEVO USUARIO
                System.out.println("🆕 Creando nuevo usuario: " + username);

                // ✅ CORREGIDO: Verificar si el usuario ya existe ANTES de intentar crear
                if (dao.existeUsuario(username.trim())) {
                    System.out.println("❌ Usuario ya existe: " + username);
                    session.setAttribute("error", "No se pudo registrar el usuario. El nombre de usuario '" + username + "' ya existe.");
                    response.sendRedirect("UsuarioServlet");
                    return;
                }

                if (password == null || password.trim().isEmpty()) {
                    session.setAttribute("error", "La contraseña es obligatoria para nuevos usuarios");
                    response.sendRedirect("UsuarioServlet");
                    return;
                }

                // ✅ VALIDAR CONTRASEÑA FUERTE EN EL SERVIDOR
                if (!ValidacionContraseña.esPasswordFuerte(password)) {
                    String mensajeError = "No se pudo registrar el usuario. La contraseña debe cumplir con los requisitos de seguridad.";
                    session.setAttribute("error", mensajeError);
                    response.sendRedirect("UsuarioServlet");
                    return;
                }

                // 🔐 ASIGNAR CONTRASEÑA (será encriptada en el DAO)
                u.setPassword(password.trim());

                if (dao.agregar(u)) {
                    System.out.println("✅ Usuario creado exitosamente: " + username);
                    session.setAttribute("mensaje", "Usuario registrado exitosamente");
                } else {
                    System.out.println("❌ Error al crear usuario: " + username);
                    session.setAttribute("error", "No se pudo registrar el usuario. Error del sistema.");
                }

            } else {
                // ✏️ ACTUALIZAR USUARIO EXISTENTE
                System.out.println("✏️ Actualizando usuario ID: " + id);

                Usuario usuarioActual = dao.obtenerPorId(id);
                if (usuarioActual == null) {
                    session.setAttribute("error", "Usuario no encontrado");
                    response.sendRedirect("UsuarioServlet");
                    return;
                }

                // ✅ CORREGIDO: Verificar si el nombre de usuario ya existe (para otro usuario)
                if (!usuarioActual.getUsername().equals(username.trim())) {
                    if (dao.existeUsuario(username.trim())) {
                        System.out.println("❌ Nombre de usuario ya existe: " + username);
                        session.setAttribute("error", "No se pudo actualizar el usuario. El nombre de usuario '" + username + "' ya existe.");
                        response.sendRedirect("UsuarioServlet?accion=editar&id=" + id);
                        return;
                    }
                }

                if (password == null || password.trim().isEmpty()) {
                    // 🔄 MANTENER CONTRASEÑA ACTUAL - pasar null para que el DAO la mantenga
                    u.setPassword(null);
                    System.out.println("🔄 Manteniendo contraseña actual para usuario: " + username);
                } else {
                    // ✅ VALIDAR NUEVA CONTRASEÑA SI SE PROPORCIONA
                    if (!ValidacionContraseña.esPasswordFuerte(password)) {
                        String mensajeError = "No se pudo actualizar el usuario. La nueva contraseña debe cumplir con los requisitos de seguridad.";
                        session.setAttribute("error", mensajeError);
                        response.sendRedirect("UsuarioServlet?accion=editar&id=" + id);
                        return;
                    }

                    // 🔐 ASIGNAR NUEVA CONTRASEÑA (será encriptada en el DAO)
                    u.setPassword(password.trim());
                    System.out.println("🔄 Actualizando contraseña para usuario: " + username);
                }

                // ✅ DEBUG: Mostrar datos antes de actualizar
                System.out.println("🔍 DEBUG - Datos del usuario a actualizar:");
                System.out.println("  ID: " + u.getId());
                System.out.println("  Username: " + u.getUsername());
                System.out.println("  Rol: " + u.getRol());
                System.out.println("  Password proporcionada: " + (u.getPassword() != null ? "SÍ" : "NO (mantener actual)"));

                if (dao.actualizar(u)) {
                    System.out.println("✅ Usuario actualizado exitosamente: " + username);
                    session.setAttribute("mensaje", "Usuario actualizado exitosamente");
                } else {
                    System.out.println("❌ Error al actualizar usuario: " + username);
                    session.setAttribute("error", "No se pudo actualizar el usuario. Verifique los datos o contacte al administrador.");
                }
            }

        } catch (Exception e) {
            System.err.println("💥 Error en el servlet UsuarioServlet:");
            e.printStackTrace();
            session.setAttribute("error", "Error en el sistema: " + e.getMessage());
        }

        response.sendRedirect("UsuarioServlet");
    }
}