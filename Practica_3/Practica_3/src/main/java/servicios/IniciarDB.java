package servicios;

import org.eclipse.jetty.server.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class IniciarDB {
    private static Server server;

    public IniciarDB() {
    }

    public static void startDatabase() {
        try {
            server = Server.getState(new String[]{"-tcpPort", "9092", "-tcpAllowOthers", "-tcpDaemon", "-ifNotExists"}).start();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

    }

    public static void stopDb() throws Exception {
        if (server != null) {
            server.stop();
        }

    }

    public static void crearTablas() throws SQLException {
        String producto = "CREATE TABLE IF NOT EXISTS PRODUCTO\n(\n  ID VARCHAR(100) PRIMARY KEY NOT NULL,\n  NOMBRE VARCHAR(100) NOT NULL,\n  PRECIO DOUBLE NOT NULL,\n  CANTIDAD INT NOT NULL\n);";
        String usuario = "CREATE TABLE IF NOT EXISTS USUARIO\n(\n  ID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,\n  USUARIO VARCHAR(100) NOT NULL,\n  NOMBRE VARCHAR(100) NOT NULL,\n  PASSWORD VARCHAR(100) NOT NULL\n);";
        String ventas = "CREATE TABLE IF NOT EXISTS VENTA\n(\n  ID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,\n  FECHA DATE NOT NULL,\n  NOMBRE_CLIENTE VARCHAR(100) NOT NULL\n);";
        String prodVentas = "CREATE TABLE IF NOT EXISTS PRODUCTO_VENTA\n(\n  ID_VENTA INT NOT NULL,\n  ID_PRODUCTO VARCHAR(100) NOT NULL,\n  CANTIDAD INT NOT NULL,\n  PRODUCTO VARCHAR(100) NOT NULL,\n  PRECIO DOUBLE NOT NULL,\n  FOREIGN KEY (ID_VENTA) REFERENCES VENTA(ID),\n  FOREIGN KEY (ID_PRODUCTO) REFERENCES PRODUCTO(ID)\n);";
        Connection con = DBService.getInstancia().getConexion();
        Statement statement = con.createStatement();
        statement.execute(producto);
        statement.execute(usuario);
        statement.execute(ventas);
        statement.execute(prodVentas);
        statement.close();
        con.close();
    }
}
