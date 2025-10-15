<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Profesor, modelo.Curso, java.util.List" %>

<%
    Profesor docente = (Profesor) session.getAttribute("docente");
    // ✅ Obtener de request (no de session)
    List<Curso> cursos = (List<Curso>) request.getAttribute("misCursos");

    if (docente == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel del Docente</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="assets/css/estilos.css">

        <style>
            body {
                background-image: url('assets/img/fondo_dashboard_docente.jpg');
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
            .nav-links a {
                color: white;
                margin-right: 25px;
                text-decoration: none;
            }
            .nav-links a:hover {
                text-decoration: underline;
            }
            .card-box {
                background-color: #fff8ed;
                border-radius: 15px;
                padding: 20px;
                text-align: center;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                transition: transform 0.2s;
            }
            .card-box:hover {
                transform: scale(1.03);
            }
        </style>
    </head>
    <body>

        <div class="header-bar">
            <div class="nav-links">
                <img src="assets/img/logosa.png" alt="Logo" style="width: 30px; height: auto; margin-right: 10px;" />
                <span class="fw-bold fs-6">Colegio SA</span>
            </div>
            <div>
                <span><%= docente.getNombres()%> <%= docente.getApellidos()%></span>
                <a href="LogoutServlet" class="btn btn-outline-light btn-sm ms-3">Cerrar sesión</a>
            </div>
        </div>

        <div class="container mt-5">
            <h2 class="text-center fw-bold mb-5">Panel del Docente</h2>

            <%-- TEMPORAL: Para depuración --%>
            <div class="alert alert-info">
                <strong>Informacion:</strong><br>
                Docente: <%= docente.getNombres()%> <%= docente.getApellidos()%><br>
                IdDocente: <%= docente.getId()%><br>
                Cursos totales <%= cursos != null ? cursos.size() : "null"%>
            </div>

            <div class="row g-4">
                <%
                    if (cursos != null && !cursos.isEmpty()) {
                        for (Curso c : cursos) {
                %>
                <div class="col-md-4">
                    <div class="card-box">
                        <h5 class="fw-bold mb-2"><%= c.getNombre()%></h5>
                        <p><strong>Grado:</strong> <%= c.getGradoNombre()%></p>
                        <div class="d-grid gap-2 mt-3">
                            <a href="TareaServlet?accion=ver&curso_id=<%= c.getId()%>" class="btn btn-outline-secondary btn-sm">Gestionar Tareas</a>
                            <a href="NotaServlet?curso_id=<%= c.getId()%>" class="btn btn-outline-primary btn-sm">Gestionar Notas</a>
                            <a href="ObservacionServlet?accion=listar&curso_id=<%= c.getId()%>" class="btn btn-outline-success btn-sm">Gestionar Observaciones</a>
                        </div>
                    </div>
                </div>
                <%
                    }
                } else {
                %>
                <div class="text-center">
                    <p>No tienes cursos asignados.</p>
                </div>
                <%
                    }
                %>
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