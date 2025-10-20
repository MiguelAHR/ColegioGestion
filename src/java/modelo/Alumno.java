package modelo;

public class Alumno {
    private int id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String fechaNacimiento;
    private int gradoId;
    private String gradoNombre; // ✅ Campo adicional para compatibilidad

    // Constructores
    public Alumno() {}
    
    public Alumno(int id, String nombres, String apellidos, int gradoId) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.gradoId = gradoId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public int getGradoId() { return gradoId; }
    public void setGradoId(int gradoId) { this.gradoId = gradoId; }

    public String getGradoNombre() { return gradoNombre; }
    public void setGradoNombre(String gradoNombre) { this.gradoNombre = gradoNombre; }

    @Override
    public String toString() {
        return "Alumno{" + "id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos + 
               ", gradoId=" + gradoId + ", gradoNombre=" + gradoNombre + '}';
    }
}