<%-- 
    Document   : dashboard
    Created on : 1 may. 2025, 1:24:01 p. m.
    Author     : Juan Pablo Amaya
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Panel de Control</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="assets/css/estilos.css?v=1.2">
    <style>
        .dashboard-card {
            background-color: #fff9f1;
            border: 1px solid #ffe9cc;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04);
            transition: transform 0.2s ease-in-out;
        }

        .dashboard-card:hover {
            transform: translateY(-5px);
        }

        .btn-custom {
            background-color: #ffffff;
            border: 1px solid #0d6efd;
            color: #0d6efd;
            padding: 0.5rem 1.2rem;
            border-radius: 6px;
            transition: all 0.3s ease-in-out;
        }

        .btn-custom:hover {
            background-color: #0d6efd;
            color: #fff;
        }
        
        /* Estilos para reducción de movimiento */
        .reduce-motion * {
            animation-duration: 0.01ms !important;
            animation-iteration-count: 1 !important;
            transition-duration: 0.01ms !important;
        }
    </style>
</head>
<body class="dashboard-page">

    <jsp:include page="header.jsp" />

    <div class="container mt-5">
        <h2 class="mb-4 text-center fw-bold">Panel de Administración</h2>

        <div class="row g-4 justify-content-center">

            <div class="col-md-4">
                <div class="dashboard-card text-center">
                    <i class="fas fa-user-graduate fa-2x mb-2 text-primary"></i>
                    <h5 class="card-title">Alumnos</h5>
                    <p>Gestiona la información de los alumnos.</p>
                    <a class="btn btn-custom" href="AlumnoServlet">Ir a Alumnos</a>
                </div>
            </div>                

            <div class="col-md-4">
                <div class="dashboard-card text-center">
                    <i class="fas fa-chalkboard-teacher fa-2x mb-2 text-success"></i>
                    <h5 class="card-title">Profesores</h5>
                    <p>Gestiona la información de los profesores.</p>
                    <a class="btn btn-custom" href="ProfesorServlet">Ir a Profesores</a>
                </div>
            </div>

            <div class="col-md-4">
                <div class="dashboard-card text-center">
                    <i class="fas fa-book fa-2x mb-2 text-warning"></i>
                    <h5 class="card-title">Cursos</h5>
                    <p>Gestiona los cursos del colegio.</p>
                    <a class="btn btn-custom" href="CursoServlet">Ir a Cursos</a>
                </div>
            </div>

            <div class="col-md-4">
                <div class="dashboard-card text-center">
                    <i class="fas fa-layer-group fa-2x mb-2 text-danger"></i>
                    <h5 class="card-title">Grados</h5>
                    <p>Gestiona los grados académicos.</p>
                    <a class="btn btn-custom" href="GradoServlet">Ir a Grados</a>
                </div>
            </div>

            <div class="col-md-4">
                <div class="dashboard-card text-center">
                    <i class="fas fa-users-cog fa-2x mb-2 text-secondary"></i>
                    <h5 class="card-title">Usuarios</h5>
                    <p>Gestiona los usuarios del sistema.</p>
                    <a class="btn btn-custom" href="UsuarioServlet">Ir a Usuarios</a>
                </div>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
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