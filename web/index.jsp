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
    int intentoActual = 4 - intentosRestantes; // Calcula el intento actual (1, 2 o 3)
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
            .intento-indicator {
                display: flex;
                justify-content: center;
                margin: 15px 0;
                gap: 8px;
            }
            .intento-punto {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                background-color: #dee2e6;
                transition: all 0.3s ease;
            }
            .intento-punto.activo {
                background-color: #dc3545;
                transform: scale(1.2);
            }
            .intento-punto.completado {
                background-color: #28a745;
            }
            .progress {
                height: 6px;
                margin: 10px 0;
            }
            .tiempo-restante {
                font-size: 0.9em;
                color: #6c757d;
                margin-top: 5px;
            }
            .attempt-warning {
                border-left: 4px solid #ffc107;
                background-color: #fff3cd;
            }
            .attempt-danger {
                border-left: 4px solid #dc3545;
                background-color: #f8d7da;
            }
        </style>
    </head>

    <body class="login-bg">
        <div class="login-card">
            <h4 class="mb-3 text-center">Iniciar Sesión</h4>

            <!-- Indicador visual de intentos -->
            <% if (!estaBloqueado && "1".equals(error)) { %>
            <div class="intento-indicator">
                <% for (int i = 1; i <= 3; i++) { %>
                    <div class="intento-punto <%= i <= intentoActual ? "activo" : "" %> <%= i < intentoActual ? "completado" : "" %>"></div>
                <% } %>
            </div>
            <div class="progress">
                <div class="progress-bar bg-warning" style="width: <%= (intentoActual / 3.0) * 100 %>%">
                    <%= intentoActual %> de 3
                </div>
            </div>
            <% } %>

            <!-- Contenedor para mensajes de error -->
            <div id="loginMessages">
                <!-- Mensajes iniciales del servidor -->
                <% if (estaBloqueado) { %>
                <div class="alert alert-danger mt-3">
                    <strong>⚠️ Cuenta temporalmente bloqueada</strong><br>
                    Has excedido el número máximo de intentos. 
                    <span id="mensajeTiempo">
                        <% if (tiempoRestanteMs != null && tiempoRestanteMs > 0) {%>
                        Podrás intentarlo nuevamente en <span id="tiempoTexto"><%= (int) Math.ceil(tiempoRestanteMs / 1000.0)%></span> segundos.
                        <% } else { %>
                        Podrás intentarlo nuevamente en breve.
                        <% } %>
                    </span>
                    <div class="tiempo-restante" id="tiempoDetalle">
                        Tiempo restante: <span id="minutos">0</span>:<span id="segundos">00</span>
                    </div>
                </div>
                <% } else if ("1".equals(error)) { %>
                <div class="alert alert-warning mt-3 attempt-warning">
                    <strong>❌ Intento <%= intentoActual %> de 3 fallido</strong><br>
                    <strong>Te quedan <%= intentosRestantes %> intento(s) restantes.</strong>
                    <% if (intentoActual == 2) { %>
                    <div class="tiempo-restante mt-1">
                        ⚠️ En el próximo intento fallido, la cuenta se bloqueará por 1 minuto.
                    </div>
                    <% } %>
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
            </div>

            <form id="loginForm" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label class="form-label">Usuario</label>
                    <input type="text" name="username" class="form-control" required 
                           id="usernameInput" value="<%= lastUsername != null ? lastUsername : ""%>" 
                           <%= estaBloqueado ? "disabled" : ""%>>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" name="password" class="form-control" required
                           id="passwordInput" <%= estaBloqueado ? "disabled" : ""%>>
                </div>

                <button type="submit" class="btn btn-primary w-100" 
                        id="submitBtn" <%= estaBloqueado ? "disabled" : ""%>>
                    <%= estaBloqueado ? "Cuenta Bloqueada" : "Ingresar"%>
                </button>
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
                    <input type="hidden" id="captchaHidden" name="captchaHidden">
                </div>

                <div id="captchaError" class="alert alert-danger alert-captcha" role="alert">
                    Código incorrecto. Intenta de nuevo.
                </div>

                <div class="d-flex gap-2">
                    <button type="button" class="btn btn-secondary w-50" onclick="cancelarLogin()">Cancelar</button>
                    <button type="button" class="btn btn-primary w-50" onclick="validarYEnviarCaptcha()">Verificar y Continuar</button>
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
                document.getElementById('captchaHidden').value = captcha;
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

            // ✅ FUNCIÓN COMPLETAMENTE CORREGIDA: Mostrar mensajes de error
            function mostrarMensaje(tipo, mensaje, intentosRestantes = null, maxIntentos = 3) {
                const messagesDiv = document.getElementById('loginMessages');
                messagesDiv.innerHTML = ''; // Limpiar mensajes anteriores

                let alertClass = 'alert-danger';
                let icon = '❌';
                let contenido = '';
                let indicadorIntentos = '';
                let progreso = '';

                if (tipo === 'credenciales' && intentosRestantes !== null) {
                    const intentoActual = maxIntentos - intentosRestantes + 1;
                    
                    // ✅ CORREGIDO: Usar método compatible con JSP
                    let puntosHTML = '';
                    for (let i = 1; i <= maxIntentos; i++) {
                        let clases = 'intento-punto';
                        if (i <= intentoActual) {
                            clases += ' activo';
                        }
                        if (i < intentoActual) {
                            clases += ' completado';
                        }
                        puntosHTML += '<div class="' + clases + '"></div>';
                    }
                    
                    indicadorIntentos = '<div class="intento-indicator">' + puntosHTML + '</div>' +
                        '<div class="progress">' +
                        '<div class="progress-bar bg-warning" style="width: ' + ((intentoActual / maxIntentos) * 100) + '%">' +
                        intentoActual + ' de ' + maxIntentos +
                        '</div>' +
                        '</div>';
                }

                switch(tipo) {
                    case 'bloqueado':
                        alertClass = 'alert-danger';
                        icon = '⚠️';
                        contenido = '<strong>' + icon + ' Cuenta temporalmente bloqueada</strong><br>Has excedido el número máximo de intentos. <span id="mensajeTiempo">' + mensaje + '</span>';
                        break;
                    case 'credenciales':
                        alertClass = 'alert-warning attempt-warning';
                        icon = '❌';
                        const intentoActual = maxIntentos - intentosRestantes + 1;
                        let advertencia = '';
                        if (intentoActual === 2) {
                            advertencia = '<div class="tiempo-restante mt-1">⚠️ En el próximo intento fallido, la cuenta se bloqueará por 1 minuto.</div>';
                        }
                        contenido = '<strong>' + icon + ' Intento ' + intentoActual + ' de ' + maxIntentos + ' fallido</strong><br><strong>Te quedan ' + intentosRestantes + ' intento(s) restantes.</strong>' + advertencia;
                        break;
                    case 'requiere_captcha':
                        alertClass = 'alert-info';
                        icon = '🛡️';
                        contenido = '<strong>' + icon + ' ' + mensaje + '</strong>';
                        break;
                    case 'captcha_incorrecto':
                        alertClass = 'alert-warning';
                        icon = '🛡️';
                        contenido = '<strong>' + icon + ' ' + mensaje + '</strong>';
                        break;
                    case 'sistema':
                        alertClass = 'alert-danger';
                        icon = '💥';
                        contenido = '<strong>' + icon + ' ' + mensaje + '</strong>';
                        break;
                    default:
                        alertClass = 'alert-danger';
                        icon = '❌';
                        contenido = '<strong>' + icon + ' ' + mensaje + '</strong>';
                }

                messagesDiv.innerHTML = 
                    indicadorIntentos +
                    '<div class="alert ' + alertClass + ' alert-dismissible fade show mt-3" role="alert">' +
                        contenido +
                        '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
                    '</div>' +
                    progreso;
            }

            // ✅ NUEVA FUNCIÓN: Enviar credenciales sin CAPTCHA primero
            function enviarCredenciales() {
                console.log("🚀 Enviando credenciales para verificación...");

                const params = new URLSearchParams();
                params.append('username', loginData.username);
                params.append('password', loginData.password);

                fetch('LoginServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: params
                })
                .then(response => response.json())
                .then(data => {
                    console.log("📊 Respuesta del servidor:", data);
                    
                    if (data.success) {
                        if (data.redirect) {
                            console.log("✅ Login exitoso - Redirigiendo a:", data.redirect);
                            window.location.href = data.redirect;
                        } else {
                            window.location.reload();
                        }
                    } else {
                        const tipoError = data.tipoError || 'desconocido';
                        const intentos = data.intentosRestantes || 0;
                        
                        console.log(`🔍 Tipo error: ${tipoError}, Intentos: ${intentos}`);
                        
                        switch(tipoError) {
                            case 'bloqueado':
                                console.log("🚫 Usuario bloqueado");
                                mostrarMensaje('bloqueado', 'Tu cuenta ha sido bloqueada temporalmente. Intenta nuevamente en unos minutos.');
                                document.getElementById('usernameInput').disabled = true;
                                document.getElementById('passwordInput').disabled = true;
                                document.getElementById('submitBtn').disabled = true;
                                document.getElementById('submitBtn').textContent = 'Cuenta Bloqueada';
                                break;
                                
                            case 'credenciales':
                                console.log(`❌ Credenciales incorrectas. Intentos restantes: ${intentos}`);
                                mostrarMensaje('credenciales', '', intentos);
                                break;
                                
                            case 'requiere_captcha':
                                console.log("🛡️ Credenciales correctas, mostrando CAPTCHA");
                                mostrarCaptcha();
                                break;
                                
                            case 'captcha_incorrecto':
                                console.log("❌ CAPTCHA incorrecto");
                                mostrarMensaje('captcha_incorrecto', 'Código de verificación incorrecto. Intenta nuevamente.');
                                break;
                                
                            default:
                                console.log("❌ Error general:", data.error);
                                mostrarMensaje('sistema', data.error || 'Error desconocido');
                                break;
                        }
                        
                        document.getElementById('submitBtn').disabled = false;
                    }
                })
                .catch(error => {
                    console.error('💥 Error de conexión:', error);
                    mostrarMensaje('sistema', 'Error de conexión. Intenta nuevamente.');
                    document.getElementById('submitBtn').disabled = false;
                })
                .finally(() => {
                    document.getElementById('loading').style.display = 'none';
                });
            }

            // ✅ NUEVA FUNCIÓN: Validar y enviar CAPTCHA
            function validarYEnviarCaptcha() {
                const input = document.getElementById('captchaInput').value.trim();

                if (input === '' || input !== captchaCode) {
                    document.getElementById('captchaError').style.display = 'block';
                    document.getElementById('captchaInput').focus();
                    generarCaptcha();
                    return;
                }

                console.log("✅ CAPTCHA correcto - Enviando credenciales con CAPTCHA");
                ocultarCaptcha();
                enviarCredencialesConCaptcha();
            }

            // ✅ NUEVA FUNCIÓN: Enviar credenciales con CAPTCHA
            function enviarCredencialesConCaptcha() {
                console.log("🚀 Enviando credenciales con CAPTCHA...");

                const captchaInputValue = document.getElementById('captchaInput').value.trim();

                const params = new URLSearchParams();
                params.append('username', loginData.username);
                params.append('password', loginData.password);
                params.append('captchaInput', captchaInputValue);
                params.append('captchaHidden', captchaCode);

                fetch('LoginServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: params
                })
                .then(response => response.json())
                .then(data => {
                    console.log("📊 Respuesta del servidor con CAPTCHA:", data);
                    
                    if (data.success) {
                        if (data.redirect) {
                            console.log("✅ Login exitoso - Redirigiendo a:", data.redirect);
                            window.location.href = data.redirect;
                        } else {
                            window.location.reload();
                        }
                    } else {
                        const tipoError = data.tipoError || 'desconocido';
                        const intentos = data.intentosRestantes || 0;
                        
                        console.log(`🔍 Tipo error: ${tipoError}, Intentos: ${intentos}`);
                        
                        switch(tipoError) {
                            case 'bloqueado':
                                mostrarMensaje('bloqueado', 'Tu cuenta ha sido bloqueada temporalmente. Intenta nuevamente en unos minutos.');
                                document.getElementById('usernameInput').disabled = true;
                                document.getElementById('passwordInput').disabled = true;
                                document.getElementById('submitBtn').disabled = true;
                                document.getElementById('submitBtn').textContent = 'Cuenta Bloqueada';
                                break;
                                
                            case 'credenciales':
                                mostrarMensaje('credenciales', '', intentos);
                                break;
                                
                            case 'captcha_incorrecto':
                                mostrarMensaje('captcha_incorrecto', 'Código de verificación incorrecto. Intenta nuevamente.');
                                mostrarCaptcha();
                                break;
                                
                            default:
                                mostrarMensaje('sistema', data.error || 'Error desconocido');
                                break;
                        }
                        
                        document.getElementById('submitBtn').disabled = false;
                    }
                })
                .catch(error => {
                    console.error('💥 Error de conexión:', error);
                    mostrarMensaje('sistema', 'Error de conexión. Intenta nuevamente.');
                    document.getElementById('submitBtn').disabled = false;
                })
                .finally(() => {
                    document.getElementById('loading').style.display = 'none';
                });
            }

            // Cancelar login
            function cancelarLogin() {
                ocultarCaptcha();
                document.getElementById('loading').style.display = 'none';
                document.getElementById('submitBtn').disabled = false;
            }

            // Manejar envío del formulario
            document.getElementById('loginForm').addEventListener('submit', function (e) {
                e.preventDefault();

                <% if (estaBloqueado) { %>
                    return;
                <% } %>

                const username = document.getElementById('usernameInput').value.trim();
                const password = document.getElementById('passwordInput').value.trim();

                if (!username || !password) {
                    mostrarMensaje('sistema', 'Por favor, completa todos los campos.');
                    return;
                }

                loginData = {username, password};
                document.getElementById('submitBtn').disabled = true;
                document.getElementById('loading').style.display = 'block';

                // ✅ CORREGIDO: Enviar solo credenciales primero, sin CAPTCHA
                enviarCredenciales();
            });

            // Cerrar modal haciendo click fuera
            window.onclick = function (event) {
                const modal = document.getElementById('captchaModal');
                if (event.target === modal) {
                    cancelarLogin();
                }
            }

            // Generar CAPTCHA inicial
            window.onload = function () {
                generarCaptcha();
                <% if (!estaBloqueado) { %>
                    document.getElementById('submitBtn').disabled = false;
                <% } %>
            };
        </script>

        <% if (estaBloqueado) {%>
        <script>
            let tiempoRestante = <%= tiempoRestanteMs != null ? (int) Math.ceil(tiempoRestanteMs / 1000.0) : 60%>;
            const username = document.getElementById('usernameInput').value;

            console.log("⏰ Tiempo restante inicial:", tiempoRestante, "segundos");

            function actualizarTiempoDetalle() {
                const minutos = Math.floor(tiempoRestante / 60);
                const segundos = tiempoRestante % 60;
                
                const minutosElem = document.getElementById('minutos');
                const segundosElem = document.getElementById('segundos');
                
                if (minutosElem && segundosElem) {
                    minutosElem.textContent = minutos;
                    segundosElem.textContent = segundos.toString().padStart(2, '0');
                }
            }

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

            if (tiempoRestante > 0) {
                function actualizarTiempo() {
                    if (tiempoRestante <= 0) {
                        console.log("⏰ Tiempo completado, verificando estado...");
                        document.getElementById('tiempoTexto').textContent = '0';
                        actualizarTiempoDetalle();
                        setInterval(verificarEstadoBloqueo, 5000);
                        return;
                    }

                    const tiempoTexto = document.getElementById('tiempoTexto');
                    if (tiempoTexto) {
                        tiempoTexto.textContent = tiempoRestante;
                    }

                    actualizarTiempoDetalle();
                    tiempoRestante--;
                    setTimeout(actualizarTiempo, 1000);
                }

                console.log("🚀 Iniciando contador de desbloqueo...");
                actualizarTiempo();
                actualizarTiempoDetalle(); // Llamada inicial
            } else {
                setInterval(verificarEstadoBloqueo, 5000);
            }

            setInterval(verificarEstadoBloqueo, 10000);

        </script>
        <% }%>
    </body>
</html>