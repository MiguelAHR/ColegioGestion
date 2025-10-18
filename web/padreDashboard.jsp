<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Padre" %>
<%@ page import="modelo.ImageDAO, modelo.Imagen" %>
<%@ page import="modelo.AsistenciaDAO, java.util.Map" %>
<%@ page import="java.util.List" %>

<%
    Padre padre = (Padre) session.getAttribute("padre");
    if (padre == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    int alumnoId = padre.getAlumnoId();

    // Obtener resumen de asistencias del mes actual
    AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    int mesActual = java.time.LocalDate.now().getMonthValue();
    int anioActual = java.time.LocalDate.now().getYear();

    // Obtener resumen de asistencias (usaremos turno 1 por defecto)
    Map<String, Object> resumenAsistencia = asistenciaDAO.obtenerResumenAsistenciaAlumnoTurno(alumnoId, 1, mesActual, anioActual);

    // Cargar imágenes ya subidas de este alumno
    List<Imagen> imgs = new ImageDAO().listarPorAlumno(alumnoId);

    // Calcular porcentaje de asistencia
    double porcentajeAsistencia = 0.0;
    if (resumenAsistencia != null && !resumenAsistencia.isEmpty()) {
        Object porcentajeObj = resumenAsistencia.get("porcentajeAsistencia");
        if (porcentajeObj != null) {
            porcentajeAsistencia = (Double) porcentajeObj;
        }
    }

    // Determinar color del badge según el porcentaje
    String badgeClass = "bg-success";
    if (porcentajeAsistencia < 75) {
        badgeClass = "bg-danger";
    } else if (porcentajeAsistencia < 90) {
        badgeClass = "bg-warning";
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel del Padre de Familia</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="assets/css/estilos.css">
        <style>
            body {
                background-image: url('assets/img/fondo_dashboard_padre.jpg');
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
            .card-box {
                background-color: #fff8ed;
                border-radius: 15px;
                padding: 25px;
                text-align: center;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                transition: transform 0.2s;
            }
            .card-box:hover {
                transform: scale(1.03);
            }
            .btn-report {
                border-radius: 0.5rem;
                font-weight: 600;
                padding: 0.5rem 1rem;
                box-shadow: 0 2px 6px rgba(0,0,0,0.12);
                transition: transform 0.1s ease, box-shadow 0.1s ease;
            }
            .btn-report:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            }
            .asistencia-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
            }
            .progress {
                height: 10px;
                margin: 10px 0;
            }
        </style>
    </head>
    <body>

        <div class="header-bar">
            <div>
                <img src="assets/img/logosa.png" alt="Logo" style="width: 30px; height: auto; margin-right: 10px;" />
                <strong>Colegio SA</strong>
            </div>
            <div>
                Padre de: <%= padre.getAlumnoNombre()%> | Grado: <%= padre.getGradoNombre()%>
                <a href="LogoutServlet" class="btn btn-outline-light btn-sm ms-3">Cerrar sesión</a>
            </div>
        </div>

        <div class="container mt-5">
            <h2 class="text-center fw-bold mb-4">Panel del Padre de Familia</h2>

            <!-- Tarjeta de Resumen de Asistencia -->
            <div class="card asistencia-card mb-4">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h5 class="card-title">🎒 Asistencia Escolar</h5>
                            <p class="card-text mb-1">
                                <strong>Asistencia Mensual:</strong> 
                                <span class="badge <%= badgeClass%> fs-6"><%= String.format("%.1f", porcentajeAsistencia)%>%</span>
                            </p>
                            <% if (resumenAsistencia != null && !resumenAsistencia.isEmpty()) {%>
                            <div class="row mt-2">
                                <div class="col-3">
                                    <small>✅ <strong><%= resumenAsistencia.get("presentes")%></strong> Presentes</small>
                                </div>
                                <div class="col-3">
                                    <small>⏰ <strong><%= resumenAsistencia.get("tardanzas")%></strong> Tardanzas</small>
                                </div>
                                <div class="col-3">
                                    <small>❌ <strong><%= resumenAsistencia.get("ausentes")%></strong> Ausentes</small>
                                </div>
                                <div class="col-3">
                                    <small>📄 <strong><%= resumenAsistencia.get("justificados")%></strong> Justificados</small>
                                </div>
                            </div>
                            <% }%>
                        </div>
                        <div class="col-md-4 text-end">
                            <a href="AsistenciaServlet?accion=verPadre&alumno_id=<%= alumnoId%>" class="btn btn-light btn-sm me-2">
                                📊 Ver Detalles
                            </a>
                            <a href="JustificacionServlet?accion=form" class="btn btn-warning btn-sm">
                                📝 Justificar Ausencia
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4 justify-content-center">

                <!-- Notas del Alumno -->
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2">Notas del Alumno</h5>
                        <p>Revisa las notas por tarea y curso.</p>
                        <a href="notasPadre.jsp?alumno_id=<%= alumnoId%>" class="btn btn-outline-primary btn-sm">Ver Notas</a>
                    </div>
                </div>

                <!-- Observaciones -->
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2">Observaciones</h5>
                        <p>Observaciones del docente sobre tu hijo.</p>
                        <a href="observacionesPadre.jsp?alumno_id=<%= alumnoId%>" class="btn btn-outline-warning btn-sm">Ver Observaciones</a>
                    </div>
                </div>

                <!-- Tareas Asignadas -->
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2">Tareas Pendientes</h5>
                        <p>Consulta las tareas por curso.</p>
                        <a href="tareasPadre.jsp?alumno_id=<%= alumnoId%>" class="btn btn-outline-success btn-sm">Ver Tareas</a>
                    </div>
                </div>

                <!-- Álbum de Fotos -->
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2">Álbum de Fotos</h5>
                        <p>Álbum de recuerdos de tu menor hijo/a.</p>
                        <a href="albumPadre.jsp?alumno_id=<%= alumnoId%>" class="btn btn-outline-info btn-sm">Ver Álbum</a>
                    </div>
                </div>

                <!-- NUEVA TARJETA: Asistencias Detalladas -->
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2">Asistencias</h5>
                        <p>Consulta el historial completo de asistencias.</p>
                        <a href="asistenciasPadre.jsp?alumno_id=<%= alumnoId%>" class="btn btn-outline-secondary btn-sm">Ver Asistencias</a>
                    </div>
                </div>

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