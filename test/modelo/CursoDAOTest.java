package modelo;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CursoDAOTest {

    private final CursoDAO dao = new CursoDAO();

    @Test
    public void testListarTodos() {
        List<Curso> cursos = dao.listar();
        assertNotNull(cursos);
        assertTrue("Debe haber cursos en la BD", cursos.size() > 0);
        System.out.println("✅ Cursos totales encontrados: " + cursos.size());
    }

    @Test
    public void testListarPorGrado() {
        int gradoId = 15; // ejemplo: 1ero Primaria
        List<Curso> cursos = dao.listarPorGrado(gradoId);
        assertNotNull(cursos);
        System.out.println("📘 Cursos encontrados en grado Id " + gradoId + ": " + cursos.size());
        for (Curso c : cursos) {
            System.out.println(" - " + c.getNombre() + " (" + c.getGradoNombre() + ")");
        }
    }

    @Test
    public void testListarPorProfesor() {
        int profesorId = 6; // ejemplo: Nick Flores
        List<Curso> cursos = dao.listarPorProfesor(profesorId);
        assertNotNull(cursos);
        System.out.println("👨‍🏫 Cursos dictados por profesor " + profesorId + ": " + cursos.size());
        for (Curso c : cursos) {
            System.out.println(" - " + c.getNombre() + " (" + c.getGradoNombre() + ")");
        }
    }

    @Test
    public void testObtenerPorId() {
        int idCurso = 14; // ejemplo: Historia
        Curso c = dao.obtenerPorId(idCurso);
        assertNotNull("Debe existir el curso con ID " + idCurso, c);
        System.out.println("📌 Curso encontrado: " + c.getNombre() + " - " + c.getGradoNombre());
    }

    @Test
    public void testCRUD() {
        // 1️⃣ Crear
        Curso nuevo = new Curso();
        nuevo.setNombre("Curso JUnit Test");
        nuevo.setGradoId(15);   // 1ero Primaria
        nuevo.setProfesorId(5); // Nick Flores
        nuevo.setCreditos(2);

        boolean creado = dao.agregar(nuevo);
        assertTrue("El curso debería haberse creado", creado);
        System.out.println("✅ Curso creado: " + nuevo.getNombre());

        // 2️⃣ Listar para obtener el ID recién insertado
        List<Curso> cursos = dao.listarPorGrado(15);
        int nuevoId = cursos.stream()
                .filter(c -> c.getNombre().equals("Curso JUnit Test"))
                .mapToInt(Curso::getId)
                .findFirst()
                .orElse(-1);

        assertTrue("El curso insertado debe existir en la BD", nuevoId > 0);

        // 3️⃣ Actualizar
        Curso actualizado = new Curso();
        actualizado.setId(nuevoId);
        actualizado.setNombre("Curso JUnit Test Actualizado");
        actualizado.setGradoId(15);
        actualizado.setProfesorId(5);
        actualizado.setCreditos(3);

        boolean modificado = dao.actualizar(actualizado);
        assertTrue("El curso debería haberse actualizado", modificado);
        System.out.println("✏️ Curso actualizado con ID " + nuevoId);

        // 4️⃣ Eliminar
        boolean eliminado = dao.eliminar(nuevoId);
        assertTrue("El curso debería haberse eliminado", eliminado);
        System.out.println("🗑️ Curso eliminado con ID " + nuevoId);
    }
}
