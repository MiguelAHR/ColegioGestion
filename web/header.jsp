<%-- 
    Document   : header
    Created on : 1 may. 2025, 8:44:30 p. m.
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
    <style>
        .accessibility-btn {
            background: transparent;
            border: 1px solid rgba(255,255,255,0.5);
            color: white;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .accessibility-btn:hover {
            background: rgba(255,255,255,0.1);
            transform: scale(1.05);
        }
        .accessibility-modal .nav-link {
            color: #495057;
            font-weight: 500;
        }
        .accessibility-modal .nav-link.active {
            background-color: #0d6efd;
            color: white;
        }
        .contrast-preview {
            width: 30px;
            height: 30px;
            border-radius: 4px;
            display: inline-block;
            margin-right: 8px;
            border: 1px solid #ddd;
            cursor: pointer;
        }
        .contrast-normal { background: #ffffff; color: #000000; }
        .contrast-invert { background: #000000; color: #ffffff; }
        .contrast-yellow { background: #ffff00; color: #000000; }
        
        /* Estilos para tooltips */
        .info-tooltip {
            color: #6c757d;
            cursor: help;
            margin-left: 5px;
        }
        .info-tooltip:hover {
            color: #0d6efd;
        }
        
        /* Estilos para las configuraciones de accesibilidad */
        .reduce-motion * {
            animation-duration: 0.01ms !important;
            animation-iteration-count: 1 !important;
            transition-duration: 0.01ms !important;
        }
        
        .high-contrast-invert {
            filter: invert(1) hue-rotate(180deg);
        }
        
        .high-contrast-yellow {
            background-color: #000000 !important;
            color: #ffff00 !important;
        }
        
        .beige-background {
            background-color: #f5f5dc !important;
        }
        
        .large-text {
            font-size: 20px !important;
        }
        
        .larger-text {
            font-size: 24px !important;
        }
        
        .largest-text {
            font-size: 28px !important;
        }
        
        /* Indicador de voz */
        .voice-indicator {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background: #dc3545;
            color: white;
            border-radius: 50%;
            width: 60px;
            height: 60px;
            display: none;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            z-index: 10000;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            animation: pulse 1.5s infinite;
        }
        
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
        
        .voice-listening .voice-indicator {
            display: flex;
        }
        
        /* ESTILOS CORREGIDOS PARA DALTONISMO */
        .colorblind-protanopia {
            filter: url('#protanopia') !important;
        }
        
        .colorblind-deuteranopia {
            filter: url('#deuteranopia') !important;
        }
        
        .colorblind-tritanopia {
            filter: url('#tritanopia') !important;
        }
        
        .colorblind-achromatopsia {
            filter: grayscale(100%) contrast(150%) !important;
        }
        
        /* Asegurar que los filtros se apliquen correctamente */
        html.colorblind-protanopia,
        html.colorblind-deuteranopia,
        html.colorblind-tritanopia,
        html.colorblind-achromatopsia {
            width: 100% !important;
            height: 100% !important;
        }
        
        body.colorblind-protanopia,
        body.colorblind-deuteranopia,
        body.colorblind-tritanopia,
        body.colorblind-achromatopsia {
            width: 100% !important;
            min-height: 100vh !important;
            margin: 0 !important;
            padding: 0 !important;
            overflow-x: hidden !important;
        }
        
        /* Simulador de vista de daltonismo para previews */
        .colorblind-preview {
            width: 25px;
            height: 25px;
            border-radius: 4px;
            display: inline-block;
            margin-right: 8px;
            border: 1px solid #ddd;
            cursor: pointer;
            position: relative;
        }
        
        .colorblind-preview.protanopia {
            background: linear-gradient(45deg, #5e5e5e 0%, #004d00 50%, #000080 100%) !important;
        }
        
        .colorblind-preview.deuteranopia {
            background: linear-gradient(45deg, #8a8a8a 0%, #006600 50%, #191974 100%) !important;
        }
        
        .colorblind-preview.tritanopia {
            background: linear-gradient(45deg, #ffff80 0%, #ff80ff 50%, #8080ff 100%) !important;
        }
        
        .colorblind-preview.achromatopsia {
            background: linear-gradient(45deg, #404040 0%, #808080 50%, #c0c0c0 100%) !important;
        }
        
        .colorblind-option {
            border: 2px solid transparent;
            border-radius: 8px;
            padding: 10px;
            margin: 5px 0;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .colorblind-option:hover {
            background-color: #f8f9fa;
            border-color: #dee2e6;
        }
        
        .colorblind-option.selected {
            background-color: #e3f2fd;
            border-color: #2196f3;
        }
        
        .colorblind-description {
            font-size: 0.85em;
            color: #6c757d;
            margin-top: 5px;
        }

        /* Solución alternativa para navegadores que no soportan bien SVG filters */
        .colorblind-protanopia-alt {
            filter: sepia(0.3) saturate(0.5) hue-rotate(-20deg) !important;
        }
        
        .colorblind-deuteranopia-alt {
            filter: sepia(0.3) saturate(0.5) hue-rotate(25deg) !important;
        }
        
        .colorblind-tritanopia-alt {
            filter: sepia(0.2) saturate(2) hue-rotate(150deg) !important;
        }
    </style>
</head>
<body>
    <!-- Filtros SVG para daltonismo - POSICIÓN CORREGIDA -->
    <svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="position: absolute; width: 0; height: 0; overflow: hidden;">
        <defs>
            <!-- Filtro para Protanopia (ceguera al rojo) -->
            <filter id="protanopia" x="0" y="0" width="100%" height="100%">
                <feColorMatrix type="matrix" values="0.567, 0.433, 0, 0, 0 
                                                     0.558, 0.442, 0, 0, 0 
                                                     0, 0.242, 0.758, 0, 0 
                                                     0, 0, 0, 1, 0" />
            </filter>
            
            <!-- Filtro para Deuteranopia (ceguera al verde) -->
            <filter id="deuteranopia" x="0" y="0" width="100%" height="100%">
                <feColorMatrix type="matrix" values="0.625, 0.375, 0, 0, 0 
                                                     0.7, 0.3, 0, 0, 0 
                                                     0, 0.3, 0.7, 0, 0 
                                                     0, 0, 0, 1, 0" />
            </filter>
            
            <!-- Filtro para Tritanopia (ceguera al azul) -->
            <filter id="tritanopia" x="0" y="0" width="100%" height="100%">
                <feColorMatrix type="matrix" values="0.95, 0.05, 0, 0, 0 
                                                     0, 0.433, 0.567, 0, 0 
                                                     0, 0.475, 0.525, 0, 0 
                                                     0, 0, 0, 1, 0" />
            </filter>
        </defs>
    </svg>

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
                    <% if ("docente".equals(request.getSession().getAttribute("rol"))) { %>
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
                    <% } %>
                </ul>

                <div class="d-flex align-items-center">
                    <span class="navbar-text text-light me-3">
                        <%= (request.getSession().getAttribute("usuario") != null) ? request.getSession().getAttribute("usuario") : "Invitado"%>
                    </span>
                    
                    <!-- Botón de accesibilidad -->
                    <button class="accessibility-btn me-2" id="accessibilityToggle" title="Configuración de accesibilidad">
                        <i class="fas fa-universal-access"></i>
                    </button>
                    
                    <a class="btn btn-outline-light btn-sm" href="LogoutServlet">Cerrar sesión</a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Modal de Configuración de Accesibilidad -->
    <div class="modal fade accessibility-modal" id="accessibilityModal" tabindex="-1" aria-labelledby="accessibilityModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="accessibilityModalLabel">
                        <i class="fas fa-universal-access me-2"></i>Panel de Accesibilidad
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <ul class="nav nav-pills mb-3" id="accessibilityTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="visual-tab" data-bs-toggle="pill" data-bs-target="#visual" type="button" role="tab" aria-controls="visual" aria-selected="true">Visual</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="navigation-tab" data-bs-toggle="pill" data-bs-target="#navigation" type="button" role="tab" aria-controls="navigation" aria-selected="false">Navegación</button>
                        </li>
                    </ul>
                    
                    <div class="tab-content" id="accessibilityTabContent">
                        <!-- Pestaña Visual -->
                        <div class="tab-pane fade show active" id="visual" role="tabpanel" aria-labelledby="visual-tab">
                            <div class="mb-3">
                                <label class="form-label">
                                    Tamaño de texto
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Ajusta el tamaño del texto para mejorar la legibilidad. Recomendado para personas con baja visión."></i>
                                </label>
                                <input type="range" class="form-range" id="fontSize" min="16" max="28" value="16">
                                <div class="d-flex justify-content-between">
                                    <small>16px</small>
                                    <small id="currentFontSize">16px</small>
                                    <small>28px</small>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">
                                    Tipo de fuente
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="OpenDyslexic es una fuente especial diseñada para personas con dislexia. Ayuda a reducir los errores de lectura."></i>
                                </label>
                                <select class="form-select" id="fontType">
                                    <option value="default">Fuente predeterminada</option>
                                    <option value="opendyslexic">OpenDyslexic</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">
                                    Esquema de contraste
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Mejora el contraste entre texto y fondo para personas con daltonismo o baja visión."></i>
                                </label>
                                <div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="contrastScheme" id="contrastNormal" value="normal" checked>
                                        <label class="form-check-label" for="contrastNormal">
                                            <span class="contrast-preview contrast-normal">A</span> Normal
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="contrastScheme" id="contrastInvert" value="invert">
                                        <label class="form-check-label" for="contrastInvert">
                                            <span class="contrast-preview contrast-invert">A</span> Invertido
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="contrastScheme" id="contrastYellow" value="yellow">
                                        <label class="form-check-label" for="contrastYellow">
                                            <span class="contrast-preview contrast-yellow">A</span> Amarillo/Negro
                                        </label>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- SECCIÓN CORREGIDA: Modos de Daltonismo -->
                            <div class="mb-3">
                                <label class="form-label">
                                    Modo para Daltonismo
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Ajusta los colores para diferentes tipos de daltonismo. Estos filtros ayudan a distinguir colores que normalmente serían confusos."></i>
                                </label>
                                
                                <div class="colorblind-options">
                                    <div class="colorblind-option" data-type="normal">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="colorblindMode" id="colorblindNormal" value="normal" checked>
                                            <label class="form-check-label" for="colorblindNormal">
                                                <span class="colorblind-preview contrast-normal">A</span> 
                                                <strong>Visión Normal</strong>
                                            </label>
                                        </div>
                                        <div class="colorblind-description">
                                            Sin ajustes para daltonismo
                                        </div>
                                    </div>
                                    
                                    <div class="colorblind-option" data-type="protanopia">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="colorblindMode" id="colorblindProtanopia" value="protanopia">
                                            <label class="form-check-label" for="colorblindProtanopia">
                                                <span class="colorblind-preview protanopia">A</span> 
                                                <strong>Protanopia</strong>
                                            </label>
                                        </div>
                                        <div class="colorblind-description">
                                            Ceguera al rojo - Dificultad para distinguir rojos y verdes
                                        </div>
                                    </div>
                                    
                                    <div class="colorblind-option" data-type="deuteranopia">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="colorblindMode" id="colorblindDeuteranopia" value="deuteranopia">
                                            <label class="form-check-label" for="colorblindDeuteranopia">
                                                <span class="colorblind-preview deuteranopia">A</span> 
                                                <strong>Deuteranopia</strong>
                                            </label>
                                        </div>
                                        <div class="colorblind-description">
                                            Ceguera al verde - Forma más común de daltonismo rojo-verde
                                        </div>
                                    </div>
                                    
                                    <div class="colorblind-option" data-type="tritanopia">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="colorblindMode" id="colorblindTritanopia" value="tritanopia">
                                            <label class="form-check-label" for="colorblindTritanopia">
                                                <span class="colorblind-preview tritanopia">A</span> 
                                                <strong>Tritanopia</strong>
                                            </label>
                                        </div>
                                        <div class="colorblind-description">
                                            Ceguera al azul - Dificultad para distinguir azules y amarillos
                                        </div>
                                    </div>
                                    
                                    <div class="colorblind-option" data-type="achromatopsia">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="colorblindMode" id="colorblindAchromatopsia" value="achromatopsia">
                                            <label class="form-check-label" for="colorblindAchromatopsia">
                                                <span class="colorblind-preview achromatopsia">A</span> 
                                                <strong>Acromatopsia</strong>
                                            </label>
                                        </div>
                                        <div class="colorblind-description">
                                            Visión en escala de grises - Incapacidad para percibir cualquier color
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3 form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="reduceAnimations">
                                <label class="form-check-label" for="reduceAnimations">
                                    Reducir animaciones
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Elimina o reduce las animaciones y transiciones. Recomendado para personas con epilepsia fotosensible o que se distraen fácilmente."></i>
                                </label>
                            </div>
                            
                            <div class="mb-3 form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="beigeBackground">
                                <label class="form-check-label" for="beigeBackground">
                                    Fondo beige para lectura
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Cambia el fondo a color beige suave para reducir el estrés visual durante la lectura prolongada."></i>
                                </label>
                            </div>
                        </div>
                        
                        <!-- Pestaña Navegación (Simplificada) -->
                        <div class="tab-pane fade" id="navigation" role="tabpanel" aria-labelledby="navigation-tab">
                            <div class="mb-3 form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="keyboardNavigation">
                                <label class="form-check-label" for="keyboardNavigation">
                                    Navegación exclusiva por teclado
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Permite navegar por todo el sistema usando solo el teclado. Ideal para personas con movilidad reducida."></i>
                                </label>
                            </div>
                            
                            <div class="mb-3 form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="voiceControl">
                                <label class="form-check-label" for="voiceControl">
                                    Habilitar control por voz
                                    <i class="fas fa-info-circle info-tooltip" data-bs-toggle="tooltip" title="Activa el reconocimiento de voz para navegar y realizar acciones usando comandos de voz. Requiere micrófono."></i>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="resetAccessibility">Restablecer</button>
                    <button type="button" class="btn btn-primary" id="saveAccessibility">Guardar configuración</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Indicador de reconocimiento de voz -->
    <div class="voice-indicator" id="voiceIndicator" title="Haz clic para detener el reconocimiento de voz">
        <i class="fas fa-microphone fa-lg"></i>
    </div>

    <script>
        // Sistema de reconocimiento de voz
        class VoiceControlSystem {
            constructor() {
                this.recognition = null;
                this.isListening = false;
                this.commands = {
                    'ir a alumnos': () => this.navigateTo('AlumnoServlet'),
                    'ir a profesores': () => this.navigateTo('ProfesorServlet'),
                    'ir a cursos': () => this.navigateTo('CursoServlet'),
                    'ir a grados': () => this.navigateTo('GradoServlet'),
                    'ir a usuarios': () => this.navigateTo('UsuarioServlet'),
                    'ir a dashboard': () => this.navigateTo('dashboard.jsp'),
                    'abrir menú': () => this.toggleMenu(),
                    'cerrar sesión': () => this.logout(),
                    'ayuda': () => this.showHelp(),
                    'leer página': () => this.readPage(),
                    'activar protanopia': () => this.setColorblindMode('protanopia'),
                    'activar deuteranopia': () => this.setColorblindMode('deuteranopia'),
                    'activar tritanopia': () => this.setColorblindMode('tritanopia'),
                    'activar escala de grises': () => this.setColorblindMode('achromatopsia'),
                    'desactivar daltonismo': () => this.setColorblindMode('normal')
                };
                
                this.init();
            }
            
            init() {
                // Verificar compatibilidad con Web Speech API
                if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
                    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
                    this.recognition = new SpeechRecognition();
                    
                    this.recognition.continuous = true;
                    this.recognition.interimResults = false;
                    this.recognition.lang = 'es-ES';
                    
                    this.recognition.onstart = () => {
                        console.log('Reconocimiento de voz activado');
                        this.isListening = true;
                        document.body.classList.add('voice-listening');
                    };
                    
                    this.recognition.onresult = (event) => {
                        const transcript = event.results[event.results.length - 1][0].transcript.toLowerCase().trim();
                        console.log('Comando detectado:', transcript);
                        this.processCommand(transcript);
                    };
                    
                    this.recognition.onerror = (event) => {
                        console.error('Error en reconocimiento de voz:', event.error);
                    };
                    
                    this.recognition.onend = () => {
                        if (this.isListening) {
                            setTimeout(() => {
                                if (this.isListening) {
                                    this.recognition.start();
                                }
                            }, 1000);
                        }
                    };
                }
            }
            
            startListening() {
                if (this.recognition && !this.isListening) {
                    try {
                        this.recognition.start();
                    } catch (error) {
                        console.error('Error al iniciar reconocimiento de voz:', error);
                    }
                }
            }
            
            stopListening() {
                if (this.recognition && this.isListening) {
                    this.isListening = false;
                    this.recognition.stop();
                    document.body.classList.remove('voice-listening');
                }
            }
            
            processCommand(transcript) {
                let commandExecuted = false;
                
                for (const [command, action] of Object.entries(this.commands)) {
                    if (transcript.includes(command)) {
                        action();
                        commandExecuted = true;
                        break;
                    }
                }
            }
            
            navigateTo(page) {
                window.location.href = page;
            }
            
            toggleMenu() {
                const navbarToggler = document.querySelector('.navbar-toggler');
                if (navbarToggler) {
                    navbarToggler.click();
                }
            }
            
            logout() {
                window.location.href = 'LogoutServlet';
            }
            
            showHelp() {
                const commandsList = Object.keys(this.commands).join('\n• ');
                alert(`Comandos de voz disponibles:\n\n• ${commandsList}`);
            }
            
            readPage() {
                const mainContent = document.querySelector('h1, h2, .container') || document.body;
                const text = mainContent.innerText || mainContent.textContent;
                this.speak(text.substring(0, 200) + '...');
            }
            
            setColorblindMode(mode) {
                const settings = JSON.parse(localStorage.getItem('accessibilitySettings')) || {};
                settings.colorblindMode = mode;
                localStorage.setItem('accessibilitySettings', JSON.stringify(settings));
                applyAccessibilitySettings(settings);
            }
            
            speak(text) {
                if ('speechSynthesis' in window) {
                    const utterance = new SpeechSynthesisUtterance(text);
                    utterance.lang = 'es-ES';
                    utterance.rate = 1.0;
                    utterance.pitch = 1.0;
                    speechSynthesis.speak(utterance);
                }
            }
        }

        // Inicializar sistema de voz
        let voiceSystem = null;

        // FUNCIÓN CORREGIDA PARA APLICAR CONFIGURACIÓN
        function applyAccessibilitySettings(settings) {
            try {
                console.log('Aplicando configuración:', settings);
                
                // Remover TODAS las clases de accesibilidad
                const classesToRemove = [
                    'large-text', 'larger-text', 'largest-text',
                    'high-contrast-invert', 'high-contrast-yellow',
                    'beige-background', 'reduce-motion',
                    'colorblind-protanopia', 'colorblind-deuteranopia',
                    'colorblind-tritanopia', 'colorblind-achromatopsia',
                    'colorblind-protanopia-alt', 'colorblind-deuteranopia-alt', 'colorblind-tritanopia-alt'
                ];
                
                classesToRemove.forEach(className => {
                    document.body.classList.remove(className);
                });
                
                // Resetear estilos inline
                document.body.style.filter = '';
                document.body.style.fontFamily = '';
                document.documentElement.style.fontSize = '';

                // 1. Aplicar tamaño de fuente
                if (settings.fontSize) {
                    const fontSize = parseInt(settings.fontSize);
                    console.log('Aplicando tamaño de fuente:', fontSize);
                    
                    if (fontSize >= 20 && fontSize < 24) {
                        document.body.classList.add('large-text');
                    } else if (fontSize >= 24 && fontSize < 28) {
                        document.body.classList.add('larger-text');
                    } else if (fontSize >= 28) {
                        document.body.classList.add('largest-text');
                    }
                }

                // 2. Aplicar tipo de fuente
                if (settings.fontType === 'opendyslexic') {
                    document.body.style.fontFamily = '"Comic Sans MS", cursive, Arial, sans-serif';
                }

                // 3. Aplicar esquema de contraste
                if (settings.contrastScheme === 'invert') {
                    document.body.classList.add('high-contrast-invert');
                } else if (settings.contrastScheme === 'yellow') {
                    document.body.classList.add('high-contrast-yellow');
                }

                // 4. APLICAR MODO DALTONISMO - CORREGIDO (usando concatenación en lugar de template literals)
                if (settings.colorblindMode && settings.colorblindMode !== 'normal') {
                    console.log('Aplicando modo daltonismo:', settings.colorblindMode);
                    
                    // Aplicar la clase principal - CORREGIDO
                    document.body.classList.add('colorblind-' + settings.colorblindMode);
                    
                    // Para navegadores problemáticos, aplicar también la versión alternativa
                    if (settings.colorblindMode !== 'achromatopsia') {
                        document.body.classList.add('colorblind-' + settings.colorblindMode + '-alt');
                    }
                    
                    // Forzar reflow para asegurar la aplicación
                    void document.body.offsetHeight;
                }

                // 5. Aplicar fondo beige
                if (settings.beigeBackground) {
                    document.body.classList.add('beige-background');
                }

                // 6. Aplicar reducción de animaciones
                if (settings.reduceAnimations) {
                    document.body.classList.add('reduce-motion');
                }

                // 7. Control por voz
                if (settings.voiceControl) {
                    if (!voiceSystem) {
                        voiceSystem = new VoiceControlSystem();
                    }
                    voiceSystem.startListening();
                } else if (voiceSystem) {
                    voiceSystem.stopListening();
                }

                console.log('Clases actuales del body:', document.body.className);

            } catch (error) {
                console.error('Error al aplicar configuración:', error);
            }
        }

        // Función para cargar configuración desde localStorage - CORREGIDA
        function loadAccessibilitySettings() {
            try {
                const settings = JSON.parse(localStorage.getItem('accessibilitySettings')) || {};
                console.log('Cargando configuración:', settings);
                
                // Configuración Visual - Solo elementos que existen
                const fontSizeElement = document.getElementById('fontSize');
                const currentFontSizeElement = document.getElementById('currentFontSize');
                if (settings.fontSize && fontSizeElement && currentFontSizeElement) {
                    fontSizeElement.value = settings.fontSize;
                    currentFontSizeElement.textContent = settings.fontSize + 'px';
                }
                
                const fontTypeElement = document.getElementById('fontType');
                if (settings.fontType && fontTypeElement) {
                    fontTypeElement.value = settings.fontType;
                }
                
                if (settings.contrastScheme) {
                    const radio = document.querySelector('input[name="contrastScheme"][value="' + settings.contrastScheme + '"]');
                    if (radio) radio.checked = true;
                }
                
                // Configuración Daltonismo
                if (settings.colorblindMode) {
                    const radio = document.querySelector('input[name="colorblindMode"][value="' + settings.colorblindMode + '"]');
                    if (radio) {
                        radio.checked = true;
                        
                        // Actualizar UI visual
                        document.querySelectorAll('.colorblind-option').forEach(option => {
                            option.classList.remove('selected');
                        });
                        const selectedOption = document.querySelector('.colorblind-option[data-type="' + settings.colorblindMode + '"]');
                        if (selectedOption) {
                            selectedOption.classList.add('selected');
                        }
                    }
                }
                
                // Solo configurar elementos que existen
                const reduceAnimationsElement = document.getElementById('reduceAnimations');
                if (reduceAnimationsElement && settings.reduceAnimations !== undefined) {
                    reduceAnimationsElement.checked = settings.reduceAnimations;
                }
                
                const beigeBackgroundElement = document.getElementById('beigeBackground');
                if (beigeBackgroundElement && settings.beigeBackground !== undefined) {
                    beigeBackgroundElement.checked = settings.beigeBackground;
                }
                
                const keyboardNavigationElement = document.getElementById('keyboardNavigation');
                if (keyboardNavigationElement && settings.keyboardNavigation !== undefined) {
                    keyboardNavigationElement.checked = settings.keyboardNavigation;
                }
                
                const voiceControlElement = document.getElementById('voiceControl');
                if (voiceControlElement && settings.voiceControl !== undefined) {
                    voiceControlElement.checked = settings.voiceControl;
                    if (settings.voiceControl && !voiceSystem) {
                        voiceSystem = new VoiceControlSystem();
                        voiceSystem.startListening();
                    }
                }
                
                // Aplicar configuración cargada
                applyAccessibilitySettings(settings);
            } catch (error) {
                console.error('Error al cargar configuración:', error);
            }
        }
        
        // Función para guardar configuración en localStorage - CORREGIDA
        function saveAccessibilitySettings() {
            try {
                // Obtener valores del formulario
                const fontSize = document.getElementById('fontSize').value;
                const fontType = document.getElementById('fontType').value;
                
                const contrastSchemeElement = document.querySelector('input[name="contrastScheme"]:checked');
                const contrastScheme = contrastSchemeElement ? contrastSchemeElement.value : 'normal';
                
                const colorblindModeElement = document.querySelector('input[name="colorblindMode"]:checked');
                const colorblindMode = colorblindModeElement ? colorblindModeElement.value : 'normal';
                
                const reduceAnimations = document.getElementById('reduceAnimations').checked;
                const beigeBackground = document.getElementById('beigeBackground').checked;
                const keyboardNavigation = document.getElementById('keyboardNavigation').checked;
                const voiceControl = document.getElementById('voiceControl').checked;
                
                const settings = {
                    // Visual
                    fontSize: fontSize,
                    fontType: fontType,
                    contrastScheme: contrastScheme,
                    colorblindMode: colorblindMode,
                    reduceAnimations: reduceAnimations,
                    beigeBackground: beigeBackground,
                    
                    // Navegación
                    keyboardNavigation: keyboardNavigation,
                    voiceControl: voiceControl
                };
                
                console.log('Guardando configuración:', settings);
                localStorage.setItem('accessibilitySettings', JSON.stringify(settings));
                
                // Aplicar configuración inmediatamente
                applyAccessibilitySettings(settings);
                
                // Cerrar modal después de guardar
                const modal = bootstrap.Modal.getInstance(document.getElementById('accessibilityModal'));
                if (modal) {
                    modal.hide();
                }
                
                // Mostrar mensaje de confirmación
                setTimeout(() => {
                    alert('Configuración de accesibilidad guardada correctamente.');
                }, 300);
                
            } catch (error) {
                console.error('Error al guardar configuración:', error);
                alert('Error al guardar la configuración. Por favor, intenta nuevamente.');
            }
        }
        
        // Función para restablecer configuración - CORREGIDA
        function resetAccessibilitySettings() {
            if (confirm('¿Estás seguro de que deseas restablecer toda la configuración de accesibilidad?')) {
                try {
                    localStorage.removeItem('accessibilitySettings');
                    
                    // Restablecer valores por defecto en el formulario
                    document.getElementById('fontSize').value = 16;
                    document.getElementById('currentFontSize').textContent = '16px';
                    document.getElementById('fontType').value = 'default';
                    
                    // Restablecer radios
                    document.getElementById('contrastNormal').checked = true;
                    document.getElementById('colorblindNormal').checked = true;
                    
                    // Restablecer checkboxes
                    const reduceAnimationsElement = document.getElementById('reduceAnimations');
                    if (reduceAnimationsElement) reduceAnimationsElement.checked = false;
                    
                    const beigeBackgroundElement = document.getElementById('beigeBackground');
                    if (beigeBackgroundElement) beigeBackgroundElement.checked = false;
                    
                    const keyboardNavigationElement = document.getElementById('keyboardNavigation');
                    if (keyboardNavigationElement) keyboardNavigationElement.checked = false;
                    
                    const voiceControlElement = document.getElementById('voiceControl');
                    if (voiceControlElement) voiceControlElement.checked = false;
                    
                    // Actualizar UI visual para daltonismo
                    document.querySelectorAll('.colorblind-option').forEach(option => {
                        option.classList.remove('selected');
                    });
                    const normalOption = document.querySelector('.colorblind-option[data-type="normal"]');
                    if (normalOption) normalOption.classList.add('selected');
                    
                    // Aplicar valores por defecto
                    applyAccessibilitySettings({});
                    
                    // Detener reconocimiento de voz si estaba activo
                    if (voiceSystem) {
                        voiceSystem.stopListening();
                    }
                    
                    alert('Configuración restablecida correctamente.');
                    
                } catch (error) {
                    console.error('Error al restablecer configuración:', error);
                    alert('Error al restablecer la configuración.');
                }
            }
        }
        
        // Inicializar cuando el DOM esté listo
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Inicializando sistema de accesibilidad...');
            
            // Inicializar tooltips de Bootstrap
            const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
            
            // Configurar interacción para opciones de daltonismo
            document.querySelectorAll('.colorblind-option').forEach(option => {
                option.addEventListener('click', function() {
                    const radio = this.querySelector('input[type="radio"]');
                    if (radio) {
                        radio.checked = true;
                        
                        // Actualizar UI visual
                        document.querySelectorAll('.colorblind-option').forEach(opt => {
                            opt.classList.remove('selected');
                        });
                        this.classList.add('selected');
                    }
                });
            });
            
            // Cargar configuración al iniciar
            loadAccessibilitySettings();
            
            // Configurar evento para el botón de accesibilidad
            const accessibilityToggle = document.getElementById('accessibilityToggle');
            if (accessibilityToggle) {
                accessibilityToggle.addEventListener('click', function() {
                    console.log('Abriendo modal de accesibilidad...');
                    const modalElement = document.getElementById('accessibilityModal');
                    if (modalElement) {
                        const modal = new bootstrap.Modal(modalElement);
                        modal.show();
                    }
                });
            }
            
            // Actualizar valor de tamaño de fuente en tiempo real
            const fontSizeSlider = document.getElementById('fontSize');
            if (fontSizeSlider) {
                fontSizeSlider.addEventListener('input', function() {
                    const currentSizeElement = document.getElementById('currentFontSize');
                    if (currentSizeElement) {
                        currentSizeElement.textContent = this.value + 'px';
                    }
                });
            }
            
            // Configurar botones de guardar y restablecer
            const saveButton = document.getElementById('saveAccessibility');
            if (saveButton) {
                saveButton.addEventListener('click', saveAccessibilitySettings);
            }
            
            const resetButton = document.getElementById('resetAccessibility');
            if (resetButton) {
                resetButton.addEventListener('click', resetAccessibilitySettings);
            }
            
            // Configurar indicador de voz
            const voiceIndicator = document.getElementById('voiceIndicator');
            if (voiceIndicator) {
                voiceIndicator.addEventListener('click', function() {
                    if (voiceSystem) {
                        voiceSystem.stopListening();
                    }
                });
            }
            
            console.log('Sistema de accesibilidad inicializado correctamente');
        });
    </script>
</body>