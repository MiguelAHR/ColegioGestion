/*
 * SERVLET PARA GESTIÓN DE GRADOS ACADÉMICOS
 * 
 * Funcionalidades: CRUD completo de grados (niveles educativos)
 * Roles: Administrador
 * Integración: Base para cursos y alumnos
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Grado;
import modelo.GradoDAO;

@WebServlet("/GradoServlet")
public class GradoServlet extends HttpServlet {

    // 🎓 DAO PARA OPERACIONES CON LA TABLA DE GRADOS
    GradoDAO dao = new GradoDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y NAVEGACIÓN DE GRADOS
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los grados (acción por defecto)
     * - editar: Formulario para modificar grado existente
     * - eliminar: Eliminar grado del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // 📋 ACCIÓN POR DEFECTO: LISTAR TODOS LOS GRADOS
        if (accion == null || accion.isEmpty()) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("grados.jsp").forward(request, response);
            return;
        }

        // 🎯 EJECUTAR ACCIÓN ESPECÍFICA
        switch (accion) {
            case "editar":
                // ✏️ CARGAR FORMULARIO DE EDICIÓN DE GRADO
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Grado g = dao.obtenerPorId(idEditar);
                request.setAttribute("grado", g);
                request.getRequestDispatcher("gradoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // 🗑️ ELIMINAR GRADO DEL SISTEMA
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("GradoServlet");
                break;

            default:
                // 🔄 REDIRECCIÓN POR DEFECTO
                response.sendRedirect("GradoServlet");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR GRADOS
     * 
     * Maneja el envío de formularios para crear nuevos grados
     * y actualizar grados existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO GRADO CON DATOS DEL FORMULARIO
        Grado g = new Grado();
        g.setNombre(request.getParameter("nombre"));
        g.setNivel(request.getParameter("nivel"));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        if (id == 0) {
            dao.agregar(g); // 🆕 CREAR NUEVO GRADO
            System.out.println("✅ Nuevo grado creado: " + g.getNombre() + " (Nivel: " + g.getNivel() + ")");
        } else {
            g.setId(id);
            dao.actualizar(g); // ✏️ ACTUALIZAR GRADO EXISTENTE
            System.out.println("✅ Grado actualizado: " + g.getNombre() + " (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA PRINCIPAL DE GRADOS
        response.sendRedirect("GradoServlet");
    }
}