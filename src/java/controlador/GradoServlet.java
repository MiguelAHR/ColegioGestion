/*
 * SERVLET PARA GESTION DE GRADOS ACADEMICOS
 * 
 * Funcionalidades: CRUD completo de grados (niveles educativos)
 * Roles: Administrador
 * Integracion: Base para cursos y alumnos
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

    // DAO para operaciones con la tabla de grados
    GradoDAO dao = new GradoDAO();

    /**
     * METODO GET - CONSULTAS Y NAVEGACION DE GRADOS
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los grados (accion por defecto)
     * - editar: Formulario para modificar grado existente
     * - eliminar: Eliminar grado del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // Accion por defecto: listar todos los grados
        if (accion == null || accion.isEmpty()) {
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("grados.jsp").forward(request, response);
            return;
        }

        // Ejecutar accion especifica
        switch (accion) {
            case "editar":
                // Cargar formulario de edicion de grado
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Grado g = dao.obtenerPorId(idEditar);
                request.setAttribute("grado", g);
                request.getRequestDispatcher("gradoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // Eliminar grado del sistema
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("GradoServlet");
                break;

            default:
                // Redireccion por defecto
                response.sendRedirect("GradoServlet");
        }
    }

    /**
     * METODO POST - CREAR Y ACTUALIZAR GRADOS
     * 
     * Maneja el envio de formularios para crear nuevos grados
     * y actualizar grados existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Determinar si es creacion (id=0) o actualizacion (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // Construir objeto grado con datos del formulario
        Grado g = new Grado();
        g.setNombre(request.getParameter("nombre"));
        g.setNivel(request.getParameter("nivel"));

        // Ejecutar operacion en base de datos
        if (id == 0) {
            dao.agregar(g); // Crear nuevo grado
            System.out.println("Nuevo grado creado: " + g.getNombre() + " (Nivel: " + g.getNivel() + ")");
        } else {
            g.setId(id);
            dao.actualizar(g); // Actualizar grado existente
            System.out.println("Grado actualizado: " + g.getNombre() + " (ID: " + id + ")");
        }

        // Redirigir a la lista principal de grados
        response.sendRedirect("GradoServlet");
    }
}