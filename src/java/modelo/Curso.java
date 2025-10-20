package modelo;

public class Curso {

    private int id;
    private String nombre;
    private String descripcion;
    private int creditos;
    private int gradoId;
    private int profesorId;
    private String gradoNombre;
    private String profesorNombre;
    private String nivel;

    // Constructores
    public Curso() {}
    
    public Curso(int id, String nombre, int gradoId, int profesorId, int creditos) {
        this.id = id;
        this.nombre = nombre;
        this.gradoId = gradoId;
        this.profesorId = profesorId;
        this.creditos = creditos;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getGradoId() {
        return gradoId;
    }

    public void setGradoId(int gradoId) {
        this.gradoId = gradoId;
    }

    public int getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(int profesorId) {
        this.profesorId = profesorId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }
    
    public String getNivel() {
        return nivel;
    }
    
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return "Curso{" + "id=" + id + ", nombre=" + nombre + ", gradoId=" + gradoId + 
               ", profesorId=" + profesorId + ", creditos=" + creditos + 
               ", gradoNombre=" + gradoNombre + ", profesorNombre=" + profesorNombre + '}';
    }
}