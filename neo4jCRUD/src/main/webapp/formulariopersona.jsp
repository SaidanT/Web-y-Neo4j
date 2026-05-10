<%@page import="com.mycompany.neo4jcrud.modelo.ConfiguracionRequest"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Formulario de Configuración</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body>
<%
    // Importante: Asegúrate de que en el Controller usaste request.setAttribute("config", ...)
    ConfiguracionRequest config = (ConfiguracionRequest)request.getAttribute("config");
    String errores = (String)request.getAttribute("errores");
%>
<div class="container mt-4">

    <div class="card p-4 mb-4 shadow-sm">
        <h1 class="mb-3"><i class="bi bi-gear-fill text-secondary"></i> Datos de Configuración</h1>

        <c:if test="${errores != null}">
            <div class="alert alert-danger">${errores}</div>
        </c:if>

        <form class="row g-3" action="ConfiguracionController" method="GET">
            <input type="hidden" name="accion" value="guardar">

            <div class="col-md-2">
                <label for="codigo" class="form-label fw-bold">ID (Auto)</label>
                <%-- Usamos la variable 'config' que definimos arriba o directamente ${config.id} --%>
                <input type="text" class="form-control bg-light" id="codigo" name="codigo" readonly
                       value="${config.id != null && config.id != 'null' ? config.id : 0}">
            </div>

            <div class="col-md-5">
                <label for="entorno" class="form-label fw-bold">Entorno</label>
                <select class="form-select" id="entorno" name="entorno">
                    <option value="dev" ${config.entorno == 'dev' ? 'selected' : ''}>Desarrollo (dev)</option>
                    <option value="test" ${config.entorno == 'test' ? 'selected' : ''}>Pruebas (test)</option>
                    <option value="prod" ${config.entorno == 'prod' ? 'selected' : ''}>Producción (prod)</option>
                </select>
            </div>

            <div class="col-md-5">
                <label for="modulo" class="form-label fw-bold">Módulo</label>
                <input type="text" class="form-control" id="modulo" name="modulo"
                       placeholder="Ej: Inventarios, Ventas..."
                       value="${config.modulo}">
            </div>

            <div class="col-12">
                <label for="parametros" class="form-label fw-bold">Parámetros (Clave-Valor)</label>
                <textarea class="form-control" id="parametros" name="parametros" rows="3"
                          placeholder="Ej: timeout=5000; max_users=100">${config.parametros}</textarea>
            </div>

            <div class="col-md-6">
                <label for="version_config" class="form-label fw-bold">Versión</label>
                <input type="text" class="form-control" id="version_config" name="version_config"
                       placeholder="Ej: 1.0.0"
                       value="${config.version_config}">
            </div>

            <div class="col-md-6 d-flex align-items-end">
                <div class="form-check mb-2">
                    <%-- Corrección lógica para el checkbox --%>
                    <input class="form-check-input" type="checkbox" id="activo" name="activo"
                    ${config.activo == 'true' ? 'checked' : ''}>
                    <label class="form-check-label fw-bold" for="activo">
                        Configuración Activa
                    </label>
                </div>
            </div>

            <div class="col-12 mt-4">
                <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Guardar Configuración</button>
                <a href="ConfiguracionController" class="btn btn-secondary">Regresar al Listado</a>
            </div>
        </form>
    </div>

    <%-- SECCIÓN DE RELACIÓN --%>
    <c:if test="${config.id != null && config.id != '0' && config.id != 'null'}">
        <div class="card p-4 border-info shadow-sm">
            <h3 class="text-info"><i class="bi bi-diagram-3"></i> Dependencia de Módulos</h3>
            <p class="text-muted">Indica si el módulo <strong>${config.modulo}</strong> depende de otra configuración.</p>

            <form action="ConfiguracionController" method="GET" class="row g-3">
                <input type="hidden" name="accion" value="conectar">
                <input type="hidden" name="idOrigen" value="${config.id}">

                <div class="col-md-6">
                    <label class="form-label fw-bold">Depende de:</label>
                    <select name="idDestino" class="form-select" required>
                        <option value="" disabled selected>Seleccionar módulo destino...</option>
                        <c:forEach var="item" items="${lista_configs}">
                            <c:if test="${item.id != config.id}">
                                <option value="${item.id}">${item.modulo} (${item.entorno}) - v${item.version_config}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-bold">Tipo de Dependencia</label>
                    <input type="text" name="tipoRelacion" class="form-control"
                           placeholder="Ej: REQUIERE, USA, COMPLEMENTA..." required>
                </div>

                <div class="col-12">
                    <button type="submit" class="btn btn-outline-info">🔗 Crear Relación en Neo4j</button>
                </div>
            </form>
        </div>
    </c:if>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>