<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Asistencia, java.util.List" %>
<%
    List<Asistencia> asistencias = (List<Asistencia>) request.getAttribute("asistencias");
    Integer cursoId = (Integer) request.getAttribute("cursoId");
    String fecha = (String) request.getAttribute("fecha");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Asistencias del Curso</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Asistencias del Curso</h2>
            <a href="AsistenciaServlet?accion=ver" class="btn btn-secondary">← Volver</a>
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

        <!-- Filtros -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="AsistenciaServlet" class="row g-3">
                    <input type="hidden" name="accion" value="verCurso">
                    <input type="hidden" name="curso_id" value="<%= cursoId %>">
                    
                    <div class="col-md-4">
                        <label for="fecha" class="form-label">Fecha</label>
                        <input type="date" class="form-control" id="fecha" name="fecha" 
                               value="<%= fecha != null ? fecha : "" %>">
                    </div>
                    
                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" class="btn btn-primary w-100">Filtrar</button>
                    </div>
                    
                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <a href="registrarAsistencia.jsp?curso_id=<%= cursoId %>&fecha=<%= fecha %>" 
                           class="btn btn-success w-100">Nueva Asistencia</a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Lista de asistencias -->
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Registros de Asistencia</h5>
            </div>
            <div class="card-body">
                <% if (asistencias != null && !asistencias.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                                <tr>
                                    <th>Alumno</th>
                                    <th>Fecha</th>
                                    <th>Hora</th>
                                    <th>Estado</th>
                                    <th>Observaciones</th>
                                    <th>Registrado por</th>
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
                                    <td><%= a.getAlumnoNombre() %></td>
                                    <td><%= a.getFecha() %></td>
                                    <td><%= a.getHoraClase() %></td>
                                    <td>
                                        <span class="badge <%= estadoBadge %>"><%= a.getEstado() %></span>
                                    </td>
                                    <td><%= a.getObservaciones() != null ? a.getObservaciones() : "-" %></td>
                                    <td><%= a.getProfesorNombre() != null ? a.getProfesorNombre() : "Sistema" %></td>
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
                            <p>No se encontraron registros de asistencia para los criterios seleccionados.</p>
                            <a href="registrarAsistencia.jsp?curso_id=<%= cursoId %>" class="btn btn-primary">
                                Registrar Primera Asistencia
                            </a>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>