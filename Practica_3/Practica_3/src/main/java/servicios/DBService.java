package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {
    private static DBService instancia;
    private String URL = "jdbc:h2:tcp://localhost/~/conexionPractica3";

    private DBService() {
        this.registrarDriver();
    }

    public static DBService getInstancia() {
        return instancia == null ? new DBService() : instancia;
    }

    private void registrarDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException var2) {
            System.out.println("ERROR -No se pudo registrar el driver: " + var2);
        }

    }

    public Connection getConexion() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(this.URL, "sa", "");
        } catch (SQLException var3) {
            System.out.println("ERROR -No se pudo acceder a la base de datos: " + var3);
        }

        return con;
    }
}
