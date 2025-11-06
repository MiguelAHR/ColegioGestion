/*
 * SERVLET PARA ADMINISTRACIÓN COMPLETA DE USUARIOS DEL SISTEMA
 * 
 * Funcionalidades: CRUD completo de usuarios, validación de contraseñas seguras
 * Roles: Exclusivo para administradores
 * Seguridad: Validación con BCrypt, políticas de contraseñas fuertes
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Usuario;
import modelo.UsuarioDAO;
import util.ValidacionContraseña; // 🛡️ UTILITARIO PARA VALIDACIÓN DE CONTRASEÑAS

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    // 👥 DAO PARA OPERACIONES CON LA TABLA DE USUARIOS
    UsuarioDAO dao = new UsuarioDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y NAVEGACIÓN DE USUARIOS
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los usuarios (acción por defecto)
     * - editar: Formulario para modificar usuario existente
     * - eliminar: Eliminar usuario del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // 📋 ACCIÓN POR DEFECTO: LISTAR TODOS LOS USUARIOS
        if (accion == null || accion.isEmpty()) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            return;
        }

        // 🎯 PROCESAR ACCIÓN ESPECÍFICA SOLICITADA
        switch (accion) {
            case "editar":
                // ✏️ CARGAR FORMULARIO DE EDICIÓN DE USUARIO
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Usuario u = dao.obtenerPorId(idEditar);
                request.setAttribute("usuario", u);
                request.getRequestDispatcher("usuarioForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // 🗑️ ELIMINAR USUARIO DEL SISTEMA
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("UsuarioServlet");
                break;

            default:
                // 🔄 REDIRECCIÓN POR DEFECTO SI LA ACCIÓN NO ES RECONOCIDA
                response.sendRedirect("UsuarioServlet");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR USUARIOS
     * 
     * Diferencias entre crear y actualizar:
     * - Crear: Requiere contraseña fuerte y validación completa
     * - Actualizar: Mantiene contraseña actual si no se cambia
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CAPTURAR DATOS DEL FORMULARIO
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(password);
        u.setRol(rol);

        try {
            if (id == 0) {
                // 🆕 REGISTRAR NUEVO USUARIO - CON VALIDACIÓN ESTRICTA DE CONTRASEÑA
                if (dao.agregar(u)) {
                    request.getSession().setAttribute("mensaje", "Usuario registrado exitosamente");
                } else {
                    // ❌ ERROR POR CONTRASEÑA DÉBIL (SOLO EN REGISTRO NUEVO)
                    String mensajeError = "No se pudo registrar el usuario. " + 
                                         "La contraseña debe ser fuerte: " + 
                                         ValidacionContraseña.obtenerRequisitosPassword();
                    request.getSession().setAttribute("error", mensajeError);
                }
            } else {
                // ✏️ ACTUALIZAR USUARIO EXISTENTE - LÓGICA MÁS FLEXIBLE
                u.setId(id);
                
                // 🔍 OBTENER USUARIO ACTUAL PARA COMPARAR CONTRASEÑAS
                Usuario usuarioActual = dao.obtenerPorId(id);
                if (usuarioActual != null) {
                    // 🔄 MANTENER CONTRASEÑA ACTUAL SI NO SE MODIFICA O ESTÁ VACÍA
                    if (password == null || password.isEmpty() || password.equals(usuarioActual.getPassword())) {
                        u.setPassword(usuarioActual.getPassword()); // 🔐 CONSERVAR CONTRASEÑA ACTUAL
                    }
                    // 💡 NOTA: En actualizaciones no se valida fortaleza de contraseña por usabilidad
                }
                
                // 💾 EJECUTAR ACTUALIZACIÓN EN BASE DE DATOS
                if (dao.actualizar(u)) {
                    request.getSession().setAttribute("mensaje", "Usuario actualizado exitosamente");
                } else {
                    // ❌ ERROR GENÉRICO EN ACTUALIZACIÓN (NO POR CONTRASEÑA DÉBIL)
                    request.getSession().setAttribute("error", "No se pudo actualizar el usuario. Error del sistema.");
                }
            }
            
        } catch (Exception e) {
            // 🚨 CAPTURA DE ERRORES INESPERADOS
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error en el sistema: " + e.getMessage());
        }

        // 🔄 REDIRIGIR A LA LISTA PRINCIPAL DE USUARIOS
        response.sendRedirect("UsuarioServlet");
    }
}