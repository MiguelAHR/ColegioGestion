package modelo;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class AsistenciaDAO {

    public boolean registrarAsistencia(Asistencia a) {
        String sql = "{CALL registrar_asistencia(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, a.getAlumnoId());
            cs.setInt(2, a.getCursoId());
            cs.setInt(3, a.getTurnoId());
            cs.setString(4, a.getFecha());
            cs.setString(5, a.getHoraClase());
            cs.setString(6, a.getEstado());
            cs.setString(7, a.getObservaciones());
            cs.setInt(8, a.getRegistradoPor());

            int resultado = cs.executeUpdate();
            System.out.println("✅ Asistencia registrada. Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al registrar asistencia:");
            System.out.println("   Código: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error general al registrar asistencia");
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarAsistenciaGrupal(int cursoId, int turnoId, String fecha, String horaClase, String alumnosJson, int registradoPor) {
        System.out.println("=== 🟡 INICIANDO DAO REGISTRO GRUPAL ===");
        System.out.println("   cursoId: " + cursoId);
        System.out.println("   turnoId: " + turnoId);
        System.out.println("   fecha: " + fecha);
        System.out.println("   horaClase: " + horaClase);
        System.out.println("   registradoPor: " + registradoPor);

        Connection con = null;
        CallableStatement cs = null;

        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            // Validar JSON
            if (alumnosJson == null || alumnosJson.trim().isEmpty()) {
                System.out.println("❌ ERROR: JSON de alumnos está vacío");
                return false;
            }

            String jsonContent = alumnosJson.trim();
            if (!jsonContent.startsWith("[") || !jsonContent.endsWith("]")) {
                System.out.println("❌ ERROR: Formato JSON inválido - no es un array");
                return false;
            }

            // Parsear JSON manualmente
            String contenido = jsonContent.substring(1, jsonContent.length() - 1);
            String[] objetos = contenido.split("\\},\\{");

            System.out.println("📊 Total de alumnos a procesar: " + objetos.length);

            // ✅ CORREGIDO: Preparar el stored procedure con 8 parámetros
            String sql = "{CALL registrar_asistencia(?, ?, ?, ?, ?, ?, ?, ?)}";
            cs = con.prepareCall(sql);

            int exitosos = 0;
            int errores = 0;

            for (int i = 0; i < objetos.length; i++) {
                String objeto = objetos[i];
                // Limpiar el objeto
                if (i == 0) {
                    objeto = objeto.substring(1); // Quitar { inicial del primer objeto
                }
                if (i == objetos.length - 1) {
                    objeto = objeto.substring(0, objeto.length() - 1); // Quitar } final del último objeto
                }
                // Parsear manualmente
                int alumnoId = 0;
                String estado = "";

                try {
                    String[] propiedades = objeto.split(",");
                    for (String prop : propiedades) {
                        String[] keyValue = prop.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].replace("\"", "").trim();
                            String value = keyValue[1].replace("\"", "").trim();

                            if ("alumno_id".equals(key)) {
                                alumnoId = Integer.parseInt(value);
                            } else if ("estado".equals(key)) {
                                estado = value;
                            }
                        }
                    }

                    System.out.println("   👤 Procesando alumno " + alumnoId + " - Estado: " + estado);

                    // Validar datos
                    if (alumnoId <= 0 || estado.isEmpty()) {
                        System.out.println("   ⚠️  Datos inválidos para alumno, saltando...");
                        errores++;
                        continue;
                    }

                    // ✅ CORREGIDO: Ejecutar stored procedure con 8 parámetros
                    cs.setInt(1, alumnoId);
                    cs.setInt(2, cursoId);
                    cs.setInt(3, turnoId);
                    cs.setString(4, fecha);
                    cs.setString(5, horaClase);
                    cs.setString(6, estado);
                    cs.setString(7, ""); // Observaciones vacías para registro grupal
                    cs.setInt(8, registradoPor);

                    int resultado = cs.executeUpdate();
                    if (resultado > 0) {
                        exitosos++;
                        System.out.println("   💾 Alumno " + alumnoId + " guardado exitosamente");
                    } else {
                        errores++;
                        System.out.println("   ❌ Alumno " + alumnoId + " no se pudo guardar (resultado: " + resultado + ")");
                    }

                } catch (Exception e) {
                    errores++;
                    System.out.println("   ❌ Error procesando alumno: " + e.getMessage());
                    // Continuar con el siguiente alumno
                }
            }

            // Confirmar transacción
            con.commit();
            System.out.println("✅ Transacción completada. Exitosos: " + exitosos + ", Errores: " + errores + ", Total: " + objetos.length);

            return exitosos > 0; // Retorna true si al menos uno se guardó

        } catch (SQLException e) {
            System.out.println("❌ ERROR SQL en transacción: " + e.getMessage());
            System.out.println("   SQL State: " + e.getSQLState());
            System.out.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();

            // Revertir transacción
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("🔄 Transacción revertida debido a error");
                } catch (SQLException ex) {
                    System.out.println("❌ Error al revertir transacción: " + ex.getMessage());
                }
            }
            return false;

        } catch (Exception e) {
            System.out.println("❌ ERROR general en transacción: " + e.getMessage());
            e.printStackTrace();

            // Revertir transacción
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("🔄 Transacción revertida debido a error general");
                } catch (SQLException ex) {
                    System.out.println("❌ Error al revertir transacción: " + ex.getMessage());
                }
            }
            return false;

        } finally {
            // Cerrar recursos
            try {
                if (cs != null) {
                    cs.close();
                }
                if (con != null) {
                    con.setAutoCommit(true); // Restaurar auto-commit
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
    }

    // ✅ CORREGIDO: Método auxiliar con 8 parámetros
    private boolean guardarAsistenciaIndividual(int alumnoId, int cursoId, int turnoId, String fecha, String horaClase, String estado, int registradoPor) {
        String sql = "{CALL registrar_asistencia(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            cs.setInt(2, cursoId);
            cs.setInt(3, turnoId);
            cs.setString(4, fecha);
            cs.setString(5, horaClase);
            cs.setString(6, estado);
            cs.setString(7, ""); // Observaciones vacías
            cs.setInt(8, registradoPor);

            int resultado = cs.executeUpdate();
            System.out.println("   💾 Alumno " + alumnoId + " guardado: " + (resultado > 0));
            return resultado > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al guardar alumno " + alumnoId + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error general al guardar alumno " + alumnoId + ": " + e.getMessage());
            return false;
        }
    }

    public List<Asistencia> obtenerAsistenciasPorCursoTurnoFecha(int cursoId, int turnoId, String fecha) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "{CALL obtener_asistencias_por_curso_turno_fecha(?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, cursoId);
            cs.setInt(2, turnoId);
            cs.setString(3, fecha);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                a.setCursoId(rs.getInt("curso_id"));
                a.setTurnoId(rs.getInt("turno_id"));
                a.setFecha(rs.getString("fecha"));
                a.setHoraClase(rs.getString("hora_clase"));
                a.setEstado(rs.getString("estado"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setRegistradoPor(rs.getInt("registrado_por"));
                a.setAlumnoNombre(rs.getString("alumno_nombre"));
                a.setProfesorNombre(rs.getString("profesor_nombre"));
                a.setTurnoNombre(rs.getString("turno_nombre"));
                lista.add(a);
            }

            System.out.println("✅ Asistencias encontradas: " + lista.size());

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al obtener asistencias por curso, turno y fecha:");
            System.out.println("   Código: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Error general al obtener asistencias por curso, turno y fecha");
            e.printStackTrace();
        }

        return lista;
    }

    public List<Asistencia> obtenerAsistenciasPorAlumnoTurno(int alumnoId, int turnoId, int mes, int anio) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "{CALL obtener_asistencias_por_alumno_turno(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            cs.setInt(2, turnoId);
            cs.setInt(3, mes);
            cs.setInt(4, anio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                a.setCursoId(rs.getInt("curso_id"));
                a.setTurnoId(rs.getInt("turno_id"));
                a.setFecha(rs.getString("fecha"));
                a.setHoraClase(rs.getString("hora_clase"));
                a.setEstado(rs.getString("estado"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setRegistradoPor(rs.getInt("registrado_por"));
                a.setCursoNombre(rs.getString("curso_nombre"));
                a.setTurnoNombre(rs.getString("turno_nombre"));
                a.setGradoNombre(rs.getString("grado_nombre"));
                lista.add(a);
            }

            System.out.println("✅ Asistencias por alumno encontradas: " + lista.size());

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al obtener asistencias por alumno y turno:");
            System.out.println("   Código: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Error general al obtener asistencias por alumno y turno");
            e.printStackTrace();
        }

        return lista;
    }

    public List<Asistencia> obtenerAusenciasPorJustificar(int alumnoId) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "{CALL obtener_ausencias_por_justificar(?)}";

        System.out.println("🔍 Ejecutando stored procedure para alumno_id: " + alumnoId);

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            ResultSet rs = cs.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getString("fecha"));
                a.setHoraClase(rs.getString("hora_clase"));
                a.setEstado(rs.getString("estado"));
                a.setCursoNombre(rs.getString("curso_nombre"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                lista.add(a);

                System.out.println("📅 Ausencia encontrada: " + a.getFecha() + " - " + a.getCursoNombre());
            }

            System.out.println("✅ Total de ausencias encontradas: " + count);

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al obtener ausencias por justificar:");
            System.out.println("   Código: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Error general al obtener ausencias por justificar para alumno: " + alumnoId);
            e.printStackTrace();
        }

        return lista;
    }

    public Map<String, Object> obtenerResumenAsistenciaAlumnoTurno(int alumnoId, int turnoId, int mes, int anio) {
        Map<String, Object> resumen = new HashMap<>();
        String sql = "{CALL obtener_resumen_asistencia_alumno_turno(?, ?, ?, ?)}";

        try (Connection con = Conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, alumnoId);
            cs.setInt(2, turnoId);
            cs.setInt(3, mes);
            cs.setInt(4, anio);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                resumen.put("totalClases", rs.getInt("total_clases"));
                resumen.put("presentes", rs.getInt("presentes"));
                resumen.put("tardanzas", rs.getInt("tardanzas"));
                resumen.put("ausentes", rs.getInt("ausentes"));
                resumen.put("justificados", rs.getInt("justificados"));
                resumen.put("porcentajeAsistencia", rs.getDouble("porcentaje_asistencia"));

                System.out.println("✅ Resumen obtenido - Asistencia: " + rs.getDouble("porcentaje_asistencia") + "%");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al obtener resumen de asistencia:");
            System.out.println("   Código: " + e.getErrorCode());
            System.out.println("   Estado: " + e.getSQLState());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Error general al obtener resumen de asistencia");
            e.printStackTrace();
        }

        return resumen;
    }
}