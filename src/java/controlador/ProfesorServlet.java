/*
 * SERVLET PARA ADMINISTRACION DE DATOS DE PROFESORES
 * 
 * Funcionalidades: CRUD completo de profesores, asignacion a cursos
 * Roles: Administrador (gestion completa)
 * Integracion: Relacion con cursos y usuarios del sistema
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

    // DAO para operaciones con la tabla de profesores
    ProfesorDAO dao = new ProfesorDAO();

    /**
     * METODO GET - CONSULTAS Y GESTION DE PROFESORES
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los profesores (accion por defecto)
     * - nuevo: Formulario para crear nuevo profesor
     * - editar: Formulario para modificar profesor existente
     * - eliminar: Eliminar profesor del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // Accion por defecto: listar todos los profesores
        if (accion == null || accion.equals("listar")) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("profesores.jsp").forward(request, response);
            return;
        }

        // Ejecutar accion especifica segun parametro
        switch (accion) {
            case "editar":
                // Cargar formulario de edicion de profesor
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Profesor p = dao.obtenerPorId(idEditar);
                request.setAttribute("profesor", p);
                request.getRequestDispatcher("profesorForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // Eliminar profesor del sistema
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("ProfesorServlet");
                break;
                
            case "nuevo":
                // Mostrar formulario para nuevo profesor
                request.getRequestDispatcher("profesorForm.jsp").forward(request, response);
                break;

            default:
                // Redireccion por defecto si accion no reconocida
                response.sendRedirect("ProfesorServlet");
        }
    }

    /**
     * METODO POST - CREAR Y ACTUALIZAR PROFESORES
     * 
     * Maneja tanto la creacion de nuevos profesores como la actualizacion
     * de profesores existentes basado en el parametro ID
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Determinar si es creacion (id=0) o actualizacion (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // Construir objeto profesor con datos del formulario
        Profesor p = new Profesor();
        p.setNombres(request.getParameter("nombres"));
        p.setApellidos(request.getParameter("apellidos"));
        p.setCorreo(request.getParameter("correo"));
        p.setEspecialidad(request.getParameter("especialidad"));

        // Ejecutar operacion en base de datos
        if (id == 0) {
            dao.agregar(p); // Crear nuevo registro
            System.out.println("Nuevo profesor creado: " + p.getNombres() + " " + p.getApellidos());
        } else {
            p.setId(id);
            dao.actualizar(p); // Actualizar registro existente
            System.out.println("Profesor actualizado: " + p.getNombres() + " " + p.getApellidos() + " (ID: " + id + ")");
        }

        // Redirigir a la lista principal de profesores
        response.sendRedirect("ProfesorServlet");
    }
}