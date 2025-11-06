/*
 * SERVLET PARA ADMINISTRACIÓN DE DATOS DE PROFESORES
 * 
 * Funcionalidades: CRUD completo de profesores, asignación a cursos
 * Roles: Administrador (gestión completa)
 * Integración: Relación con cursos y usuarios del sistema
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Profesor;
import modelo.ProfesorDAO;

@WebServlet("/ProfesorServlet")
public class ProfesorServlet extends HttpServlet {

    // 👨‍🏫 DAO PARA OPERACIONES CON LA TABLA DE PROFESORES
    ProfesorDAO dao = new ProfesorDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y GESTIÓN DE PROFESORES
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los profesores (acción por defecto)
     * - nuevo: Formulario para crear nuevo profesor
     * - editar: Formulario para modificar profesor existente
     * - eliminar: Eliminar profesor del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // 📋 ACCIÓN POR DEFECTO: LISTAR TODOS LOS PROFESORES
        if (accion == null || accion.equals("listar")) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("profesores.jsp").forward(request, response);
            return;
        }

        // 🎯 EJECUTAR ACCIÓN ESPECÍFICA SEGÚN PARÁMETRO
        switch (accion) {
            case "editar":
                // ✏️ CARGAR FORMULARIO DE EDICIÓN DE PROFESOR
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Profesor p = dao.obtenerPorId(idEditar);
                request.setAttribute("profesor", p);
                request.getRequestDispatcher("profesorForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // 🗑️ ELIMINAR PROFESOR DEL SISTEMA
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("ProfesorServlet");
                break;
                
            case "nuevo":
                // ➕ MOSTRAR FORMULARIO PARA NUEVO PROFESOR
                request.getRequestDispatcher("profesorForm.jsp").forward(request, response);
                break;

            default:
                // 🔄 REDIRECCIÓN POR DEFECTO SI ACCIÓN NO RECONOCIDA
                response.sendRedirect("ProfesorServlet");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR PROFESORES
     * 
     * Maneja tanto la creación de nuevos profesores como la actualización
     * de profesores existentes basado en el parámetro ID
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO PROFESOR CON DATOS DEL FORMULARIO
        Profesor p = new Profesor();
        p.setNombres(request.getParameter("nombres"));
        p.setApellidos(request.getParameter("apellidos"));
        p.setCorreo(request.getParameter("correo"));
        p.setEspecialidad(request.getParameter("especialidad"));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        if (id == 0) {
            dao.agregar(p); // 🆕 CREAR NUEVO REGISTRO
            System.out.println("✅ Nuevo profesor creado: " + p.getNombres() + " " + p.getApellidos());
        } else {
            p.setId(id);
            dao.actualizar(p); // ✏️ ACTUALIZAR REGISTRO EXISTENTE
            System.out.println("✅ Profesor actualizado: " + p.getNombres() + " " + p.getApellidos() + " (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA PRINCIPAL DE PROFESORES
        response.sendRedirect("ProfesorServlet");
    }
}