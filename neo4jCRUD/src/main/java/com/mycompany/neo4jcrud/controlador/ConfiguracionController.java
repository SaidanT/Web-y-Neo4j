package com.mycompany.neo4jcrud.controlador;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.neo4jcrud.modelo.Configuracion;
import com.mycompany.neo4jcrud.modelo.ConfiguracionRequest;
import com.mycompany.neo4jcrud.persistencia.ConfiguracionDB;
import com.mycompany.neo4jcrud.bean.neo4jBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConfiguracionController", value = "/configuraciones/ConfiguracionController")
public class ConfiguracionController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionController.class);

    @Inject
    private neo4jBean neo4jBean; // Inyectamos el bean de Neo4j en lugar del de Mongo

    private ConfiguracionDB configuracionDB;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            if (neo4jBean == null) {
                neo4jBean = new neo4jBean();
            }
            configuracionDB = new ConfiguracionDB(neo4jBean.getDriver());
            logger.info("Conexión a Neo4j establecida para el módulo de Configuraciones");
        } catch (Exception e) {
            logger.error("No se pudo conectar a Neo4j: " + e.getMessage());
            configuracionDB = null;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (configuracionDB == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de conexión a Neo4j");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        logger.info("Módulo Configuraciones (Neo4j) - Acción: " + accion);

        String mensaje = null;

        switch (accion) {
            case "agregar":
                crearConfiguracion(request, response);
                break;
            case "guardar":
                String errores = validarConfiguracion(request);
                if (errores.isEmpty()) {
                    if (guardarConfiguracion(request)) {
                        mensaje = "Configuración guardada correctamente en Neo4j";
                    } else {
                        mensaje = "Error al intentar guardar en el grafo";
                    }
                    listarConfiguraciones(mensaje, request, response);
                } else {
                    mostrarErrores(errores, request, response);
                }
                break;
            case "editar":
                if (!editarConfiguracion(request, response)) {
                    mensaje = "Error al recuperar los datos del nodo";
                    listarConfiguraciones(mensaje, request, response);
                }
                break;
            case "eliminar":
                if (eliminarConfiguracion(request)) {
                    mensaje = "Nodo eliminado correctamente";
                } else {
                    mensaje = "Error al eliminar el nodo (posiblemente tiene relaciones)";
                }
                listarConfiguraciones(mensaje, request, response);
                break;
            default:
                listarConfiguraciones(null, request, response);
                break;
        }
    }

    private void listarConfiguraciones(String mensaje, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Configuracion> configuraciones;
        try {
            configuraciones = configuracionDB.getConfiguraciones();
        } catch (Exception e) {
            logger.error("Error recuperando nodos de Neo4j: " + e.getMessage());
            configuraciones = new ArrayList<>();
        }
        request.setAttribute("lista_configs", configuraciones);
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/configuraciones.jsp").forward(request, response);
    }

    private void crearConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("config", new ConfiguracionRequest());
        request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
    }

    private boolean editarConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codigo = request.getParameter("codigo");
        try {
            Configuracion config = configuracionDB.buscar(codigo);
            if (config == null) return false;
            request.setAttribute("config", new ConfiguracionRequest(config));
            request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String validarConfiguracion(HttpServletRequest request) {
        return configuracionDB.validar(
                request.getParameter("entorno"),
                request.getParameter("modulo"),
                request.getParameter("version_config"),
                request.getParameter("parametros")
        );
    }

    private void mostrarErrores(String errores, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ConfiguracionRequest configReq = new ConfiguracionRequest(
                request.getParameter("codigo"),
                request.getParameter("entorno"),
                request.getParameter("modulo"),
                request.getParameter("parametros"),
                request.getParameter("version_config"),
                String.valueOf(request.getParameter("activo") != null)
        );
        request.setAttribute("config", configReq);
        request.setAttribute("errores", errores);
        request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
    }

    private boolean guardarConfiguracion(HttpServletRequest request) {
        String codigo = request.getParameter("codigo");
        String entorno = request.getParameter("entorno");
        String modulo = request.getParameter("modulo");
        String parametros = request.getParameter("parametros");
        String version = request.getParameter("version_config");
        boolean activo = request.getParameter("activo") != null;

        if (codigo == null || "0".equals(codigo) || "null".equals(codigo)) {
            return configuracionDB.insertar(entorno, modulo, parametros, version, activo);
        } else {
            return configuracionDB.editar(codigo, entorno, modulo, parametros, version, activo);
        }
    }

    private boolean eliminarConfiguracion(HttpServletRequest request) {
        return configuracionDB.eliminar(request.getParameter("codigo"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}