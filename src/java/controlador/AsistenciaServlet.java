package controlador;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Asistencia;
import modelo.AsistenciaDAO;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.Profesor;
import modelo.Padre;

public class AsistenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "ver";
        }

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol");

        try {
            switch (accion) {
                case "ver":
                    if ("docente".equals(rol)) {
                        verCursosDocente(request, response);
                    } else if ("padre".equals(rol)) {
                        verAsistenciasPadre(request, response);
                    }
                    break;
                case "verCurso":
                    verAsistenciasCurso(request, response);
                    break;
                case "registrar":
                    mostrarFormRegistro(request, response);
                    break;
                case "reportes":
                    mostrarReportes(request, response);
                    break;
                case "verPadre":
                    verAsistenciasPadreDetalle(request, response);
                    break;
                default:
                    response.sendRedirect("dashboard.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "registrar";
        }

        try {
            switch (accion) {
                case "registrar":
                    registrarAsistencia(request, response);
                    break;
                case "registrarGrupal":
                    registrarAsistenciaGrupal(request, response);
                    break;
                default:
                    response.sendRedirect("AsistenciaServlet?accion=ver");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void verCursosDocente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");
        
        if (docente != null) {
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());
            request.setAttribute("misCursos", cursos);
        }
        
        request.getRequestDispatcher("asistenciasDocente.jsp").forward(request, response);
    }

    private void verAsistenciasCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int cursoId = Integer.parseInt(request.getParameter("curso_id"));
        String fecha = request.getParameter("fecha");
        
        if (fecha == null) {
            fecha = java.time.LocalDate.now().toString();
        }
        
        AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
        List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorCursoTurnoFecha(cursoId, 1, fecha);
        
        request.setAttribute("asistencias", asistencias);
        request.setAttribute("cursoId", cursoId);
        request.setAttribute("fecha", fecha);
        
        request.getRequestDispatcher("asistenciasCurso.jsp").forward(request, response);
    }

    private void verAsistenciasPadre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");
        
        if (padre != null) {
            int alumnoId = padre.getAlumnoId();
            int mes = Integer.parseInt(request.getParameter("mes") != null ? 
                request.getParameter("mes") : String.valueOf(java.time.LocalDate.now().getMonthValue()));
            int anio = Integer.parseInt(request.getParameter("anio") != null ? 
                request.getParameter("anio") : String.valueOf(java.time.LocalDate.now().getYear()));
            
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorAlumnoTurno(alumnoId, 1, mes, anio);
            Map<String, Object> resumen = asistenciaDAO.obtenerResumenAsistenciaAlumnoTurno(alumnoId, 1, mes, anio);
            
            request.setAttribute("asistencias", asistencias);
            request.setAttribute("resumen", resumen);
            request.setAttribute("mes", mes);
            request.setAttribute("anio", anio);
        }
        
        request.getRequestDispatcher("asistenciasPadre.jsp").forward(request, response);
    }

    private void verAsistenciasPadreDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verAsistenciasPadre(request, response);
    }

    private void mostrarFormRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");
        
        if (docente != null) {
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());
            request.setAttribute("cursos", cursos);
        }
        
        request.getRequestDispatcher("registrarAsistencia.jsp").forward(request, response);
    }

    private void mostrarReportes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("reportesAsistencia.jsp").forward(request, response);
    }

    private void registrarAsistencia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("AsistenciaServlet?accion=ver&mensaje=Asistencia registrada");
    }

    private void registrarAsistenciaGrupal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("AsistenciaServlet?accion=ver&mensaje=Asistencias registradas");
    }
}