<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Curso, java.util.List" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("misCursos");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Reportes de Asistencia</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Reportes de Asistencia</h2>
            <a href="docenteDashboard.jsp" class="btn btn-secondary">← Volver al Dashboard</a>
        </div>

        <div class="row">
            <!-- Tarjeta de Reporte Mensual -->
            <div class="col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="bi bi-calendar-month" style="font-size: 3rem; color: #0d6efd;"></i>
                        <h5 class="card-title mt-3">Reporte Mensual</h5>
                        <p class="card-text">Genera reportes detallados de asistencia por mes y curso.</p>
                        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalMensual">
                            Generar Reporte
                        </button>
                    </div>
                </div>
            </div>

            <!-- Tarjeta de Reporte Trimestral -->
            <div class="col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="bi bi-graph-up" style="font-size: 3rem; color: #198754;"></i>
                        <h5 class="card-title mt-3">Reporte Trimestral</h5>
                        <p class="card-text">Reportes consolidados por trimestre según calendario escolar.</p>
                        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#modalTrimestral">
                            Generar Reporte
                        </button>
                    </div>
                </div>
            </div>

            <!-- Tarjeta de Alertas -->
            <div class="col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="bi bi-exclamation-triangle" style="font-size: 3rem; color: #ffc107;"></i>
                        <h5 class="card-title mt-3">Alertas de Asistencia</h5>
                        <p class="card-text">Consulta alumnos con bajo porcentaje de asistencia.</p>
                        <button class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#modalAlertas">
                            Ver Alertas
                        </button>
                    </div>
                </div>
            </div>

            <!-- Tarjeta de Estadísticas -->
            <div class="col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="bi bi-bar-chart" style="font-size: 3rem; color: #6f42c1;"></i>
                        <h5 class="card-title mt-3">Estadísticas Generales</h5>
                        <p class="card-text">Métricas y estadísticas generales de asistencia.</p>
                        <a href="estadisticasAsistencia.jsp" class="btn btn-purple" style="background-color: #6f42c1; color: white;">
                            Ver Estadísticas
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Reporte Mensual -->
    <div class="modal fade" id="modalMensual" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Reporte Mensual</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="GenerarReporteServlet" method="post" target="_blank">
                        <input type="hidden" name="tipo" value="mensual">
                        <div class="mb-3">
                            <label for="curso_reporte" class="form-label">Curso</label>
                            <select class="form-select" id="curso_reporte" name="curso_id" required>
                                <option value="">Seleccione un curso</option>
                                <% if (cursos != null) {
                                    for (Curso c : cursos) { %>
                                        <option value="<%= c.getId() %>"><%= c.getNombre() %></option>
                                    <% }
                                } %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="mes_reporte" class="form-label">Mes</label>
                            <select class="form-select" id="mes_reporte" name="mes" required>
                                <% for (int i = 1; i <= 12; i++) { %>
                                    <option value="<%= i %>" <%= i == java.time.LocalDate.now().getMonthValue() ? "selected" : "" %>>
                                        <%= new java.text.DateFormatSymbols().getMonths()[i-1] %>
                                    </option>
                                <% } %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="anio_reporte" class="form-label">Año</label>
                            <input type="number" class="form-control" id="anio_reporte" name="anio" 
                                   value="<%= java.time.LocalDate.now().getYear() %>" required>
                        </div>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Generar PDF</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Reporte Trimestral -->
    <div class="modal fade" id="modalTrimestral" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Reporte Trimestral</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="GenerarReporteServlet" method="post" target="_blank">
                        <input type="hidden" name="tipo" value="trimestral">
                        <div class="mb-3">
                            <label for="trimestre" class="form-label">Trimestre</label>
                            <select class="form-select" id="trimestre" name="trimestre" required>
                                <option value="1">Primer Trimestre (Marzo - Mayo)</option>
                                <option value="2">Segundo Trimestre (Junio - Agosto)</option>
                                <option value="3">Tercer Trimestre (Septiembre - Noviembre)</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="anio_trimestral" class="form-label">Año</label>
                            <input type="number" class="form-control" id="anio_trimestral" name="anio" 
                                   value="<%= java.time.LocalDate.now().getYear() %>" required>
                        </div>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-success">Generar PDF</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>