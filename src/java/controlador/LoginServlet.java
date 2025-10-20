/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    // Constantes para el control de intentos
    private static final int MAX_INTENTOS = 3;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 1;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ✅ CAPTCHA activado
    private static final boolean CAPTCHA_ACTIVADO = true;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ FIJAR CODIFICACIÓN UTF-8 AL INICIO
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String captchaInput = request.getParameter("captchaInput");
        String captchaHidden = request.getParameter("captchaHidden");

        System.out.println("=========================================");
        System.out.println("🎯 MÉTODO doPost INICIADO");
        System.out.println("🔐 INICIO PROCESO LOGIN CON CAPTCHA");
        System.out.println("📧 Usuario: " + user);
        System.out.println("🔑 Longitud password: " + (pass != null ? pass.length() : "null"));
        System.out.println("📝 CAPTCHA recibido: " + captchaInput);
        System.out.println("📝 CAPTCHA esperado: " + captchaHidden);

        try {
            // ✅ VALIDACIÓN CAPTCHA CON MÁS DETALLES
            if (CAPTCHA_ACTIVADO) {
                System.out.println("🔍 Validando CAPTCHA...");
                System.out.println("   - CaptchaHidden: '" + captchaHidden + "'");
                System.out.println("   - CaptchaInput: '" + captchaInput + "'");
                System.out.println("   - Son iguales: " + (captchaHidden != null && captchaInput != null && captchaHidden.equals(captchaInput)));
                
                if (captchaHidden == null || captchaInput == null) {
                    System.out.println("❌ CAPTCHA NULL - Hidden: " + (captchaHidden == null) + ", Input: " + (captchaInput == null));
                    response.getWriter().write("{\"success\": false, \"error\": \"Código de verificación requerido\"}");
                    return;
                }
                
                // ✅ COMPARACIÓN CASE-SENSITIVE (sin IgnoreCase)
                if (!captchaHidden.equals(captchaInput)) {
                    System.out.println("❌ CAPTCHA INCORRECTO - Acceso denegado");
                    response.getWriter().write("{\"success\": false, \"error\": \"Código de verificación incorrecto\"}");
                    return;
                } else {
                    System.out.println("✅ CAPTCHA VALIDADO CORRECTAMENTE");
                }
            } else {
                System.out.println("⚠️  CAPTCHA DESACTIVADO");
            }

            // 1. Desbloquear usuarios expirados
            System.out.println("🔄 Desbloqueando usuarios expirados...");
            usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);
            
            // 2. Verificar si el usuario está bloqueado
            System.out.println("🔍 Verificando estado de bloqueo para: " + user);
            if (usuarioDAO.estaBloqueado(user)) {
                System.out.println("🚫 USUARIO BLOQUEADO EN BD: " + user);
                
                long tiempoRestante = calcularTiempoRestanteBloqueo(user);
                System.out.println("⏰ Tiempo restante bloqueo: " + tiempoRestante + "ms");
                
                response.getWriter().write("{\"success\": false, \"error\": \"Usuario bloqueado. Intente más tarde.\"}");
                return;
            } else {
                System.out.println("✅ Usuario no está bloqueado");
            }

            // 3. VERIFICAR CREDENCIALES CON BCRYPT
            System.out.println("🔐 Verificando credenciales con BCrypt...");
            boolean credencialesValidas = usuarioDAO.verificarCredenciales(user, pass);

            if (credencialesValidas) {
                System.out.println("✅ CREDENCIALES VÁLIDAS");
                
                // Login exitoso - resetear contadores
                usuarioDAO.resetearIntentosUsuario(user);
                System.out.println("🔄 Contadores de intentos reseteados");
                
                // Obtener el usuario completo
                Usuario usuario = usuarioDAO.obtenerPorUsername(user);
                System.out.println("👤 Usuario obtenido de BD: " + (usuario != null ? "SI" : "NO"));
                
                if (usuario != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", user);
                    session.setAttribute("rol", usuario.getRol());

                    System.out.println("🎯 Rol detectado: " + usuario.getRol());
                    System.out.println("💾 Sesión creada - Usuario: " + user + ", Rol: " + usuario.getRol());

                    // Determinar redirección según el rol
                    String redirectUrl = determinarRedireccion(usuario.getRol(), user, request, response);
                    
                    if (redirectUrl != null) {
                        System.out.println("➡️ REDIRIGIENDO A: " + redirectUrl);
                        response.getWriter().write("{\"success\": true, \"redirect\": \"" + redirectUrl + "\"}");
                    }
                    return;
                    
                } else {
                    System.out.println("❌ ERROR: Usuario autenticado pero no encontrado en BD");
                    response.getWriter().write("{\"success\": false, \"error\": \"Error del sistema. Contacte al administrador.\"}");
                    return;
                }

            } else {
                // Login fallido
                System.out.println("❌ CREDENCIALES INVÁLIDAS");
                usuarioDAO.incrementarIntentoFallido(user);
                int intentosRestantes = getIntentosRestantes(user);

                System.out.println("📊 Intentos fallidos incrementados. Restantes: " + intentosRestantes);

                if (intentosRestantes <= 0) {
                    System.out.println("🚫 BLOQUEANDO USUARIO POR MÁXIMOS INTENTOS");
                    usuarioDAO.bloquearUsuario(user);
                    
                    response.getWriter().write("{\"success\": false, \"error\": \"Usuario bloqueado por intentos fallidos.\"}");
                } else {
                    response.getWriter().write("{\"success\": false, \"error\": \"Credenciales incorrectas. Intentos restantes: " + intentosRestantes + "\"}");
                }
            }

        } catch (Exception e) {
            System.out.println("💥 ERROR CRÍTICO EN LOGIN:");
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"error\": \"Error del sistema. Intente nuevamente.\"}");
        } finally {
            System.out.println("🏁 FIN PROCESO LOGIN");
            System.out.println("=========================================");
        }
    }

    // Método para determinar la redirección según el rol
    private String determinarRedireccion(String rol, String user, HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        if ("admin".equalsIgnoreCase(rol)) {
            System.out.println("➡️ REDIRIGIENDO A DASHBOARD ADMIN");
            return "dashboard.jsp";

        } else if ("docente".equalsIgnoreCase(rol)) {
            System.out.println("👨‍🏫 BUSCANDO DOCENTE EN BD...");
            modelo.Profesor docente = new modelo.ProfesorDAO().obtenerPorUsername(user);

            if (docente != null) {
                System.out.println("✅ DOCENTE ENCONTRADO: " + docente.getNombres() + " " + docente.getApellidos());
                System.out.println("🔍 ID Docente: " + docente.getId());

                HttpSession session = request.getSession();
                session.setAttribute("docente", docente);
                System.out.println("💾 Docente guardado en sesión");

                // Obtener cursos del docente
                System.out.println("📚 BUSCANDO CURSOS PARA DOCENTE ID: " + docente.getId());
                java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                System.out.println("✅ CURSOS ENCONTRADOS: " + misCursos.size());

                // Guardar cursos en sesión para el dashboard
                session.setAttribute("misCursos", misCursos);
                
                return "docenteDashboard.jsp";
                
            } else {
                System.out.println("❌ NO SE ENCONTRÓ DOCENTE PARA USERNAME: " + user);
                return "index.jsp?error=sin_docente";
            }
            
        } else if ("padre".equalsIgnoreCase(rol)) {
            System.out.println("👨‍👧‍👦 BUSCANDO PADRE EN BD...");
            modelo.Padre padre = new modelo.PadreDAO().obtenerPorUsername(user);
            
            if (padre != null) {
                System.out.println("✅ PADRE ENCONTRADO: " + padre.getAlumnoNombre());
                request.getSession().setAttribute("padre", padre);
                return "padreDashboard.jsp";
            } else {
                System.out.println("❌ NO SE ENCONTRÓ PADRE PARA USERNAME: " + user);
                return "index.jsp?error=padre_invalido";
            }
            
        } else {
            System.out.println("❌ ROL DESCONOCIDO: " + rol);
            return "index.jsp?error=3";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ✅ FIJAR CODIFICACIÓN UTF-8 TAMBIÉN EN GET
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        System.out.println("🔍 GET Request - Acción: " + accion);

        // Endpoint para verificar estado de bloqueo
        if ("verificarBloqueo".equals(accion)) {
            String username = request.getParameter("username");
            System.out.println("🔐 Verificando bloqueo para: " + username);
            
            if (username != null) {
                try {
                    usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);
                    boolean bloqueado = usuarioDAO.estaBloqueado(username);
                    
                    System.out.println("📊 Estado bloqueo: " + bloqueado);
                    
                    response.getWriter().write("{\"bloqueado\": " + bloqueado + "}");
                    return;
                } catch (Exception e) {
                    System.out.println("❌ Error verificando bloqueo: " + e.getMessage());
                    response.getWriter().write("{\"bloqueado\": false}");
                    return;
                }
            }
        }

        // Endpoint para verificar fortaleza de contraseña
        if ("verificarPassword".equals(accion)) {
            String password = request.getParameter("password");
            System.out.println("🔐 Verificando fortaleza password");
            
            if (password != null) {
                try {
                    boolean esFuerte = ValidacionContraseña.esPasswordFuerte(password);
                    String mensaje = esFuerte ? "Contraseña segura" : ValidacionContraseña.obtenerRequisitosPassword();
                    
                    System.out.println("📊 Password fuerte: " + esFuerte);
                    
                    response.getWriter().write("{\"esFuerte\": " + esFuerte + ", \"mensaje\": \"" + mensaje + "\"}");
                    return;
                } catch (Exception e) {
                    System.out.println("❌ Error validando password: " + e.getMessage());
                    response.getWriter().write("{\"esFuerte\": false, \"mensaje\": \"Error al validar contraseña\"}");
                    return;
                }
            }
        }

        if ("dashboard".equalsIgnoreCase(accion)) {
            String user = (String) session.getAttribute("usuario");
            System.out.println("🏠 Dashboard solicitado por: " + user);

            if (user == null) {
                System.out.println("❌ Usuario no en sesión - Redirigiendo a login");
                response.sendRedirect("index.jsp");
                return;
            }

            String rol = (String) session.getAttribute("rol");
            System.out.println("🎯 Rol en sesión: " + rol);

            if ("docente".equalsIgnoreCase(rol)) {
                modelo.Profesor docente = (modelo.Profesor) session.getAttribute("docente");
                if (docente != null) {
                    System.out.println("📚 [GET] Buscando cursos para docente ID: " + docente.getId());
                    java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                    System.out.println("✅ [GET] Cursos encontrados: " + misCursos.size());

                    request.setAttribute("misCursos", misCursos);
                    System.out.println("➡️ [GET] Forward a docenteDashboard.jsp");
                    request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
                    return;
                } else {
                    System.out.println("❌ [GET] Docente no encontrado en sesión");
                }
            }

            System.out.println("➡️ Redirigiendo por error sin docente");
            response.sendRedirect("index.jsp?error=sin_docente");
        }
    }

    // Métodos auxiliares
    private int getIntentosRestantes(String username) {
        modelo.Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null) {
            int intentosFallidos = usuario.getIntentosFallidos();
            int restantes = Math.max(0, MAX_INTENTOS - intentosFallidos);
            System.out.println("📊 Intentos fallidos: " + intentosFallidos + ", Restantes: " + restantes);
            return restantes;
        }
        System.out.println("📊 Usuario no encontrado, intentos restantes: " + MAX_INTENTOS);
        return MAX_INTENTOS;
    }

    private long calcularTiempoRestanteBloqueo(String username) {
        modelo.Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null && usuario.getFechaBloqueo() != null) {
            long tiempoTranscurrido = System.currentTimeMillis() - usuario.getFechaBloqueo().getTime();
            long tiempoTotalBloqueo = TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
            long restante = Math.max(0, tiempoTotalBloqueo - tiempoTranscurrido);
            System.out.println("⏰ Tiempo bloqueo - Transcurrido: " + tiempoTranscurrido + "ms, Restante: " + restante + "ms");
            return restante;
        }
        System.out.println("⏰ Tiempo bloqueo por defecto: " + (TIEMPO_BLOQUEO_MINUTOS * 60 * 1000) + "ms");
        return TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
    }
}