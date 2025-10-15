<%-- 
    Document   : usuarioForm
    Created on : 2 may. 2025, 1:45:40 a. m.
    Author     : Juan Pablo Amaya
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="javax.servlet.http.HttpSession" %>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    Usuario u = (Usuario) request.getAttribute("usuario");
    boolean esEditar = u != null;
%>

<head>
    <meta charset="UTF-8">
    <title>Registrar Alumno</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="assets/css/estilos.css">
</head>
<body class="dashboard-page">

    <jsp:include page="header.jsp" />

    <div class="container mt-4">
        <h2><%= esEditar ? "Editar Usuario" : "Registrar Usuario"%></h2>
        <form action="UsuarioServlet" method="post">
            <input type="hidden" name="id" value="<%= esEditar ? u.getId() : ""%>">

            <div class="mb-3">
                <label class="form-label">Nombre de Usuario:</label>
                <input type="text" class="form-control" name="username" value="<%= esEditar ? u.getUsername() : ""%>" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Contraseña:</label>
                <input type="password" class="form-control" name="password" value="<%= esEditar ? u.getPassword() : ""%>" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Rol:</label>
                <select class="form-select" name="rol" required>
                    <option value="">-- Selecciona un rol --</option>
                    <option value="admin" <%= esEditar && u.getRol().equals("admin") ? "selected" : ""%>>admin</option>
                    <option value="docente" <%= esEditar && u.getRol().equals("docente") ? "selected" : ""%>>docente</option>
                    <option value="padre" <%= esEditar && u.getRol().equals("padre") ? "selected" : ""%>>padre</option>
                </select>
            </div>


            <button type="submit" class="btn btn-primary"><%= esEditar ? "Actualizar" : "Registrar"%></button>
            <a href="UsuarioServlet" class="btn btn-secondary">Cancelar</a>
        </form>
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
