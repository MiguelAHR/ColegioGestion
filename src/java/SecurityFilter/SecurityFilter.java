/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        
        if (session != null && session.getAttribute("usuario") != null) {
            String rol = (String) session.getAttribute("rol"); 
            String requestURI = httpRequest.getRequestURI(); 

            if (requestURI.contains("/dashboard.jsp") && rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/usuarios.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp");
            }
            
            if (requestURI.contains("/asistenciasDocente.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/docenteDashboard.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/cursos.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/cursoForm.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
             if (requestURI.contains("/grados.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
             
            if (requestURI.contains("/gradoForm.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            if (requestURI.contains("/justificacionesPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/albumPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/alumnoForm.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/asistenciasCurso.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/asistenciasPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/justificacionesPendientes.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/justificarAusencia.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/notasDocente.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/notasPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/notaForm.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            if (requestURI.contains("/observacionesDocente.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/observacionesPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/profesorForm.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/profesores.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/registrarAsistencia.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/reporteAsistencia.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/tareaForm.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/tareaDocente.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/tareaPadre.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/uploadImage.jsp") && !rol.equals("padre")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
             if (requestURI.contains("/usuarioForm.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
             
             if (requestURI.contains("/usuarios.jsp") && !rol.equals("admin")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            if (requestURI.contains("/verAlumnos.jsp") && !rol.equals("docente")) {
                httpResponse.sendRedirect("acceso_denegado.jsp"); 
                return;
            }
            
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect("login.jsp");
        }
    }

    public void destroy() {
    }
}

