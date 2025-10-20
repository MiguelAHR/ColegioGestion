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
        
        <%-- Mostrar mensajes de éxito/error --%>
        <c:if test="${not empty sessionScope.mensaje}">
            <div class="alert alert-success">${sessionScope.mensaje}</div>
            <c:remove var="mensaje" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-danger">${sessionScope.error}</div>
            <c:remove var="error" scope="session"/>
        </c:if>
        
        <form action="UsuarioServlet" method="post" id="usuarioForm">
            <input type="hidden" name="id" value="<%= esEditar ? u.getId() : ""%>">

            <div class="mb-3">
                <label class="form-label">Nombre de Usuario:</label>
                <input type="text" class="form-control" name="username" value="<%= esEditar ? u.getUsername() : ""%>" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Contraseña:</label>
                <input type="password" class="form-control" name="password" 
                       id="passwordInput" 
                       value="<%= esEditar ? u.getPassword() : ""%>" 
                       <%= esEditar ? "" : "required" %>
                       oninput="validarPasswordEnTiempoReal(this.value)">
                
                <!-- ✅ NUEVO: Indicador de fortaleza de contraseña (solo para nuevos registros) -->
                <% if (!esEditar) { %>
                    <div id="indicadorPassword" class="form-text mt-1" style="font-size: 0.85em;"></div>
                    
                    <!-- ✅ NUEVO: Requisitos de contraseña -->
                    <div class="requisitos-password mt-2 p-2 border rounded" style="background-color: #f8f9fa; font-size: 0.8em; display: none;" id="requisitosPassword">
                        <strong>Requisitos de contraseña segura:</strong>
                        <ul class="mb-0 mt-1" style="padding-left: 1.2em;">
                            <li>Mínimo 8 caracteres</li>
                            <li>Al menos una letra mayúscula</li>
                            <li>Al menos una letra minúscula</li>
                            <li>Al menos un número</li>
                            <li>Al menos un carácter especial (!@#$%^&* etc.)</li>
                        </ul>
                    </div>
                <% } else { %>
                    <div class="form-text">Dejar en blanco para mantener la contraseña actual</div>
                <% } %>
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

            <button type="submit" class="btn btn-primary" id="submitBtn">
                <%= esEditar ? "Actualizar" : "Registrar"%>
            </button>
            <a href="UsuarioServlet" class="btn btn-secondary">Cancelar</a>
        </form>
    </div>
    
    <!-- ✅ NUEVO: Script para validación en tiempo real (solo para nuevos registros) -->
    <% if (!esEditar) { %>
    <script>
        // Validar contraseña en tiempo real
        function validarPasswordEnTiempoReal(password) {
            const indicador = document.getElementById('indicadorPassword');
            const requisitos = document.getElementById('requisitosPassword');
            const submitBtn = document.getElementById('submitBtn');
            
            if (password.length === 0) {
                indicador.innerHTML = '';
                requisitos.style.display = 'none';
                submitBtn.disabled = false;
                return;
            }
            
            // Mostrar requisitos cuando el usuario empiece a escribir
            if (password.length > 0) {
                requisitos.style.display = 'block';
            }
            
            // Validar longitud mínima primero (feedback inmediato)
            if (password.length < 8) {
                indicador.innerHTML = '❌ Muy corta (mínimo 8 caracteres)';
                indicador.style.color = 'red';
                submitBtn.disabled = true;
                return;
            }
            
            // Consultar al servidor para validación completa
            fetch('LoginServlet?accion=verificarPassword&password=' + encodeURIComponent(password))
                .then(response => response.json())
                .then(data => {
                    if (data.esFuerte) {
                        indicador.innerHTML = '✅ Contraseña segura';
                        indicador.style.color = 'green';
                        submitBtn.disabled = false;
                    } else {
                        indicador.innerHTML = '❌ ' + data.mensaje;
                        indicador.style.color = 'red';
                        submitBtn.disabled = true;
                    }
                })
                .catch(error => {
                    console.error('Error al validar contraseña:', error);
                    indicador.innerHTML = '⚠️ Error validando contraseña';
                    indicador.style.color = 'orange';
                    submitBtn.disabled = false;
                });
        }
        
        // Mostrar/ocultar requisitos al enfocar/desenfocar el campo
        document.getElementById('passwordInput').addEventListener('focus', function() {
            if (this.value.length > 0) {
                document.getElementById('requisitosPassword').style.display = 'block';
            }
        });
        
        document.getElementById('passwordInput').addEventListener('blur', function() {
            // Ocultar requisitos después de un tiempo si el campo está vacío
            if (this.value.length === 0) {
                setTimeout(() => {
                    document.getElementById('requisitosPassword').style.display = 'none';
                }, 500);
            }
        });
        
        // Validar antes de enviar el formulario
        document.getElementById('usuarioForm').addEventListener('submit', function(e) {
            const password = document.getElementById('passwordInput').value;
            if (password.length > 0 && password.length < 8) {
                e.preventDefault();
                alert('La contraseña debe tener al menos 8 caracteres');
                return false;
            }
        });
    </script>
    <% } %>
    
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