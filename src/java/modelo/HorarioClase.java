/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class HorarioClase {
    private int id;
    private int cursoId;
    private int turnoId;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private int aulaId;
    private boolean activo;
    
    // Campos adicionales para mostrar en vistas
    private String cursoNombre;
    private String turnoNombre;
    private String aulaNombre;
    private String sedeNombre;
    private String gradoNombre;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public int getTurnoId() { return turnoId; }
    public void setTurnoId(int turnoId) { this.turnoId = turnoId; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public int getAulaId() { return aulaId; }
    public void setAulaId(int aulaId) { this.aulaId = aulaId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }

    public String getTurnoNombre() { return turnoNombre; }
    public void setTurnoNombre(String turnoNombre) { this.turnoNombre = turnoNombre; }

    public String getAulaNombre() { return aulaNombre; }
    public void setAulaNombre(String aulaNombre) { this.aulaNombre = aulaNombre; }

    public String getSedeNombre() { return sedeNombre; }
    public void setSedeNombre(String sedeNombre) { this.sedeNombre = sedeNombre; }

    public String getGradoNombre() { return gradoNombre; }
    public void setGradoNombre(String gradoNombre) { this.gradoNombre = gradoNombre; }
}