/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Usuario;
import modelo.UsuarioDAO;
import util.ValidacionContraseña; // IMPORTANTE: Agregar para validación

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            return;
        }

        switch (accion) {
            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Usuario u = dao.obtenerPorId(idEditar);
                request.setAttribute("usuario", u);
                request.getRequestDispatcher("usuarioForm.jsp").forward(request, response);
                break;

            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("UsuarioServlet");
                break;

            default:
                response.sendRedirect("UsuarioServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(password);
        u.setRol(rol);

        try {
            if (id == 0) {
                // ✅ REGISTRAR NUEVO USUARIO - CON VALIDACIÓN
                if (dao.agregar(u)) {
                    request.getSession().setAttribute("mensaje", "Usuario registrado exitosamente");
                } else {
                    // ❌ Error por contraseña débil (solo en registro)
                    String mensajeError = "No se pudo registrar el usuario. " + 
                                         "La contraseña debe ser fuerte: " + 
                                         ValidacionContraseña.obtenerRequisitosPassword();
                    request.getSession().setAttribute("error", mensajeError);
                }
            } else {
                // ✅ ACTUALIZAR USUARIO EXISTENTE - SIN VALIDACIÓN DE CONTRASEÑA FUERTE
                u.setId(id);
                
                // Obtener usuario actual para comparar contraseñas
                Usuario usuarioActual = dao.obtenerPorId(id);
                if (usuarioActual != null) {
                    // Si la contraseña no cambió o está vacía, mantener la actual
                    if (password == null || password.isEmpty() || password.equals(usuarioActual.getPassword())) {
                        u.setPassword(usuarioActual.getPassword()); // Mantener contraseña actual
                    }
                    // ❌ SI LA CONTRASEÑA CAMBIÓ: Ya no se valida fortaleza en dao.actualizar()
                }
                
                // ✅ ACTUALIZAR SIEMPRE (ya no hay validación de contraseña fuerte)
                if (dao.actualizar(u)) {
                    request.getSession().setAttribute("mensaje", "Usuario actualizado exitosamente");
                } else {
                    // ❌ Error genérico (no por contraseña débil)
                    request.getSession().setAttribute("error", "No se pudo actualizar el usuario. Error del sistema.");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error en el sistema: " + e.getMessage());
        }

        response.sendRedirect("UsuarioServlet");
    }
}