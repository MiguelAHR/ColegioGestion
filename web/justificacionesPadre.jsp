<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Justificacion, java.util.List" %>
<%
    List<Justificacion> justificaciones = (List<Justificacion>) request.getAttribute("justificaciones");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Mis Justificaciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Mis Justificaciones</h2>
            <div>
                <a href="justificarAusencia.jsp" class="btn btn-primary me-2">
                    <i class="bi bi-plus-circle"></i> Nueva Justificación
                </a>
                <a href="asistenciasPadre.jsp" class="btn btn-secondary">← Volver a Asistencias</a>
            </div>
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

        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="bi bi-list-check"></i> Historial de Justificaciones
                    <span class="badge bg-light text-dark ms-2"><%= justificaciones != null ? justificaciones.size() : 0 %></span>
                </h5>
            </div>
            <div class="card-body">
                <% if (justificaciones != null && !justificaciones.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                                <tr>
                                    <th>Fecha Envío</th>
                                    <th>Curso</th>
                                    <th>Fecha Ausencia</th>
                                    <th>Tipo</th>
                                    <th>Estado</th>
                                    <th>Respuesta</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Justificacion j : justificaciones) { 
                                    String estadoBadge = "";
                                    String estadoIcon = "";
                                    switch(j.getEstado()) {
                                        case "PENDIENTE": 
                                            estadoBadge = "bg-warning";
                                            estadoIcon = "bi-clock";
                                            break;
                                        case "APROBADO": 
                                            estadoBadge = "bg-success";
                                            estadoIcon = "bi-check-circle";
                                            break;
                                        case "RECHAZADO": 
                                            estadoBadge = "bg-danger";
                                            estadoIcon = "bi-x-circle";
                                            break;
                                    }
                                %>
                                <tr>
                                    <td>
                                        <small>
                                            <%= j.getFechaJustificacion() != null ? 
                                                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(j.getFechaJustificacion()) : "N/A" %>
                                        </small>
                                    </td>
                                    <td><%= j.getCursoNombre() %></td>
                                    <td>
                                        <small>
                                            <%= j.getFecha() %><br>
                                            <%= j.getHoraClase() %>
                                        </small>
                                    </td>
                                    <td>
                                        <span class="badge bg-info"><%= j.getTipoJustificacion() %></span>
                                    </td>
                                    <td>
                                        <span class="badge <%= estadoBadge %>">
                                            <i class="bi <%= estadoIcon %>"></i> <%= j.getEstado() %>
                                        </span>
                                    </td>
                                    <td>
                                        <% if (j.getObservacionesAprobacion() != null && !j.getObservacionesAprobacion().isEmpty()) { %>
                                            <button type="button" class="btn btn-sm btn-outline-secondary" 
                                                    data-bs-toggle="popover" 
                                                    data-bs-title="Observaciones del Docente"
                                                    data-bs-content="<%= j.getObservacionesAprobacion() %>">
                                                Ver
                                            </button>
                                        <% } else { %>
                                            <span class="text-muted">-</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-outline-primary" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#detalleModal<%= j.getId() %>">
                                            <i class="bi bi-eye"></i> Detalles
                                        </button>

                                        <!-- Modal de detalles -->
                                        <div class="modal fade" id="detalleModal<%= j.getId() %>" tabindex="-1">
                                            <div class="modal-dialog modal-lg">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title">Detalles de Justificación</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <strong>Alumno:</strong> <%= j.getAlumnoNombre() %><br>
                                                                <strong>Curso:</strong> <%= j.getCursoNombre() %><br>
                                                                <strong>Fecha de Ausencia:</strong> <%= j.getFecha() %> <%= j.getHoraClase() %>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <strong>Tipo:</strong> <%= j.getTipoJustificacion() %><br>
                                                                <strong>Estado:</strong> 
                                                                <span class="badge <%= estadoBadge %>"><%= j.getEstado() %></span><br>
                                                                <strong>Fecha Envío:</strong> 
                                                                <%= j.getFechaJustificacion() != null ? 
                                                                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(j.getFechaJustificacion()) : "N/A" %>
                                                            </div>
                                                        </div>
                                                        <hr>
                                                        <div class="mb-3">
                                                            <strong>Descripción:</strong>
                                                            <p class="mt-2 p-3 bg-light rounded"><%= j.getDescripcion() %></p>
                                                        </div>
                                                        <% if (j.getObservacionesAprobacion() != null && !j.getObservacionesAprobacion().isEmpty()) { %>
                                                            <div class="mb-3">
                                                                <strong>Observaciones del Docente:</strong>
                                                                <p class="mt-2 p-3 bg-light rounded"><%= j.getObservacionesAprobacion() %></p>
                                                            </div>
                                                        <% } %>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center py-4">
                        <div class="text-muted">
                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                            <h5 class="mt-3">No hay justificaciones enviadas</h5>
                            <p>No has enviado ninguna justificación hasta el momento.</p>
                            <a href="justificarAusencia.jsp" class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Enviar Primera Justificación
                            </a>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Inicializar popovers
        var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    </script>
</body>
</html>