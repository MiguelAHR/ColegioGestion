<%-- 
    Document   : header
    Created on : 1 may. 2025, 8:44:30 p. m.
    Author     : Juan Pablo Amaya
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>


<head>
    <meta charset="UTF-8">
    <title>Panel Colegio</title>

    <!-- Bootstrap 5 por CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="<%= request.getSession().getAttribute("rol") != null && "docente".equals(request.getSession().getAttribute("rol")) ? "docenteDashboard.jsp" : "dashboard.jsp"%>"><img src="assets/img/logosa.png" alt="Logo" style="width: 30px; height: auto; margin-right: 10px;">Colegio SA</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarNav" aria-controls="navbarNav"
                    aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">

                    <%
                        // Usamos la sesión directamente sin declaración explícita de la variable "session"
                        if ("docente".equals(request.getSession().getAttribute("rol"))) {
                    %>
                    <!-- Opciones para el rol Docente -->
                    <li class="nav-item">
                        <a class="nav-link" href="TareaServlet">Tareas</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="NotaServlet">Notas</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="VerAlumnosServlet">Ver Alumnos</a>
                    </li>
                    <% } else if ("admin".equals(request.getSession().getAttribute("rol"))) { %>
                    <!-- Opciones para el rol Admin -->
                    <li class="nav-item">
                        <a class="nav-link" href="AlumnoServlet">Alumnos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="ProfesorServlet">Profesores</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="CursoServlet">Cursos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="GradoServlet">Grados</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="UsuarioServlet">Usuarios</a>
                    </li>
                    <% }%>

                </ul>

                <span class="navbar-text text-light me-3">
                    <%= (request.getSession().getAttribute("usuario") != null) ? request.getSession().getAttribute("usuario") : "Invitado"%>
                </span>
                <a class="btn btn-outline-light btn-sm" href="LogoutServlet">Cerrar sesión</a>
            </div>
        </div>
    </nav>
</body>





