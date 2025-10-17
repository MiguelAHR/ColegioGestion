<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Asistencia, java.util.List, java.util.Map" %>
<%
    List<Asistencia> asistencias = (List<Asistencia>) request.getAttribute("asistencias");
    Map<String, Object> resumen = (Map<String, Object>) request.getAttribute("resumen");
    Integer mes = (Integer) request.getAttribute("mes");
    Integer anio = (Integer) request.getAttribute("anio");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
    
    if (mes == null) mes = java.time.LocalDate.now().getMonthValue();
    if (anio == null) anio = java.time.LocalDate.now().getYear();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Asistencias de mi Hijo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Asistencias de mi Hijo</h2>
            <a href="padreDashboard.jsp" class="btn btn-secondary">← Volver al Dashboard</a>
        </div>

        <% if (mensaje != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <%= mensaje %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
        
        <% if (error != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <%= error %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Resumen -->
        <% if (resumen != null && !resumen.isEmpty()) { 
            double porcentaje = (Double) resumen.get("porcentajeAsistencia");
            String progressClass = "bg-success";
            if (porcentaje < 75) progressClass = "bg-danger";
            else if (porcentaje < 90) progressClass = "bg-warning";
        %>
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">📊 Resumen del Mes</h5>
                        <div class="row text-center">
                            <div class="col-md-3 mb-3">
                                <div class="p-3 bg-success bg-opacity-10 rounded">
                                    <h3 class="text-success mb-0"><%= resumen.get("presentes") %></h3>
                                    <small class="text-muted">Presentes</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <div class="p-3 bg-warning bg-opacity-10 rounded">
                                    <h3 class="text-warning mb-0"><%= resumen.get("tardanzas") %></h3>
                                    <small class="text-muted">Tardanzas</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <div class="p-3 bg-danger bg-opacity-10 rounded">
                                    <h3 class="text-danger mb-0"><%= resumen.get("ausentes") %></h3>
                                    <small class="text-muted">Ausentes</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <div class="p-3 bg-info bg-opacity-10 rounded">
                                    <h3 class="text-info mb-0"><%= resumen.get("justificados") %></h3>
                                    <small class="text-muted">Justificados</small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mt-3">
                            <div class="d-flex justify-content-between mb-1">
                                <span>Porcentaje de Asistencia</span>
                                <span><strong><%= String.format("%.1f", porcentaje) %>%</strong></span>
                            </div>
                            <div class="progress" style="height: 20px;">
                                <div class="progress-bar <%= progressClass %>" role="progressbar" 
                                     style="width: <%= porcentaje %>%;" 
                                     aria-valuenow="<%= porcentaje %>" aria-valuemin="0" aria-valuemax="100">
                                    <%= String.format("%.1f", porcentaje) %>%
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <% } %>

        <!-- Filtros -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="AsistenciaServlet" class="row g-3">
                    <input type="hidden" name="accion" value="verPadre">
                    
                    <div class="col-md-3">
                        <label for="mes" class="form-label">Mes</label>
                        <select class="form-select" id="mes" name="mes">
                            <% for (int i = 1; i <= 12; i++) { %>
                                <option value="<%= i %>" <%= i == mes ? "selected" : "" %>>
                                    <%= new java.text.DateFormatSymbols().getMonths()[i-1] %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="anio" class="form-label">Año</label>
                        <select class="form-select" id="anio" name="anio">
                            <% for (int i = anio - 2; i <= anio + 1; i++) { %>
                                <option value="<%= i %>" <%= i == anio ? "selected" : "" %>>
                                    <%= i %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" class="btn btn-primary w-100">Filtrar</button>
                    </div>
                    
                    <div class="col-md-3">
                        <label class="form-label">&nbsp;</label>
                        <a href="justificarAusencia.jsp" class="btn btn-warning w-100">
                            📝 Justificar Ausencia
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Lista de asistencias -->
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Detalle de Asistencias</h5>
            </div>
            <div class="card-body">
                <% if (asistencias != null && !asistencias.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                                <tr>
                                    <th>Fecha</th>
                                    <th>Curso</th>
                                    <th>Grado</th>
                                    <th>Estado</th>
                                    <th>Hora</th>
                                    <th>Observaciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Asistencia a : asistencias) { 
                                    String estadoBadge = "";
                                    switch(a.getEstado()) {
                                        case "PRESENTE": estadoBadge = "bg-success"; break;
                                        case "TARDANZA": estadoBadge = "bg-warning"; break;
                                        case "AUSENTE": estadoBadge = "bg-danger"; break;
                                        case "JUSTIFICADO": estadoBadge = "bg-info"; break;
                                    }
                                %>
                                <tr>
                                    <td><%= a.getFecha() %></td>
                                    <td><%= a.getCursoNombre() %></td>
                                    <td><%= a.getGradoNombre() %></td>
                                    <td>
                                        <span class="badge <%= estadoBadge %>"><%= a.getEstado() %></span>
                                    </td>
                                    <td><%= a.getHoraClase() %></td>
                                    <td><%= a.getObservaciones() != null ? a.getObservaciones() : "-" %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center py-4">
                        <div class="text-muted">
                            <i class="bi bi-calendar-x" style="font-size: 3rem;"></i>
                            <h5 class="mt-3">No hay asistencias registradas</h5>
                            <p>No se encontraron registros de asistencia para el período seleccionado.</p>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>