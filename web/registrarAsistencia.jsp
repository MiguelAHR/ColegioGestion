<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Curso, java.util.List" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    String cursoIdParam = request.getParameter("curso_id");
    String fechaParam = request.getParameter("fecha");
    
    if (fechaParam == null) {
        fechaParam = java.time.LocalDate.now().toString();
    }
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
            <h2><i class="bi bi-plus-circle"></i> Registrar Asistencia</h2>
            <a href="AsistenciaServlet?accion=ver" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Volver
            </a>
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
                                   value="<%= fechaParam %>" required>
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
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-outline-success btn-sm" onclick="marcarTodos('PRESENTE')">
                                    <i class="bi bi-check-circle"></i> Todos Presentes
                                </button>
                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="marcarTodos('AUSENTE')">
                                    <i class="bi bi-x-circle"></i> Todos Ausentes
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Información del curso seleccionado -->
                    <div id="info-curso" class="alert alert-info" style="display: none;">
                        <i class="bi bi-info-circle"></i>
                        <span id="info-text">Seleccione un curso para ver los alumnos</span>
                    </div>

                    <!-- Lista de alumnos (se cargará dinámicamente) -->
                    <div id="lista-alumnos" class="mb-4">
                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle"></i> 
                            Seleccione un curso para cargar la lista de alumnos.
                        </div>
                    </div>

                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <button type="submit" class="btn btn-primary" id="btn-guardar" disabled>
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
            verificarEstadoBoton();
        }
        
        function verificarEstadoBoton() {
            const tieneAlumnos = document.querySelectorAll('.estado-alumno').length > 0;
            const todosMarcados = Array.from(document.querySelectorAll('.estado-alumno'))
                .every(select => select.value !== '');
            
            document.getElementById('btn-guardar').disabled = !(tieneAlumnos && todosMarcados);
        }
        
        // Simulación de carga de alumnos (en una implementación real sería vía AJAX)
        document.getElementById('curso_id').addEventListener('change', function() {
            const cursoId = this.value;
            const infoCurso = document.getElementById('info-curso');
            const listaAlumnos = document.getElementById('lista-alumnos');
            
            if (cursoId) {
                // Simular alumnos de ejemplo
                const alumnos = [
                    { id: 1, nombre: 'Juan Pérez García' },
                    { id: 2, nombre: 'María López Martínez' },
                    { id: 3, nombre: 'Carlos Rodríguez Silva' },
                    { id: 4, nombre: 'Ana García Torres' }
                ];
                
                let html = '<h5 class="border-bottom pb-2">Lista de Alumnos</h5>';
                html += '<div class="table-responsive"><table class="table table-striped">';
                html += '<thead><tr><th>Alumno</th><th>Estado</th></tr></thead><tbody>';
                
                alumnos.forEach(alumno => {
                    html += `
                        <tr>
                            <td>
                                <input type="hidden" name="alumnos[${alumno.id}][id]" value="${alumno.id}">
                                ${alumno.nombre}
                            </td>
                            <td>
                                <select class="form-select form-select-sm estado-alumno" 
                                        name="alumnos[${alumno.id}][estado]" 
                                        onchange="verificarEstadoBoton()" required>
                                    <option value="">Seleccionar</option>
                                    <option value="PRESENTE">Presente</option>
                                    <option value="TARDANZA">Tardanza</option>
                                    <option value="AUSENTE">Ausente</option>
                                </select>
                            </td>
                        </tr>
                    `;
                });
                
                html += '</tbody></table></div>';
                listaAlumnos.innerHTML = html;
                
                // Mostrar información del curso
                const cursoSeleccionado = document.querySelector(`#curso_id option[value="${cursoId}"]`).textContent;
                document.getElementById('info-text').textContent = `Curso seleccionado: ${cursoSeleccionado}`;
                infoCurso.style.display = 'block';
                
            } else {
                listaAlumnos.innerHTML = '<div class="alert alert-warning"><i class="bi bi-exclamation-triangle"></i> Seleccione un curso para cargar la lista de alumnos.</div>';
                infoCurso.style.display = 'none';
                document.getElementById('btn-guardar').disabled = true;
            }
        });
    </script>
</body>
</html>