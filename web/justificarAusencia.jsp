<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Asistencia, java.util.List" %>
<%
    String alumnoId = request.getParameter("alumno_id");
    // En una implementación real, cargarías las ausencias del alumno
    List<Asistencia> ausencias = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Justificar Ausencia</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Justificar Ausencia</h2>
            <a href="asistenciasPadre.jsp" class="btn btn-secondary">← Volver a Asistencias</a>
        </div>

        <div class="row">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-body">
                        <form method="post" action="JustificacionServlet" enctype="multipart/form-data">
                            <input type="hidden" name="accion" value="crear">
                            <input type="hidden" name="alumno_id" value="<%= alumnoId %>">
                            
                            <div class="mb-3">
                                <label for="asistencia_id" class="form-label">Seleccione la ausencia a justificar *</label>
                                <select class="form-select" id="asistencia_id" name="asistencia_id" required>
                                    <option value="">Seleccione una fecha de ausencia</option>
                                    <% if (!ausencias.isEmpty()) {
                                        for (Asistencia a : ausencias) { %>
                                            <option value="<%= a.getId() %>">
                                                <%= a.getFecha() %> - <%= a.getCursoNombre() %> (<%= a.getHoraClase() %>)
                                            </option>
                                        <% }
                                    } else { %>
                                        <option value="">No hay ausencias pendientes de justificación</option>
                                    <% } %>
                                </select>
                                <div class="form-text">Solo se pueden justificar ausencias con estado "AUSENTE"</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="tipo_justificacion" class="form-label">Tipo de Justificación *</label>
                                <select class="form-select" id="tipo_justificacion" name="tipo_justificacion" required>
                                    <option value="">Seleccione un tipo</option>
                                    <option value="ENFERMEDAD">Enfermedad</option>
                                    <option value="EMERGENCIA_FAMILIAR">Emergencia Familiar</option>
                                    <option value="CITA_MEDICA">Cita Médica</option>
                                    <option value="OTRO">Otro</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="descripcion" class="form-label">Descripción Detallada *</label>
                                <textarea class="form-control" id="descripcion" name="descripcion" 
                                          rows="4" placeholder="Describa el motivo de la ausencia..." required></textarea>
                                <div class="form-text">Proporcione todos los detalles necesarios para la justificación.</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="documento_adjunto" class="form-label">Documento Adjunto (Opcional)</label>
                                <input type="file" class="form-control" id="documento_adjunto" name="documento_adjunto"
                                       accept=".pdf,.jpg,.jpeg,.png,.doc,.docx">
                                <div class="form-text">
                                    Formatos aceptados: PDF, JPG, PNG, DOC, DOCX (Máximo 5MB)
                                </div>
                            </div>
                            
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i>
                                <strong>Importante:</strong> Las justificaciones serán revisadas por el personal docente. 
                                Recibirá una notificación una vez que sea aprobada o rechazada.
                            </div>
                            
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-send"></i> Enviar Justificación
                                </button>
                                <a href="asistenciasPadre.jsp" class="btn btn-secondary">Cancelar</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> Tipos de Justificación</h6>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <h6 class="text-primary">Enfermedad</h6>
                            <small class="text-muted">Incluye certificados médicos o justificativos de salud.</small>
                        </div>
                        <div class="mb-3">
                            <h6 class="text-primary">Emergencia Familiar</h6>
                            <small class="text-muted">Situaciones familiares urgentes que requieren la presencia del estudiante.</small>
                        </div>
                        <div class="mb-3">
                            <h6 class="text-primary">Cita Médica</h6>
                            <small class="text-muted">Consultas médicas programadas con comprobante.</small>
                        </div>
                        <div class="mb-3">
                            <h6 class="text-primary">Otro</h6>
                            <small class="text-muted">Otras situaciones justificadas que no encajan en las categorías anteriores.</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>