package com.mycompany.neo4jcrud.persistencia;

import com.mycompany.neo4jcrud.modelo.Configuracion;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.types.Node;

public class ConfiguracionDB {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionDB.class);
    private final Driver driver;
    // Ajustado a tu base de datos destino
    private final String DATABASE_NAME = "poyecto";

    public ConfiguracionDB(Driver driver) {
        this.driver = driver;
    }

    public String validar(String entorno, String modulo, String version, String parametros) {
        if (entorno == null || entorno.trim().isEmpty())
            return "El entorno (dev, test, prod) es obligatorio";
        if (modulo == null || modulo.trim().isEmpty())
            return "El nombre del módulo es obligatorio";
        if (version == null || version.trim().isEmpty())
            return "La versión de configuración es obligatoria";
        if (parametros == null || parametros.trim().isEmpty())
            return "Debe ingresar al menos un parámetro";
        return "";
    }

    public boolean insertar(String entorno, String modulo, String parametros, String version, boolean activo) {
        try {
            String id = java.util.UUID.randomUUID().toString();

            // Consulta ajustada a los campos de la imagen de WhatsApp
            String cypher = "CREATE (c:Configuracion {id: $id, entorno: $entorno, modulo: $modulo, " +
                    "parametros: $parametros, version_config: $version, activo: $activo})";

            Map<String, Object> params = Map.of(
                    "id", id,
                    "entorno", entorno,
                    "modulo", modulo,
                    "parametros", parametros,
                    "version", version,
                    "activo", activo
            );

            try (Session session = driver.session(SessionConfig.forDatabase(DATABASE_NAME))) {
                var summary = session.executeWrite(tx -> tx.run(cypher, params).consume());
                return summary.counters().nodesCreated() > 0;
            }
        } catch (Exception e) {
            logger.error("Error al insertar configuración", e);
            return false;
        }
    }

    public boolean editar(String id, String entorno, String modulo, String parametros, String version, boolean activo) {
        String cypher = "MATCH (c:Configuracion {id: $id}) " +
                "SET c.entorno = $entorno, c.modulo = $modulo, " +
                "c.parametros = $parametros, c.version_config = $version, c.activo = $activo";

        Map<String, Object> params = Map.of(
                "id", id,
                "entorno", entorno,
                "modulo", modulo,
                "parametros", parametros,
                "version", version,
                "activo", activo
        );

        try (Session session = driver.session(SessionConfig.forDatabase(DATABASE_NAME))) {
            Result result = session.run(cypher, params);
            return result.consume().counters().propertiesSet() > 0;
        } catch (Exception e) {
            logger.error("Error editando configuración", e);
            return false;
        }
    }

    public boolean eliminar(String id) {
        String cypher = "MATCH (c:Configuracion {id: $id}) DETACH DELETE c";
        try (Session session = driver.session(SessionConfig.forDatabase(DATABASE_NAME))) {
            Result result = session.run(cypher, Map.of("id", id));
            return result.consume().counters().nodesDeleted() > 0;
        } catch (Exception e) {
            logger.error("Error eliminando configuración", e);
            return false;
        }
    }

    public Configuracion buscar(String id) {
        String cypher = "MATCH (c:Configuracion {id: $id}) RETURN c";
        try (Session session = driver.session(SessionConfig.forDatabase(DATABASE_NAME))) {
            Result result = session.run(cypher, Map.of("id", id));
            if (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                return mapearConfiguracion(node);
            }
            return null;
        }
    }

    public List<Configuracion> getConfiguraciones() {
        String cypher = "MATCH (c:Configuracion) RETURN c ORDER BY c.modulo";
        List<Configuracion> lista = new ArrayList<>();
        try (Session session = driver.session(SessionConfig.forDatabase(DATABASE_NAME))) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                Node node = result.next().get("c").asNode();
                lista.add(mapearConfiguracion(node));
            }
        }
        return lista;
    }

    // Método auxiliar para no repetir código al convertir nodos a objetos
    private Configuracion mapearConfiguracion(Node node) {
        return new Configuracion(
                node.get("id").asString(),
                node.get("entorno").asString(),
                node.get("modulo").asString(),
                node.get("parametros").asString(),
                node.get("version_config").asString(),
                node.get("activo").asBoolean()
        );
    }
}