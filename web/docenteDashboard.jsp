<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Profesor, modelo.Curso, java.util.List" %>
<%@ page import="modelo.AsistenciaDAO, java.util.Map" %>

<%
    Profesor docente = (Profesor) session.getAttribute("docente");
    List<Curso> cursos = (List<Curso>) request.getAttribute("misCursos");
    
    // Obtener estadísticas de asistencias para cada curso
    AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    Map<Integer, Map<String, Object>> estadisticasCursos = new java.util.HashMap<>();
    
    if (cursos != null) {
        for (Curso curso : cursos) {
            // Obtener resumen del mes actual
            int mesActual = java.time.LocalDate.now().getMonthValue();
            int anioActual = java.time.LocalDate.now().getYear();
            
            // Aquí necesitarías un método para obtener estadísticas por curso
            // Por ahora usaremos datos de ejemplo
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalAlumnos", 25);
            stats.put("presentesHoy", 22);
            stats.put("ausentesHoy", 3);
            stats.put("porcentajeAsistencia", 88.0);
            
            estadisticasCursos.put(curso.getId(), stats);
        }
    }

    if (docente == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel del Docente</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="assets/css/estilos.css">

        <style>
            body {
                background-image: url('assets/img/fondo_dashboard_docente.jpg');
                background-size: 100% 100%;
                background-position: center;
                background-attachment: fixed;
                height: 100vh;
            }
            .header-bar {
                background-color: #111;
                color: white;
                padding: 15px 30px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .nav-links a {
                color: white;
                margin-right: 25px;
                text-decoration: none;
            }
            .nav-links a:hover {
                text-decoration: underline;
            }
            .card-box {
                background-color: #fff8ed;
                border-radius: 15px;
                padding: 20px;
                text-align: center;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                transition: transform 0.2s;
            }
            .card-box:hover {
                transform: scale(1.03);
            }
            .asistencia-badge {
                font-size: 0.8rem;
                padding: 4px 8px;
                border-radius: 10px;
            }
            .btn-asistencia {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                transition: all 0.3s ease;
            }
            .btn-asistencia:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
        </style>
    </head>
    <body>

        <div class="header-bar">
            <div class="nav-links">
                <img src="assets/img/logosa.png" alt="Logo" style="width: 30px; height: auto; margin-right: 10px;" />
                <span class="fw-bold fs-6">Colegio SA</span>
            </div>
            <div>
                <span><%= docente.getNombres()%> <%= docente.getApellidos()%></span>
                <a href="LogoutServlet" class="btn btn-outline-light btn-sm ms-3">Cerrar sesión</a>
            </div>
        </div>

        <div class="container mt-5">
            <h2 class="text-center fw-bold mb-5">Panel del Docente</h2>

            <!-- Sección de Asistencias Rápidas -->
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">📊 Módulo de Asistencias</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-4">
                            <a href="AsistenciaServlet?accion=registrar" class="btn btn-asistencia btn-block mb-2 w-100">
                                📝 Tomar Asistencia
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="AsistenciaServlet?accion=reportes" class="btn btn-info btn-block mb-2 w-100">
                                📈 Ver Reportes
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="JustificacionServlet?accion=pending" class="btn btn-warning btn-block mb-2 w-100">
                                ⏳ Justificaciones Pendientes
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <%
                    if (cursos != null && !cursos.isEmpty()) {
                        for (Curso c : cursos) {
                            Map<String, Object> stats = estadisticasCursos.get(c.getId());
                %>
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2"><%= c.getNombre()%></h5>
                        <p><strong>Grado:</strong> <%= c.getGradoNombre()%></p>
                        
                        <!-- Información de Asistencia -->
                        <% if (stats != null) { %>
                        <div class="mb-3 p-2 bg-light rounded">
                            <small class="text-muted">Asistencia Hoy:</small><br>
                            <span class="badge bg-success"><%= stats.get("presentesHoy") %> Presentes</span>
                            <span class="badge bg-danger"><%= stats.get("ausentesHoy") %> Ausentes</span>
                            <div class="mt-1">
                                <small><strong><%= stats.get("porcentajeAsistencia") %>%</strong> de asistencia</small>
                            </div>
                        </div>
                        <% } %>
                        
                        <div class="d-grid gap-2 mt-3">
                            <a href="TareaServlet?accion=ver&curso_id=<%= c.getId()%>" class="btn btn-outline-secondary btn-sm">Gestionar Tareas</a>
                            <a href="NotaServlet?curso_id=<%= c.getId()%>" class="btn btn-outline-primary btn-sm">Gestionar Notas</a>
                            <a href="ObservacionServlet?accion=listar&curso_id=<%= c.getId()%>" class="btn btn-outline-success btn-sm">Gestionar Observaciones</a>
                            <!-- NUEVO BOTÓN PARA ASISTENCIAS -->
                            <a href="AsistenciaServlet?accion=verCurso&curso_id=<%= c.getId()%>" class="btn btn-outline-info btn-sm">📊 Gestionar Asistencias</a>
                        </div>
                    </div>
                </div>
                <%
                    }
                } else {
                %>
                <div class="text-center">
                    <p>No tienes cursos asignados.</p>
                </div>
                <%
                    }
                %>
            </div>
        </div>

        <footer class="bg-dark text-white py-2">
            <div class="container text-center text-md-start">
                <div class="row">
                    <div class="col-md-4 mb-0">
                        <div class="logo-container text-center">
                            <img src="assets/img/logosa.png" alt="Logo" class="img-fluid mb-1" width="80" height="auto">
                            <p class="fs-6">"Líderes en educación de calidad al más alto nivel"</p>
                        </div>
                    </div>

                    <div class="col-md-4 mb-0">
                        <h5 class="fs-8">Contacto:</h5>
                        <p class="fs-6">Dirección: Av. El Sol 461, San Juan de Lurigancho 15434</p>
                        <p class="fs-6">Teléfono: 987654321</p>
                        <p class="fs-6">Correo: colegiosanantonio@gmail.com</p>
                    </div>

                    <div class="col-md-4 mb-0">
                        <h5 class="fs-8">Síguenos:</h5>
                        <a href="https://www.facebook.com/" class="text-white d-block fs-6">Facebook</a>
                        <a href="https://www.instagram.com/" class="text-white d-block fs-6">Instagram</a>
                        <a href="https://twitter.com/" class="text-white d-block fs-6">Twitter</a>
                        <a href="https://www.youtube.com/" class="text-white d-block fs-6">YouTube</a>
                    </div>
                </div>

                <div class="text-center mt-0">
                    <p class="fs-6">&copy; 2025 Colegio SA - Todos los derechos reservados</p>
                </div>
            </div>
        </footer>
    </body>
</html>