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
import modelo.Usuario; // IMPORTANTE: Agregar este import
import util.ValidacionContraseña; // ✅ MANTENER para el endpoint de validación

public class LoginServlet extends HttpServlet {

    // Constantes para el control de intentos
    private static final int MAX_INTENTOS = 3;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 1; // 1 minuto
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        System.out.println("🔐 Intento de login con usuario: " + user);

        try {
            // Primero, desbloquear usuarios expirados
            usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);
            
            // Verificar si el usuario está bloqueado en la base de datos
            if (usuarioDAO.estaBloqueado(user)) {
                System.out.println("🚫 Usuario bloqueado en BD: " + user);
                
                // Calcular tiempo restante de bloqueo
                long tiempoRestante = calcularTiempoRestanteBloqueo(user);
                request.setAttribute("tiempoRestante", tiempoRestante);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp?error=bloqueado");
                dispatcher.forward(request, response);
                return;
            }

            // ❌ ELIMINADO: Validación de contraseña fuerte en login
            // Los usuarios existentes con contraseñas débiles pueden loguearse normalmente

            // VERIFICAR CREDENCIALES CON BCRYPT
            boolean credencialesValidas = usuarioDAO.verificarCredenciales(user, pass);

            if (credencialesValidas) {
                // Login exitoso - resetear contadores en BD
                usuarioDAO.resetearIntentosUsuario(user);
                
                // Obtener el usuario completo para la sesión
                Usuario usuario = usuarioDAO.obtenerPorUsername(user);
                
                if (usuario != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", user);
                    session.setAttribute("rol", usuario.getRol());

                    System.out.println("✅ Usuario autenticado. Rol: " + usuario.getRol());

                    // Redirección según el rol
                    if ("admin".equalsIgnoreCase(usuario.getRol())) {
                        System.out.println("➡️ Redirigiendo a dashboard de admin");
                        response.sendRedirect("dashboard.jsp");
                        return;

                    } else if ("docente".equalsIgnoreCase(usuario.getRol())) {
                        System.out.println("👨‍🏫 Buscando docente con username: " + user);
                        modelo.Profesor docente = new modelo.ProfesorDAO().obtenerPorUsername(user);

                        if (docente != null) {
                            System.out.println("✅ Docente encontrado: " + docente.getNombres() + " " + docente.getApellidos());
                            System.out.println("🔍 ID del docente: " + docente.getId());

                            session.setAttribute("docente", docente);

                            // DEBUG: Obtener y mostrar cursos
                            System.out.println("📚 Buscando cursos para el docente ID: " + docente.getId());
                            java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                            System.out.println("✅ Cursos encontrados: " + misCursos.size());

                            for (modelo.Curso curso : misCursos) {
                                System.out.println("   - Curso: " + curso.getNombre() + " (ID: " + curso.getId() + ")");
                            }

                            // ✅ CORRECCIÓN: Mantener en request y hacer forward
                            request.setAttribute("misCursos", misCursos);
                            request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
                        } else {
                            System.out.println("❌ No se encontró docente para el username: " + user);
                            response.sendRedirect("index.jsp?error=sin_docente");
                        }
                        return;
                    } else if ("padre".equalsIgnoreCase(usuario.getRol())) {
                        System.out.println("👨‍👧‍👦 Buscando padre con username: " + user);
                        modelo.Padre padre = new modelo.PadreDAO().obtenerPorUsername(user);
                        if (padre != null) {
                            System.out.println("✅ Padre encontrado: " + padre.getAlumnoNombre());
                            session.setAttribute("padre", padre);
                            response.sendRedirect("padreDashboard.jsp");
                        } else {
                            System.out.println("❌ No se encontró padre para el username: " + user);
                            response.sendRedirect("index.jsp?error=padre_invalido");
                        }
                        return;
                    } else {
                        System.out.println("❌ Rol desconocido: " + usuario.getRol());
                        response.sendRedirect("index.jsp?error=3");
                        return;
                    }
                } else {
                    System.out.println("❌ Error: Usuario autenticado pero no encontrado en BD");
                    response.sendRedirect("index.jsp?error=2");
                    return;
                }

            } else {
                // Login fallido - incrementar intentos en BD
                usuarioDAO.incrementarIntentoFallido(user);
                int intentosRestantes = getIntentosRestantes(user);

                System.out.println("❌ Credenciales inválidas para usuario: " + user
                        + ". Intentos restantes: " + intentosRestantes);

                if (intentosRestantes <= 0) {
                    usuarioDAO.bloquearUsuario(user);
                    
                    // Calcular tiempo restante para mostrar en la página
                    long tiempoRestante = TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
                    request.setAttribute("tiempoRestante", tiempoRestante);
                    
                    RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp?error=bloqueado");
                    dispatcher.forward(request, response);
                } else {
                    response.sendRedirect("index.jsp?error=1&intentos=" + intentosRestantes);
                }
            }

        } catch (Exception e) {
            System.out.println("💥 Error en el login:");
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=2");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        // Nuevo endpoint para verificar estado de bloqueo
        if ("verificarBloqueo".equals(accion)) {
            String username = request.getParameter("username");
            if (username != null) {
                try {
                    usuarioDAO.desbloquearUsuariosExpirados(TIEMPO_BLOQUEO_MINUTOS);
                    boolean bloqueado = usuarioDAO.estaBloqueado(username);
                    
                    response.setContentType("application/json");
                    response.getWriter().write("{\"bloqueado\": " + bloqueado + "}");
                    return;
                } catch (Exception e) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"bloqueado\": false}");
                    return;
                }
            }
        }

        // ✅ MANTENER: Endpoint para verificar fortaleza de contraseña en tiempo real (para usuarioForm.jsp)
        if ("verificarPassword".equals(accion)) {
            String password = request.getParameter("password");
            if (password != null) {
                try {
                    boolean esFuerte = ValidacionContraseña.esPasswordFuerte(password);
                    String mensaje = esFuerte ? "Contraseña segura" : ValidacionContraseña.obtenerRequisitosPassword();
                    
                    response.setContentType("application/json");
                    response.getWriter().write("{\"esFuerte\": " + esFuerte + ", \"mensaje\": \"" + mensaje + "\"}");
                    return;
                } catch (Exception e) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"esFuerte\": false, \"mensaje\": \"Error al validar contraseña\"}");
                    return;
                }
            }
        }

        if ("dashboard".equalsIgnoreCase(accion)) {
            String user = (String) session.getAttribute("usuario");

            if (user == null) {
                response.sendRedirect("index.jsp");
                return;
            }

            String rol = (String) session.getAttribute("rol");

            if ("docente".equalsIgnoreCase(rol)) {
                modelo.Profesor docente = (modelo.Profesor) session.getAttribute("docente");
                if (docente != null) {
                    System.out.println("📚 [doGet] Buscando cursos para docente ID: " + docente.getId());
                    java.util.List<modelo.Curso> misCursos = new modelo.CursoDAO().listarPorProfesor(docente.getId());
                    System.out.println("✅ [doGet] Cursos encontrados: " + misCursos.size());

                    // ✅ Pasar a request para el forward final
                    request.setAttribute("misCursos", misCursos);
                    request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
                    return;
                }
            }

            response.sendRedirect("index.jsp?error=sin_docente");
        }
    }

    // Métodos auxiliares
    private int getIntentosRestantes(String username) {
        modelo.Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null) {
            int intentosFallidos = usuario.getIntentosFallidos();
            return Math.max(0, MAX_INTENTOS - intentosFallidos);
        }
        return MAX_INTENTOS;
    }

    private long calcularTiempoRestanteBloqueo(String username) {
        modelo.Usuario usuario = usuarioDAO.obtenerDatosBloqueo(username);
        if (usuario != null && usuario.getFechaBloqueo() != null) {
            long tiempoTranscurrido = System.currentTimeMillis() - usuario.getFechaBloqueo().getTime();
            long tiempoTotalBloqueo = TIEMPO_BLOQUEO_MINUTOS * 60 * 1000;
            return Math.max(0, tiempoTotalBloqueo - tiempoTranscurrido);
        }
        return TIEMPO_BLOQUEO_MINUTOS * 60 * 1000; // Tiempo completo por defecto
    }
}