<%-- 
    Document   : index
    Created on : 1 may. 2025, 1:23:30 p. m.
    Author     : Juan Pablo Amaya
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>

<%
    String error = request.getParameter("error");
    String intentosParam = request.getParameter("intentos");
    int intentosRestantes = intentosParam != null ? Integer.parseInt(intentosParam) : 3;
    boolean estaBloqueado = "bloqueado".equals(error);
    
    // Obtener el tiempo restante directamente del request
    Long tiempoRestanteMs = (Long) request.getAttribute("tiempoRestante");
    if (tiempoRestanteMs == null && estaBloqueado) {
        tiempoRestanteMs = 60000L; // 1 minuto por defecto
    }
    
    // Obtener el username del último intento si está disponible
    String lastUsername = request.getParameter("username");
%>

<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="assets/css/estilos.css">
</head>

<body class="login-bg">
    <div class="login-card">
        <h4 class="mb-3 text-center">Iniciar Sesión</h4>
        
        <form action="LoginServlet" method="post" id="loginForm">
            <div class="mb-3">
                <label class="form-label">Usuario</label>
                <input type="text" name="username" class="form-control" required 
                       id="usernameInput" value="<%= lastUsername != null ? lastUsername : "" %>" 
                       <%= estaBloqueado ? "disabled" : "" %>>
            </div>
            <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" name="password" class="form-control" required
                       id="passwordInput" <%= estaBloqueado ? "disabled" : "" %>>
            </div>
            
            <button type="submit" class="btn btn-primary w-100" 
                    id="submitBtn" <%= estaBloqueado ? "disabled" : "" %>>
                <%= estaBloqueado ? "Cuenta Bloqueada" : "Ingresar" %>
            </button>

            <!-- Mensajes de error y advertencias -->
            <% if (estaBloqueado) { %>
                <div class="alert alert-danger mt-3">
                    <strong>⚠️ Cuenta temporalmente bloqueada</strong><br>
                    Has excedido el número máximo de intentos. 
                    <span id="mensajeTiempo">
                        <% if (tiempoRestanteMs != null && tiempoRestanteMs > 0) { %>
                            Podrás intentarlo nuevamente en <span id="tiempoTexto"><%= (int)Math.ceil(tiempoRestanteMs / 1000.0) %></span> segundos.
                        <% } else { %>
                            Podrás intentarlo nuevamente en breve.
                        <% } %>
                    </span>
                </div>
            <% } else if ("1".equals(error)) { %>
                <div class="alert alert-warning mt-3">
                    <strong>❌ Credenciales incorrectas</strong><br>
                    Te quedan <strong><%= intentosRestantes %></strong> intento(s) restantes.
                </div>
            <% } else if ("2".equals(error)) { %>
                <div class="alert alert-danger mt-3">
                    Error del sistema. Por favor, contacta al administrador.
                </div>
            <% } else if ("3".equals(error)) { %>
                <div class="alert alert-danger mt-3">
                    Rol no reconocido. Contacta al administrador.
                </div>
            <% } else if ("sin_docente".equals(error)) { %>
                <div class="alert alert-danger mt-3">
                    No se encontró información del docente.
                </div>
            <% } else if ("padre_invalido".equals(error)) { %>
                <div class="alert alert-danger mt-3">
                    No se encontró información del padre.
                </div>
            <% } %>
        </form>
    </div>

    <% if (estaBloqueado) { %>
    <script>
        // Tiempo restante en segundos - usando el cálculo del servidor
        let tiempoRestante = <%= tiempoRestanteMs != null ? (int)Math.ceil(tiempoRestanteMs / 1000.0) : 60 %>;
        const username = document.getElementById('usernameInput').value;
        
        console.log("⏰ Tiempo restante inicial desde servidor:", tiempoRestante, "segundos");
        console.log("👤 Usuario:", username);
        
        // Función para verificar si el usuario sigue bloqueado
        function verificarEstadoBloqueo() {
            if (!username) return;
            
            fetch('LoginServlet?accion=verificarBloqueo&username=' + encodeURIComponent(username))
                .then(response => response.json())
                .then(data => {
                    console.log("🔍 Estado de bloqueo:", data.bloqueado);
                    if (!data.bloqueado) {
                        console.log("✅ Usuario desbloqueado, recargando página...");
                        location.reload();
                    }
                })
                .catch(error => {
                    console.error("Error al verificar bloqueo:", error);
                });
        }
        
        // Solo actualizar el texto si hay tiempo restante
        if (tiempoRestante > 0) {
            function actualizarTiempo() {
                if (tiempoRestante <= 0) {
                    // Cuando el tiempo llega a 0, empezar a verificar el estado periódicamente
                    console.log("⏰ Tiempo completado, verificando estado...");
                    document.getElementById('tiempoTexto').textContent = '0';
                    
                    // Verificar cada 5 segundos si ya fue desbloqueado
                    setInterval(verificarEstadoBloqueo, 5000);
                    return;
                }
                
                // Actualizar el texto del tiempo
                const tiempoTexto = document.getElementById('tiempoTexto');
                if (tiempoTexto) {
                    tiempoTexto.textContent = tiempoRestante;
                }
                
                tiempoRestante--;
                setTimeout(actualizarTiempo, 1000);
            }
            
            // Iniciar el contador
            console.log("🚀 Iniciando contador de desbloqueo...");
            actualizarTiempo();
        } else {
            // Si no hay tiempo restante, verificar estado periódicamente
            console.log("⏰ Sin tiempo restante, verificando estado periódicamente...");
            setInterval(verificarEstadoBloqueo, 5000);
        }
        
        // También verificar el estado periódicamente como respaldo
        setInterval(verificarEstadoBloqueo, 10000); // Cada 10 segundos
        
    </script>
    <% } %>
</body>