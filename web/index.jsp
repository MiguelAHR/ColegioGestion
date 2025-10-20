<%-- 
    Document   : index
    Created on : 1 may. 2025, 1:23:30 p. m.
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

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="assets/css/estilos.css">
    <style>
        /* Estilos para el modal CAPTCHA */
        .captcha-modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .captcha-content {
            background-color: #fefefe;
            margin: 15% auto;
            padding: 20px;
            border-radius: 10px;
            width: 400px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        .captcha-text {
            font-family: 'Courier New', monospace;
            font-size: 24px;
            font-weight: bold;
            letter-spacing: 3px;
            background: linear-gradient(45deg, #666, #000);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            padding: 15px;
            border: 2px dashed #ccc;
            text-align: center;
            user-select: none;
            margin: 15px 0;
        }
        .captcha-refresh {
            cursor: pointer;
            color: #007bff;
            background: none;
            border: none;
            font-size: 16px;
        }
        .alert-captcha {
            display: none;
            margin-top: 10px;
        }
        .loading {
            display: none;
            text-align: center;
            padding: 10px;
        }
    </style>
</head>

<body class="login-bg">
    <div class="login-card">
        <h4 class="mb-3 text-center">Iniciar Sesión</h4>
        
        <!-- Contenedor para mensajes de error -->
        <div id="loginMessages"></div>
        
        <form id="loginForm" method="post" class="needs-validation" novalidate>
            <div class="mb-3">
                <label class="form-label">Usuario</label>
                <input type="text" name="username" class="form-control" required 
                       id="usernameInput" value="<%= lastUsername != null ? lastUsername : "juantapia" %>" 
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

            <!-- Mensajes de error del servidor -->
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

    <!-- Modal CAPTCHA -->
    <div id="captchaModal" class="captcha-modal">
        <div class="captcha-content">
            <h5 class="text-center mb-3">Verificación de Seguridad</h5>
            <p class="text-center">Por favor, resuelve el CAPTCHA para continuar:</p>
            
            <div class="text-center mb-3">
                <div id="captchaText" class="captcha-text"></div>
                <button type="button" class="captcha-refresh" onclick="generarCaptcha()">
                    🔄 Generar nuevo código
                </button>
            </div>
            
            <div class="mb-3">
                <label class="form-label">Ingresa el código de arriba:</label>
                <input type="text" id="captchaInput" class="form-control" 
                       placeholder="Escribe el código aquí" required>
            </div>
            
            <div id="captchaError" class="alert alert-danger alert-captcha" role="alert">
                Código incorrecto. Intenta de nuevo.
            </div>
            
            <div class="d-flex gap-2">
                <button type="button" class="btn btn-secondary w-50" onclick="cancelarLogin()">Cancelar</button>
                <button type="button" class="btn btn-primary w-50" onclick="validarCaptcha()">Verificar</button>
            </div>
        </div>
    </div>

    <!-- Loading indicator -->
    <div id="loading" class="loading">
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2">Verificando credenciales...</p>
    </div>

    <script>
        let captchaCode = '';
        let loginData = null;

        // Generar CAPTCHA
        function generarCaptcha() {
            const caracteres = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
            let captcha = '';
            for (let i = 0; i < 6; i++) {
                captcha += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
            }
            
            document.getElementById('captchaText').textContent = captcha;
            captchaCode = captcha;
            document.getElementById('captchaInput').value = '';
            document.getElementById('captchaError').style.display = 'none';
        }

        // Mostrar modal CAPTCHA
        function mostrarCaptcha() {
            generarCaptcha();
            document.getElementById('captchaModal').style.display = 'block';
            document.getElementById('captchaInput').focus();
        }

        // Ocultar modal CAPTCHA
        function ocultarCaptcha() {
            document.getElementById('captchaModal').style.display = 'none';
        }

        // Validar CAPTCHA
        function validarCaptcha() {
            const input = document.getElementById('captchaInput').value.trim();
            
            if (input === '' || input !== captchaCode) {
                document.getElementById('captchaError').style.display = 'block';
                document.getElementById('captchaInput').focus();
                generarCaptcha();
                return;
            }

            // CAPTCHA correcto, proceder con el login
            ocultarCaptcha();
            enviarLoginFinal();
        }

        // Cancelar login
        function cancelarLogin() {
            ocultarCaptcha();
            document.getElementById('loading').style.display = 'none';
            document.getElementById('submitBtn').disabled = false;
        }

        // Enviar login después de CAPTCHA válido - CON DEBUGGING
        function enviarLoginFinal() {
            console.log("🚀 Iniciando envío de login...");
            document.getElementById('loading').style.display = 'block';
            
            const formData = new FormData();
            formData.append('username', loginData.username);
            formData.append('password', loginData.password);
            formData.append('captchaInput', document.getElementById('captchaInput').value.trim());
            formData.append('captchaHidden', captchaCode);

            console.log("📤 Enviando datos:", {
                username: loginData.username,
                captchaInput: document.getElementById('captchaInput').value.trim(),
                captchaHidden: captchaCode
            });

            fetch('LoginServlet', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                console.log("📥 Respuesta recibida - Status:", response.status, "Redirected:", response.redirected);
                console.log("🔗 URL de respuesta:", response.url);
                
                if (response.redirected) {
                    console.log("➡️ Redireccionando a:", response.url);
                    window.location.href = response.url;
                    return;
                }
                return response.text().then(text => {
                    console.log("📄 Contenido de respuesta:", text);
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        console.error("❌ Error parseando JSON:", e);
                        return { success: false, error: text || 'Error desconocido' };
                    }
                });
            })
            .then(data => {
                console.log("📊 Datos parseados:", data);
                if (data && data.success && data.redirect) {
                    console.log("✅ Login exitoso - Redirigiendo a:", data.redirect);
                    window.location.href = data.redirect;
                } else if (data && data.success) {
                    console.log("✅ Login exitoso - Recargando página");
                    window.location.reload();
                } else {
                    const errorMsg = data && data.error ? data.error : 'Error en el servidor. Intenta nuevamente.';
                    console.log("❌ Error del servidor:", errorMsg);
                    mostrarError(errorMsg);
                    document.getElementById('submitBtn').disabled = false;
                }
            })
            .catch(error => {
                console.error('💥 Error de conexión:', error);
                mostrarError('Error de conexión. Intenta nuevamente.');
                document.getElementById('submitBtn').disabled = false;
            })
            .finally(() => {
                console.log("🏁 Finalizando proceso de login");
                document.getElementById('loading').style.display = 'none';
            });
        }

        // Mostrar mensaje de error - CORREGIDO
        function mostrarError(mensaje) {
            const messagesDiv = document.getElementById('loginMessages');
            messagesDiv.innerHTML = `
                <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                    ${mensaje}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
        }

        // Manejar envío del formulario
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            <% if (estaBloqueado) { %>
                return; // No hacer nada si está bloqueado
            <% } %>
            
            const username = document.getElementById('usernameInput').value.trim();
            const password = document.getElementById('passwordInput').value.trim();

            if (!username || !password) {
                mostrarError('Por favor, completa todos los campos.');
                return;
            }

            // Guardar datos y mostrar CAPTCHA
            loginData = { username, password };
            document.getElementById('submitBtn').disabled = true;
            document.getElementById('loading').style.display = 'block';

            // Simular verificación inicial y mostrar CAPTCHA
            setTimeout(() => {
                document.getElementById('loading').style.display = 'none';
                mostrarCaptcha();
            }, 500);
        });

        // Cerrar modal haciendo click fuera
        window.onclick = function(event) {
            const modal = document.getElementById('captchaModal');
            if (event.target === modal) {
                cancelarLogin();
            }
        }

        // Generar CAPTCHA inicial
        window.onload = function() {
            generarCaptcha();
            // Asegurarse de que el botón no esté deshabilitado si no hay bloqueo
            <% if (!estaBloqueado) { %>
                document.getElementById('submitBtn').disabled = false;
            <% } %>
        };
    </script>

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
</html>