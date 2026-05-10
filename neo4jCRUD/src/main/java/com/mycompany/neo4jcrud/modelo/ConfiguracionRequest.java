package com.mycompany.neo4jcrud.modelo;

public class ConfiguracionRequest {
    private String id;
    private String entorno;
    private String modulo;
    private String parametros;
    private String version_config;
    private String activo; // Se maneja como String para capturar el valor del checkbox o radio

    public ConfiguracionRequest() {
        this.id = "0";
        this.entorno = "";
        this.modulo = "";
        this.parametros = "";
        this.version_config = "";
        this.activo = "false";
    }

    // Constructor completo para transferir datos del formulario
    public ConfiguracionRequest(String id, String entorno, String modulo, String parametros, String version_config, String activo) {
        this.id = id;
        this.entorno = entorno;
        this.modulo = modulo;
        this.parametros = parametros;
        this.version_config = version_config;
        this.activo = activo;
    }

    // Constructor de conveniencia para convertir un objeto del modelo a uno de Request (útil para Editar)
    public ConfiguracionRequest(Configuracion config) {
        this.id = config.getId();
        this.entorno = config.getEntorno();
        this.modulo = config.getModulo();
        this.parametros = config.getParametros();
        this.version_config = config.getVersion_config();
        this.activo = String.valueOf(config.isActivo());
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

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }
}