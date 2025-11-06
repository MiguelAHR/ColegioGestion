package SecurityFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class SecurityFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        System.out.println("SecurityFilter: Processing URI: " + requestURI);

        // Excluir páginas públicas y recursos estáticos del filtro
        if (requestURI.endsWith("login.jsp")
                || requestURI.endsWith("index.jsp")
                || requestURI.contains("/LoginServlet")
                || requestURI.endsWith("acceso_denegado.jsp")
                || requestURI.contains("/css/")
                || requestURI.contains("/js/")
                || requestURI.contains("/images/")
                || requestURI.contains(".css")
                || requestURI.contains(".js")
                || requestURI.contains(".png")
                || requestURI.contains(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // Si no hay sesión, redirigir al login
        if (session == null || session.getAttribute("usuario") == null) {
            System.out.println("SecurityFilter: No session, redirecting to login");
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");

        if (rol == null) {
            System.out.println("SecurityFilter: No role found, redirecting to login");
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        System.out.println("SecurityFilter: User role: " + rol);
        System.out.println("SecurityFilter: User role: " + rol + ", URI: " + requestURI);
        // Verificar permisos según el rol
        boolean accessGranted = checkAccess(rol, requestURI);

        if (!accessGranted) {
            System.out.println("SecurityFilter: Access DENIED for role: " + rol + " to: " + requestURI);
            httpResponse.sendRedirect(contextPath + "/acceso_denegado.jsp");
            return;
        }

        System.out.println("SecurityFilter: Access GRANTED for role: " + rol + " to: " + requestURI);
        chain.doFilter(request, response);
    }

    private boolean checkAccess(String rol, String requestURI) {
        System.out.println("SecurityFilter: Checking access for role " + rol + " to " + requestURI);
        // ADMIN - Acceso completo a funciones administrativas
        if ("admin".equals(rol)) {
            return requestURI.contains("/admin/")
                    || requestURI.contains("/usuarios.jsp")
                    || requestURI.contains("/usuarioForm.jsp")
                    || requestURI.contains("/cursos.jsp")
                    || requestURI.contains("/cursoForm.jsp")
                    || requestURI.contains("/profesores.jsp")
                    || requestURI.contains("/profesorForm.jsp")
                    || requestURI.contains("/gradoForm.jsp")
                    || requestURI.contains("/alumnoForm.jsp")
                    || requestURI.contains("/verAlumnos.jsp")
                    || requestURI.contains("/dashboard.jsp")
                    || requestURI.contains("/AlumnoServlet")
                    || requestURI.contains("/ProfesorServlet")
                    || requestURI.contains("/UsuarioServlet")
                    || requestURI.contains("/CursoServlet")
                    || requestURI.contains("/AsistenciaServlet");
        } // DOCENTE - Solo funciones de docente
        else if ("docente".equals(rol)) {
            // Primero verificar que NO sea una página administrativa
            if (requestURI.contains("/usuarios.jsp")
                    || requestURI.contains("/usuarioForm.jsp")
                    || requestURI.contains("/cursos.jsp")
                    || requestURI.contains("/cursoForm.jsp")
                    || requestURI.contains("/profesores.jsp")
                    || requestURI.contains("/profesorForm.jsp")
                    || requestURI.contains("/gradoForm.jsp")
                    || requestURI.contains("/alumnoForm.jsp")
                    || requestURI.contains("/dashboard.jsp")
                    || requestURI.contains("/admin/")
                    || requestURI.contains("/ProfesorServlet")
                    || requestURI.contains("/UsuarioServlet")
                    || requestURI.contains("/CursoServlet")) {
                return false;
            }

            // Permitir páginas específicas de docente INCLUYENDO AsistenciaServlet
            return requestURI.contains("/docente/")
                    || requestURI.contains("/asistenciasDocente.jsp")
                    || requestURI.contains("/docenteDashboard.jsp")
                    || requestURI.contains("/justificacionesPendientes.jsp")
                    || requestURI.contains("/notasDocente.jsp")
                    || requestURI.contains("/notaForm.jsp")
                    || requestURI.contains("/observacionesDocente.jsp")
                    || requestURI.contains("/registrarAsistencia.jsp")
                    || requestURI.contains("/reporteAsistencia.jsp")
                    || requestURI.contains("/tareaForm.jsp")
                    || requestURI.contains("/tareaDocente.jsp")
                    || requestURI.contains("/verAlumnos.jsp")
                    || requestURI.contains("/asistenciasCurso.jsp")
                    || requestURI.contains("/grados.jsp")
                    || requestURI.contains("/AsistenciaServlet")
                    || requestURI.contains("/TareaServlet")
                    || requestURI.contains("/ObservacionServlet")
                    || requestURI.contains("/JustificacionServlet")
                    || requestURI.contains("/AlumnoServlet");
        } // PADRE - Solo funciones de padre
        else if ("padre".equals(rol)) {
            // Primero verificar que NO sea una página administrativa o de docente
            if (requestURI.contains("/usuarios.jsp")
                    || requestURI.contains("/usuarioForm.jsp")
                    || requestURI.contains("/cursos.jsp")
                    || requestURI.contains("/cursoForm.jsp")
                    || requestURI.contains("/profesores.jsp")
                    || requestURI.contains("/profesorForm.jsp")
                    || requestURI.contains("/gradoForm.jsp")
                    || requestURI.contains("/alumnoForm.jsp")
                    || requestURI.contains("/dashboard.jsp")
                    || requestURI.contains("/admin/")
                    || requestURI.contains("/asistenciasDocente.jsp")
                    || requestURI.contains("/docenteDashboard.jsp")
                    || requestURI.contains("/justificacionesPendientes.jsp")
                    || requestURI.contains("/notasDocente.jsp")
                    || requestURI.contains("/notaForm.jsp")
                    || requestURI.contains("/observacionesDocente.jsp")
                    || requestURI.contains("/registrarAsistencia.jsp")
                    || requestURI.contains("/reporteAsistencia.jsp")
                    || requestURI.contains("/tareaForm.jsp")
                    || requestURI.contains("/tareaDocente.jsp")
                    || requestURI.contains("/verAlumnos.jsp")
                    || requestURI.contains("/asistenciasCurso.jsp")
                    || requestURI.contains("/grados.jsp")
                    || requestURI.contains("/docente/")
                    || requestURI.contains("/AlumnoServlet")
                    || requestURI.contains("/ProfesorServlet")
                    || requestURI.contains("/UsuarioServlet")
                    || requestURI.contains("/CursoServlet")
                    || requestURI.contains("/TareaServlet")
                    || requestURI.contains("/ObservacionServlet")) {
                return false;
            }

            // Solo permitir páginas específicas de padre
            return requestURI.contains("/padre/")
                    || requestURI.contains("/justificacionesPadre.jsp")
                    || requestURI.contains("/albumPadre.jsp")
                    || requestURI.contains("/asistenciasPadre.jsp")
                    || requestURI.contains("/justificarAusencia.jsp")
                    || requestURI.contains("/notasPadre.jsp")
                    || requestURI.contains("/observacionesPadre.jsp")
                    || requestURI.contains("/tareaPadre.jsp")
                    || requestURI.contains("/uploadImage.jsp")
                    || requestURI.contains("/JustificacionServlet")
                    || requestURI.contains("/NotasPadreServlet")
                    || requestURI.contains("/ObservacionesPadreServlet")
                    || requestURI.contains("/AsistenciaServlet")
                    || requestURI.contains("/TareasPadreServlet");
        }

        return false;
    }

    public void destroy() {
    }
}
