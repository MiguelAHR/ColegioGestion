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
                                            for (Curso c : cursos) {%>
                                    <option value="<%= c.getId()%>" 
                                            <%= (cursoIdParam != null && cursoIdParam.equals(String.valueOf(c.getId()))) ? "selected" : ""%>>
                                        <%= c.getNombre()%> - <%= c.getGradoNombre()%>
                                    </option>
                                    <% }
                                        }%>
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
                                       value="<%= fechaParam%>" required>
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
                                            const selects = document.querySelectorAll('.estado-alumno');
                                            const tieneAlumnos = selects.length > 0;
                                            const todosMarcados = Array.from(selects).every(select => select.value !== '');
                                            document.getElementById('btn-guardar').disabled = !(tieneAlumnos && todosMarcados);
                                        }

                                        // Cargar alumnos cuando se selecciona un curso
                                        document.getElementById('curso_id').addEventListener('change', function () {
                                            const cursoId = this.value;
                                            const cursoTexto = this.options[this.selectedIndex].text;
                                            const infoCurso = document.getElementById('info-curso');
                                            const listaAlumnos = document.getElementById('lista-alumnos');

                                            if (cursoId) {
                                                // Mostrar loading
                                                listaAlumnos.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando alumnos...</span></div><p class="mt-2 text-muted">Cargando lista de alumnos...</p></div>';

                                                console.log("🔄 Solicitando alumnos para curso:", cursoId);

                                                // Realizar petición AJAX
                                                fetch('AlumnoServlet?accion=obtenerPorCurso&curso_id=' + cursoId)
                                                        .then(response => {
                                                            if (!response.ok) {
                                                                throw new Error('Error en la respuesta del servidor: ' + response.status);
                                                            }
                                                            return response.json();
                                                        })
                                                        .then(alumnos => {
                                                            console.log('✅ Alumnos recibidos:', alumnos);

                                                            if (!alumnos || alumnos.length === 0) {
                                                                listaAlumnos.innerHTML = '<div class="alert alert-warning"><i class="bi bi-exclamation-triangle"></i> No se encontraron alumnos para este curso.</div>';
                                                                return;
                                                            }

                                                            // Construir la tabla de alumnos
                                                            let html = '<h5 class="border-bottom pb-2">Lista de Alumnos</h5>';
                                                            html += '<div class="table-responsive"><table class="table table-striped">';
                                                            html += '<thead><tr><th>#</th><th>Alumno</th><th>Estado</th></tr></thead><tbody>';

                                                            alumnos.forEach((alumno, index) => {
                                                                html += '<tr>' +
                                                                        '<td class="text-muted">' + (index + 1) + '</td>' +
                                                                        '<td><strong>' + alumno.nombres + ' ' + alumno.apellidos + '</strong>' +
                                                                        '<input type="hidden" name="alumnos[' + index + '][alumno_id]" value="' + alumno.id + '">' +
                                                                        '<div class="text-muted small">ID: ' + alumno.id + '</div></td>' +
                                                                        '<td><select class="form-select form-select-sm estado-alumno" name="alumnos[' + index + '][estado]" onchange="verificarEstadoBoton()" required>' +
                                                                        '<option value="">Seleccionar</option>' +
                                                                        '<option value="PRESENTE">✅ Presente</option>' +
                                                                        '<option value="TARDANZA">⏰ Tardanza</option>' +
                                                                        '<option value="AUSENTE">❌ Ausente</option>' +
                                                                        '</select></td></tr>';
                                                            });

                                                            html += '</tbody></table></div>';

                                                            // Agregar botones de acción rápida
                                                            html += '<div class="mt-3 d-flex gap-2 justify-content-end">' +
                                                                    '<button type="button" class="btn btn-outline-success btn-sm" onclick="marcarTodos(\'PRESENTE\')">' +
                                                                    '<i class="bi bi-check-circle"></i> Todos Presentes</button>' +
                                                                    '<button type="button" class="btn btn-outline-warning btn-sm" onclick="marcarTodos(\'TARDANZA\')">' +
                                                                    '<i class="bi bi-clock"></i> Todos Tardanza</button>' +
                                                                    '<button type="button" class="btn btn-outline-danger btn-sm" onclick="marcarTodos(\'AUSENTE\')">' +
                                                                    '<i class="bi bi-x-circle"></i> Todos Ausentes</button></div>';

                                                            listaAlumnos.innerHTML = html;

                                                            // Mostrar información del curso
                                                            document.getElementById('info-text').innerHTML = '<strong>Curso seleccionado:</strong> ' + cursoTexto + ' | <strong>Total de alumnos:</strong> ' + alumnos.length;
                                                            infoCurso.style.display = 'block';

                                                            // Verificar estado del botón después de cargar
                                                            setTimeout(verificarEstadoBoton, 100);

                                                        })
                                                        .catch(error => {
                                                            console.error('❌ Error al cargar alumnos:', error);
                                                            listaAlumnos.innerHTML = '<div class="alert alert-danger"><i class="bi bi-exclamation-triangle"></i> Error al cargar la lista de alumnos: ' + error.message + '<br><small>Verifica la consola para más detalles</small></div>';
                                                        });

                                            } else {
                                                listaAlumnos.innerHTML = '<div class="alert alert-warning"><i class="bi bi-exclamation-triangle"></i> Seleccione un curso para cargar la lista de alumnos.</div>';
                                                infoCurso.style.display = 'none';
                                                document.getElementById('btn-guardar').disabled = true;
                                            }
                                        });

                                        // ✅ MEJORADO: Manejar el envío del formulario
                                        document.getElementById('formAsistencia').addEventListener('submit', function (e) {
                                            e.preventDefault(); // Prevenir envío normal

                                            console.log("🔄 Procesando envío del formulario...");

                                            // Validar que todos los alumnos tengan estado seleccionado
                                            const selects = document.querySelectorAll('.estado-alumno');
                                            const sinSeleccionar = Array.from(selects).filter(select => select.value === '');

                                            if (sinSeleccionar.length > 0) {
                                                alert('❌ Por favor, seleccione el estado para todos los alumnos antes de guardar.');
                                                sinSeleccionar[0].focus();
                                                return;
                                            }

                                            // Validar que haya alumnos cargados
                                            if (selects.length === 0) {
                                                alert('❌ No hay alumnos cargados. Por favor, seleccione un curso primero.');
                                                return;
                                            }

                                            console.log("✅ Validaciones pasadas, preparando datos...");

                                            // Mostrar loading en el botón
                                            const btnGuardar = document.getElementById('btn-guardar');
                                            const originalText = btnGuardar.innerHTML;
                                            btnGuardar.innerHTML = '<i class="bi bi-hourglass-split"></i> Guardando...';
                                            btnGuardar.disabled = true;

                                            // Crear un campo oculto con los datos en formato JSON
                                            const alumnosData = [];
                                            document.querySelectorAll('#lista-alumnos tbody tr').forEach((row, index) => {
                                                const alumnoId = row.querySelector('input[name*="alumno_id"]').value;
                                                const estado = row.querySelector('.estado-alumno').value;
                                                alumnosData.push({
                                                    alumno_id: parseInt(alumnoId),
                                                    estado: estado
                                                });
                                            });

                                            // Eliminar campo JSON anterior si existe
                                            const existingJsonInput = document.getElementById('alumnos_json');
                                            if (existingJsonInput) {
                                                existingJsonInput.remove();
                                            }

                                            // Crear nuevo campo JSON
                                            const jsonInput = document.createElement('input');
                                            jsonInput.type = 'hidden';
                                            jsonInput.name = 'alumnos_json';
                                            jsonInput.id = 'alumnos_json';
                                            jsonInput.value = JSON.stringify(alumnosData);
                                            this.appendChild(jsonInput);

                                            console.log("📦 Datos a enviar:", jsonInput.value);
                                            console.log("🚀 Enviando formulario...");

                                            // ✅ AGREGADO: Timeout para evitar que el botón se quede bloqueado
                                            setTimeout(() => {
                                                // Restaurar el botón si pasa mucho tiempo sin respuesta
                                                btnGuardar.innerHTML = originalText;
                                                btnGuardar.disabled = false;
                                                console.log("⏰ Timeout: Restaurando botón");
                                            }, 10000); // 10 segundos

                                            // Enviar formulario
                                            this.submit();
                                        });

                                        // Inicializar fecha con hoy si está vacía
                                        document.addEventListener('DOMContentLoaded', function () {
                                            const fechaInput = document.getElementById('fecha');
                                            if (fechaInput && !fechaInput.value) {
                                                const hoy = new Date().toISOString().split('T')[0];
                                                fechaInput.value = hoy;
                                            }

                                            // Verificar estado inicial del botón
                                            verificarEstadoBoton();
                                        });
        </script>
    </body>
</html>