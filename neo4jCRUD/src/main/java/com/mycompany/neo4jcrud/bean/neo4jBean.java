package com.mycompany.neo4jcrud.bean;

import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;

public class neo4jBean {

    private static final Logger logger = LoggerFactory.getLogger(neo4jBean.class);
    private Driver driver;
    private String uri;
    private String username;
    private String password;
    private String database; // Campo para la base de datos específica

    public neo4jBean() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("neo4j.properties")) {
            if (input == null) {
                logger.error("No se encontró el archivo neo4j.properties en el classpath");
                return;
            }
            Properties props = new Properties();
            props.load(input);

            // Cargamos valores con tus credenciales actuales
            this.uri = props.getProperty("uri", "neo4j://127.0.0.1:7687");
            this.username = props.getProperty("username", "neo4j");
            this.password = props.getProperty("password", "12345678");
            this.database = props.getProperty("database", "proyecto"); // Por defecto a poyecto

            this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
            logger.info("Driver Neo4j inicializado exitosamente para URI: {}", uri);

        } catch (Exception e) {
            logger.error("Error crítico al cargar la configuración de Neo4j", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Proporciona una sesión apuntando específicamente a la base de datos configurada.
     */
    public Session getSession() {
        if (driver == null) {
            throw new IllegalStateException("El Driver de Neo4j no ha sido inicializado.");
        }
        // Coherencia: Forzamos la sesión a trabajar en la base de datos elegida
        return driver.session(SessionConfig.forDatabase(this.database));
    }

    public Driver getDriver() {
        return driver;
    }

    public String getDatabaseName() {
        return database;
    }

    public boolean checkConnection() {
        try (Session session = getSession()) {
            Result result = session.run("RETURN 1");
            boolean conectado = result.hasNext();
            if (conectado) {
                logger.info("Verificación de conexión exitosa a la base de datos: {}", database);
            }
            return conectado;
        } catch (Exception e) {
            logger.error("Fallo la verificación de conexión a Neo4j en la base de datos: {}", database, e);
            return false;
        }
    }

    /**
     * Cierra el pool de conexiones del driver.
     * Debe llamarse desde el método destroy() del Servlet.
     */
    public void close() {
        if (driver != null) {
            driver.close();
            logger.info("Pool de conexiones de Neo4j cerrado correctamente.");
        }
    }
}