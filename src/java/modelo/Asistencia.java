package modelo;

import java.util.Date;

public class Asistencia {
    private int id;
    private int alumnoId;
    private int cursoId;
    private int turnoId;
    private String fecha;
    private String horaClase;
    private String estado; // PRESENTE, TARDANZA, AUSENTE, JUSTIFICADO
    private String observaciones;
    private int registradoPor;
    private Date fechaRegistro;
    private Date fechaActualizacion;
    private boolean activo;
    
    // Campos adicionales para mostrar en vistas
    private String alumnoNombre;
    private String cursoNombre;
    private String turnoNombre;
    private String profesorNombre;
    private String gradoNombre;

    // Constructores
    public Asistencia() {}
    
    public Asistencia(int alumnoId, int cursoId, int turnoId, String fecha, String horaClase, String estado) {
        this.alumnoId = alumnoId;
        this.cursoId = cursoId;
        this.turnoId = turnoId;
        this.fecha = fecha;
        this.horaClase = horaClase;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAlumnoId() { return alumnoId; }
    public void setAlumnoId(int alumnoId) { this.alumnoId = alumnoId; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public int getTurnoId() { return turnoId; }
    public void setTurnoId(int turnoId) { this.turnoId = turnoId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraClase() { return horaClase; }
    public void setHoraClase(String horaClase) { this.horaClase = horaClase; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(int registradoPor) { this.registradoPor = registradoPor; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getAlumnoNombre() { return alumnoNombre; }
    public void setAlumnoNombre(String alumnoNombre) { this.alumnoNombre = alumnoNombre; }

    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }

    public String getTurnoNombre() { return turnoNombre; }
    public void setTurnoNombre(String turnoNombre) { this.turnoNombre = turnoNombre; }

    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String profesorNombre) { this.profesorNombre = profesorNombre; }

    public String getGradoNombre() { return gradoNombre; }
    public void setGradoNombre(String gradoNombre) { this.gradoNombre = gradoNombre; }

    @Override
    public String toString() {
        return "Asistencia{" + "id=" + id + ", alumnoId=" + alumnoId + ", cursoId=" + cursoId + 
               ", turnoId=" + turnoId + ", fecha=" + fecha + ", horaClase=" + horaClase + 
               ", estado=" + estado + ", observaciones=" + observaciones + 
               ", registradoPor=" + registradoPor + ", alumnoNombre=" + alumnoNombre + 
               ", cursoNombre=" + cursoNombre + ", turnoNombre=" + turnoNombre + 
               ", profesorNombre=" + profesorNombre + ", gradoNombre=" + gradoNombre + '}';
    }
}