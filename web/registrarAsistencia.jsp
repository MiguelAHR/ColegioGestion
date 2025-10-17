<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Curso, modelo.Alumno, java.util.List" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    String cursoIdParam = request.getParameter("curso_id");
    String fechaParam = request.getParameter("fecha");
    
    // En una implementación real, aquí cargarías los alumnos del curso seleccionado
    List<Alumno> alumnos = new java.util.ArrayList<>(); // Placeholder
%>
<!DOCTYPE html>
<html>
<head>
    <title>Registrar Asistencia</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Registrar Asistencia</h2>
            <a href="AsistenciaServlet?accion=ver" class="btn btn-secondary">← Volver</a>
        </div>

        <div class="card">
            <div class="card-body">
                <form method="post" action="AsistenciaServlet" id="formAsistencia">
                    <input type="hidden" name="accion" value="registrarGrupal">
                    
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <label for="curso_id" class="form-label">Curso *</label>
                            <select class="form-select" id="curso_id" name="curso_id" required>
                                <option value="">Seleccione un curso</option>
                                <% if (cursos != null) {
                                    for (Curso c : cursos) { %>
                                        <option value="<%= c.getId() %>" 
                                            <%= (cursoIdParam != null && cursoIdParam.equals(String.valueOf(c.getId()))) ? "selected" : "" %>>
                                            <%= c.getNombre() %> - <%= c.getGradoNombre() %>
                                        </option>
                                    <% }
                                } %>
                            </select>
                        </div>
                        
                        <div class="col-md-3">
                            <label for="turno_id" class="form-label">Turno *</label>
                            <select class="form-select" id="turno_id" name="turno_id" required>
                                <option value="1">MAÑANA</option>
                                <option value="2">TARDE</option>
                            </select>
                        </div>
                        
                        <div class="col-md-3">
                            <label for="fecha" class="form-label">Fecha *</label>
                            <input type="date" class="form-control" id="fecha" name="fecha" 
                                   value="<%= fechaParam != null ? fechaParam : java.time.LocalDate.now().toString() %>" required>
                        </div>
                    </div>

                    <div class="row mb-4">
                        <div class="col-md-6">
                            <label for="hora_clase" class="form-label">Hora de Clase *</label>
                            <input type="time" class="form-control" id="hora_clase" name="hora_clase" 
                                   value="08:00" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Acción Rápida</label>
                            <div>
                                <button type="button" class="btn btn-outline-success btn-sm" onclick="marcarTodos('PRESENTE')">
                                    Marcar Todos Presentes
                                </button>
                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="marcarTodos('AUSENTE')">
                                    Marcar Todos Ausentes
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Lista de alumnos -->
                    <div class="mb-4">
                        <h5 class="border-bottom pb-2">Lista de Alumnos</h5>
                        <div id="lista-alumnos">
                            <% if (alumnos.isEmpty()) { %>
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle"></i> Seleccione un curso para cargar la lista de alumnos.
                                </div>
                            <% } else { 
                                // Aquí iría el loop para mostrar alumnos
                            } %>
                        </div>
                    </div>

                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Guardar Asistencias
                        </button>
                        <a href="AsistenciaServlet?accion=ver" class="btn btn-secondary">Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function marcarTodos(estado) {
            document.querySelectorAll('.estado-alumno').forEach(select => {
                select.value = estado;
            });
        }
        
        // En una implementación real, aquí iría el código para cargar alumnos vía AJAX
        document.getElementById('curso_id').addEventListener('change', function() {
            // Cargar alumnos del curso seleccionado
            console.log('Curso cambiado:', this.value);
        });
    </script>
</body>
</html>