package PDV;

public class Sesion {
    private static int usuarioId;
    private static String nombreUsuario;
    private static String rolUsuario;
    private static int turnoId;

    public static int getUsuarioId() {
        return usuarioId;
    }

    public static void setUsuarioId(int usuarioId) {
        Sesion.usuarioId = usuarioId;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static void setNombreUsuario(String nombreUsuario) {
        Sesion.nombreUsuario = nombreUsuario;
    }

    public static String getRolUsuario() {
        return rolUsuario;
    }

    public static void setRolUsuario(String rolUsuario) {
        Sesion.rolUsuario = rolUsuario;
    }

    public static int getTurnoId() {
        return turnoId;
    }

    public static void setTurnoId(int turnoId) {
        Sesion.turnoId = turnoId;
    }
}
