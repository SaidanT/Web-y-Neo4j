package com.mycompany.neo4jcrud.modelo;

import java.util.UUID;

public class Configuracion {
    private String id;
    private String entorno;
    private String modulo;
    private String parametros; // Pares clave-valor (ej: "clave=valor")
    private String version_config;
    private boolean activo;

    public Configuracion() {
        this.id = null;
        this.entorno = "";
        this.modulo = "";
        this.parametros = "";
        this.version_config = "";
        this.activo = false;
    }

    // Constructor para nuevos registros (genera UUID)
    public Configuracion(String entorno, String modulo, String parametros, String version_config, boolean activo) {
        this.id = UUID.randomUUID().toString();
        this.entorno = entorno;
        this.modulo = modulo;
        this.parametros = parametros;
        this.version_config = version_config;
        this.activo = activo;
    }

    // Constructor para registros existentes (recuperados de Neo4j)
    public Configuracion(String id, String entorno, String modulo, String parametros, String version_config, boolean activo) {
        this.id = id;
        this.entorno = entorno;
        this.modulo = modulo;
        this.parametros = parametros;
        this.version_config = version_config;
        this.activo = activo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEntorno() { return entorno; }
    public void setEntorno(String entorno) { this.entorno = entorno; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getParametros() { return parametros; }
    public void setParametros(String parametros) { this.parametros = parametros; }

    public String getVersion_config() { return version_config; }
    public void setVersion_config(String version_config) { this.version_config = version_config; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}