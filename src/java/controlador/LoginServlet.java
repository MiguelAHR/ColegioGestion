package controlador;

import conexion.Conexion;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import modelo.UsuarioDAO;
import modelo.Usuario;
import util.ValidacionContraseña;

public class LoginServlet extends HttpServlet {

    private static final int MAX_INTENTOS = 3;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 1;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String user = request.getParameter("username");
        String hashedPasswordFromFrontend = request.getParameter("password");
        String captchaInput = request.getParameter("captchaInput");
        String captchaHidden = request.getParameter("captchaHidden");

        System.out.println("🔐 Intento de login con usuario: " + user);

        try {
            // Primero desbloquear usuarios expirados
            usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);

            // Verificar si el usuario está bloqueado
            if (usuarioDAO.estaBloqueado(user)) {
                System.out.println("🚫 Usuario bloqueado detectado: " + user);
                long tiempoRestante = calcularTiempoRestanteBloqueo(user);
                String json = "{\"success\": false, \"error\": \"Usuario bloqueado. Intente más tarde.\", \"tipoError\": \"bloqueado\", \"tiempoRestante\": " + tiempoRestante + "}";
                response.getWriter().write(json);
                return;
            }

            // ✅ PRIMERO VALIDAR CREDENCIALES SIN CAPTCHA
            boolean credencialesCorrectas = usuarioDAO.verificarCredencialesConHash(user, hashedPasswordFromFrontend);
            
            if (!credencialesCorrectas) {
                System.out.println("❌ Credenciales incorrectas para: " + user);
                manejarCredencialesInvalidas(user, response);
                return;
            }

            // ✅ SI LAS CREDENCIALES SON CORRECTAS, ENTONCES VALIDAR CAPTCHA
            System.out.println("✅ Credenciales correctas, validando CAPTCHA para: " + user);
            
            if (captchaInput == null || captchaHidden == null || !captchaInput.trim().equals(captchaHidden.trim())) {
                System.out.println("❌ CAPTCHA incorrecto o no proporcionado para: " + user);
                String json = "{\"success\": false, \"error\": \"Código de verificación requerido\", \"tipoError\": \"requiere_captcha\"}";
                response.getWriter().write(json);
                return;
            }

            System.out.println("✅ CAPTCHA validado correctamente");

            // Si llegamos aquí, login exitoso
            Usuario usuario = usuarioDAO.obtenerPorUsername(user);
            if (usuario == null) {
                enviarJson(response, false, "Error del sistema. Contacte al administrador.", "sistema");
                return;
            }

            // Resetear intentos en login exitoso
            usuarioDAO.resetearIntentosUsuario(user);
            
            HttpSession session = request.getSession();
            session.setAttribute("usuario", user);
            session.setAttribute("rol", usuario.getRol());

            String redirectUrl = determinarRedireccion(usuario.getRol(), user, request, response);
            if (redirectUrl != null) {
                enviarJson(response, true, redirectUrl);
            } else {
                enviarJson(response, false, "No se pudo determinar la redirección", "redireccion");
            }

        } catch (Exception e) {
            System.err.println("💥 Error en el login:");
            e.printStackTrace();
            enviarJson(response, false, "Error interno del servidor", "sistema");
        }
    }

    private void manejarCredencialesInvalidas(String username, HttpServletResponse response) throws IOException {
        usuarioDAO.incrementarIntentoFallido(username);
        int intentosRestantes = getIntentosRestantes(username);

        System.out.println("📊 Intentos restantes para " + username + ": " + intentosRestantes);

        if (intentosRestantes <= 0) {
            usuarioDAO.bloquearUsuario(username);
            long tiempoRestante = TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
            String json = "{\"success\": false, \"error\": \"Usuario bloqueado por intentos fallidos.\", \"tipoError\": \"bloqueado\", \"tiempoRestante\": " + tiempoRestante + "}";
            response.getWriter().write(json);
        } else {
            String json = "{\"success\": false, \"error\": \"Credenciales incorrectas\", \"tipoError\": \"credenciales\", \"intentosRestantes\": " + intentosRestantes + "}";
            response.getWriter().write(json);
        }
    }

    private int getIntentosRestantes(String username) {
        Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null) {
            int intentosUsados = usuario.getIntentosFallidos();
            int restantes = MAX_INTENTOS - intentosUsados;
            System.out.println("🔢 Intentos usados: " + intentosUsados + ", Restantes: " + restantes);
            return Math.max(0, restantes);
        }
        return MAX_INTENTOS;
    }

    private long calcularTiempoRestanteBloqueo(String username) {
        Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null && usuario.getFechaBloqueo() != null) {
            long transcurrido = System.currentTimeMillis() - usuario.getFechaBloqueo().getTime();
            long total = TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
            long restante = Math.max(0, total - transcurrido);
            System.out.println("⏰ Tiempo bloqueo restante para " + username + ": " + restante + "ms");
            return restante;
        }
        return TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
    }

    private void enviarJson(HttpServletResponse response, boolean success, String mensaje) throws IOException {
        response.getWriter().write("{\"success\": " + success + ", \"redirect\": \"" + mensaje + "\"}");
    }

    private void enviarJson(HttpServletResponse response, boolean success, String mensaje, String tipoError) throws IOException {
        response.getWriter().write("{\"success\": " + success + ", \"error\": \"" + mensaje + "\", \"tipoError\": \"" + tipoError + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        System.out.println("📨 GET Request - Acción: " + accion);

        switch (accion != null ? accion : "") {
            case "verificarBloqueo":
                verificarBloqueo(request, response);
                break;
            case "verificarPassword":
                verificarPassword(request, response);
                break;
            case "dashboard":
                accederDashboard(session, request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }

    private void verificarBloqueo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        if (username == null) {
            response.getWriter().write("{\"bloqueado\": true}");
            return;
        }

        try {
            usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);
            boolean bloqueado = usuarioDAO.estaBloqueado(username);
            System.out.println("🔍 Verificación bloqueo para " + username + ": " + bloqueado);
            response.getWriter().write("{\"bloqueado\": " + bloqueado + "}");
        } catch (Exception e) {
            System.err.println("❌ Error verificando bloqueo: " + e.getMessage());
            response.getWriter().write("{\"bloqueado\": true}");
        }
    }

    private void verificarPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        if (password == null) {
            return;
        }

        try {
            boolean esFuerte = ValidacionContraseña.esPasswordFuerte(password);
            String mensaje = esFuerte ? "Contraseña segura" : ValidacionContraseña.obtenerRequisitosPassword();
            response.getWriter().write("{\"esFuerte\": " + esFuerte + ", \"mensaje\": \"" + mensaje + "\"}");
        } catch (Exception e) {
            System.err.println("❌ Error validando password: " + e.getMessage());
            response.getWriter().write("{\"esFuerte\": false, \"mensaje\": \"Error al validar contraseña\"}");
        }
    }

    private void accederDashboard(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = (String) session.getAttribute("usuario");
        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");
        if ("docente".equalsIgnoreCase(rol)) {
            modelo.Profesor docente = (modelo.Profesor) session.getAttribute("docente");
            if (docente != null) {
                java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                request.setAttribute("misCursos", misCursos);
                request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
                return;
            }
        } else if ("admin".equalsIgnoreCase(rol)) {
            response.sendRedirect("dashboard.jsp");
            return;
        } else if ("padre".equalsIgnoreCase(rol)) {
            response.sendRedirect("padreDashboard.jsp");
            return;
        }
        response.sendRedirect("index.jsp?error=rol_desconocido");
    }

    private String determinarRedireccion(String rol, String user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("📍 Determinando redireccion para rol: " + rol + ", usuario: " + user);

        if ("admin".equalsIgnoreCase(rol)) {
            return "dashboard.jsp";
        }

        if ("docente".equalsIgnoreCase(rol)) {
            System.out.println("👨‍🏫 Buscando datos del docente: " + user);
            modelo.Profesor docente = new modelo.ProfesorDAO().obtenerPorUsername(user);

            if (docente != null) {
                HttpSession session = request.getSession();
                session.setAttribute("docente", docente);
                System.out.println("✅ Docente encontrado: " + docente.getNombres() + " " + docente.getApellidos());

                java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                session.setAttribute("misCursos", misCursos);
                System.out.println("📚 Cursos cargados: " + (misCursos != null ? misCursos.size() : 0));

                return "docenteDashboard.jsp";
            } else {
                System.out.println("❌ No se encontró información del docente para: " + user);
                return "index.jsp?error=No se encontró información del docente";
            }
        }

        if ("padre".equalsIgnoreCase(rol)) {
            modelo.Padre padre = new modelo.PadreDAO().obtenerPorUsername(user);
            if (padre != null) {
                request.getSession().setAttribute("padre", padre);
                return "padreDashboard.jsp";
            } else {
                return "index.jsp?error=padre_invalido";
            }
        }

        System.out.println("⚠️ Rol no reconocido: " + rol);
        return "index.jsp?error=rol_no_reconocido";
    }
}