<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Curso, java.util.List" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("misCursos");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestión de Asistencias - Docente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .card-curso {
            transition: transform 0.2s;
            border: none;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .card-curso:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container mt-4">
        <h2 class="mb-4">Gestión de Asistencias</h2>

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

        <div class="row">
            <% if (cursos != null && !cursos.isEmpty()) { 
                for (Curso curso : cursos) { %>
                <div class="col-md-6 col-lg-4 mb-4">
                    <div class="card card-curso h-100">
                        <div class="card-body">
                            <h5 class="card-title text-primary"><%= curso.getNombre() %></h5>
                            <p class="card-text">
                                <strong>Grado:</strong> <%= curso.getGradoNombre() %><br>
                                <strong>Créditos:</strong> <%= curso.getCreditos() %>
                            </p>
                        </div>
                        <div class="card-footer bg-transparent">
                            <div class="d-grid gap-2">
                                <a href="AsistenciaServlet?accion=verCurso&curso_id=<%= curso.getId() %>" 
                                   class="btn btn-primary btn-sm">
                                    📊 Ver Asistencias
                                </a>
                                <a href="registrarAsistencia.jsp?curso_id=<%= curso.getId() %>" 
                                   class="btn btn-success btn-sm">
                                    📝 Registrar Asistencia
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                <% }
            } else { %>
                <div class="col-12">
                    <div class="alert alert-info text-center">
                        <h5>No tienes cursos asignados</h5>
                        <p class="mb-0">Contacta con administración para asignarte cursos.</p>
                    </div>
                </div>
            <% } %>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>