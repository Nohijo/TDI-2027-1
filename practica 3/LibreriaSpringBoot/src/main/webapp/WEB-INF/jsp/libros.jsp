<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
    Practica 3 - Tecnologias para Desarrollos en Internet.

    Vista del catalogo: formulario de alta, tabla con filtro en vivo y los
    modales de editar y eliminar. Solo muestra lo que deja BookController
    ("libro", "libros" y al buscar "q", "campo", "orden"). Los textos salen de
    messages.properties; los que usa jQuery van en los atributos data-* de #app.

    Autor: Miguel Angel Marquez Cristoval
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%@ include file="fragmentos/cabecera.jspf" %>

<spring:message code="libro.nombre.requerido" var="msgNombre" />
<spring:message code="libro.autor.requerido" var="msgAutor" />
<spring:message code="libro.precio.invalido" var="msgPrecio" />
<spring:message code="libro.nombre.ejemplo" var="ejemploNombre" />
<spring:message code="libro.autor.ejemplo" var="ejemploAutor" />
<spring:message code="libro.precio.ejemplo" var="ejemploPrecio" />

<main class="container my-4 flex-grow-1" id="app"
      data-contexto="${ctx}"
      data-idioma="${idioma}"
      data-uno="<spring:message code="catalogo.contador.uno" htmlEscape="true" />"
      data-varios="<spring:message code="catalogo.contador.varios" htmlEscape="true" />"
      data-actualizado="<spring:message code="aviso.actualizado" htmlEscape="true" />"
      data-eliminado="<spring:message code="aviso.eliminado" htmlEscape="true" />"
      data-no-existe="<spring:message code="aviso.noExiste" htmlEscape="true" />"
      data-error="<spring:message code="aviso.error" htmlEscape="true" />"
      data-cerrar="<spring:message code="form.cerrar" htmlEscape="true" />">

    <div id="avisos">
        <c:if test="${not empty aviso}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <spring:message code="${aviso}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="<spring:message code="form.cerrar" />"></button>
            </div>
        </c:if>
        <c:if test="${errorBD}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <spring:message code="aviso.errorBD" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="<spring:message code="form.cerrar" />"></button>
            </div>
        </c:if>
    </div>

    <div class="row g-4">

        <div class="col-12 col-lg-4">
            <section class="card shadow-sm">
                <div class="card-body">
                    <h2 class="h5 card-title"><spring:message code="form.agregar.titulo" /></h2>
                    <form:form modelAttribute="libro" method="post" action="${ctx}/libros" id="formAgregar">
                        <div class="mb-3">
                            <form:label path="nombre" cssClass="form-label"><spring:message code="libro.nombre" /></form:label>
                            <form:input path="nombre" cssClass="form-control" cssErrorClass="form-control is-invalid"
                                        maxlength="150" required="required" placeholder="${ejemploNombre}" data-msg="${msgNombre}" />
                            <form:errors path="nombre" element="div" cssClass="invalid-feedback" />
                        </div>
                        <div class="mb-3">
                            <form:label path="autor" cssClass="form-label"><spring:message code="libro.autor" /></form:label>
                            <form:input path="autor" cssClass="form-control" cssErrorClass="form-control is-invalid"
                                        maxlength="150" required="required" placeholder="${ejemploAutor}" data-msg="${msgAutor}" />
                            <form:errors path="autor" element="div" cssClass="invalid-feedback" />
                        </div>
                        <div class="mb-3">
                            <form:label path="precio" cssClass="form-label"><spring:message code="libro.precio" /></form:label>
                            <form:input path="precio" type="number" cssClass="form-control" cssErrorClass="form-control is-invalid"
                                        min="0" max="99999999.99" step="0.01" required="required"
                                        placeholder="${ejemploPrecio}" data-msg="${msgPrecio}" />
                            <form:errors path="precio" element="div" cssClass="invalid-feedback" />
                        </div>
                        <button type="submit" class="btn btn-primary w-100"><spring:message code="form.agregar.boton" /></button>
                    </form:form>
                </div>
            </section>
        </div>

        <div class="col-12 col-lg-8">
            <section class="card shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-baseline">
                        <h2 class="h5 card-title"><spring:message code="catalogo.titulo" /></h2>
                        <span class="text-muted small" id="contador"></span>
                    </div>

                    <%-- Al escribir filtra jQuery lo que ya esta en la tabla; Enter busca en la base. --%>
                    <form method="get" action="${ctx}/libros" class="mb-3" role="search">
                        <label for="filtro" class="form-label"><spring:message code="catalogo.filtro" /></label>
                        <div class="input-group">
                            <input type="search" class="form-control" id="filtro" name="q" autocomplete="off"
                                   value="<c:out value="${q}" />"
                                   placeholder="<spring:message code="catalogo.filtro.ejemplo" htmlEscape="true" />">
                            <a class="btn btn-outline-secondary" href="${ctx}/libros"><spring:message code="catalogo.limpiar" /></a>
                        </div>
                        <div class="form-text"><spring:message code="catalogo.filtro.ayuda" /></div>
                        <c:if test="${not empty campo}">
                            <input type="hidden" name="campo" value="<c:out value="${campo}" />">
                            <input type="hidden" name="orden" value="<c:out value="${orden}" />">
                        </c:if>
                    </form>

                    <div class="table-responsive">
                        <table class="table table-striped table-hover align-middle mb-0" id="tablaLibros">
                            <thead>
                                <tr>
                                    <c:forEach var="columna" items="nombre,autor,precio">
                                        <c:set var="ascendente" value="${campo == columna and orden != 'desc'}" />
                                        <c:url var="enlaceOrden" value="/libros">
                                            <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                                            <c:param name="campo" value="${columna}" />
                                            <c:param name="orden" value="${ascendente ? 'desc' : 'asc'}" />
                                        </c:url>
                                        <th scope="col" class="${columna == 'precio' ? 'text-end' : ''}">
                                            <a class="enlace-orden" href="${enlaceOrden}">
                                                <spring:message code="libro.${columna}" />
                                                <c:if test="${campo == columna}">${ascendente ? '&#9650;' : '&#9660;'}</c:if>
                                            </a>
                                        </th>
                                    </c:forEach>
                                    <th scope="col" class="text-end"><spring:message code="catalogo.acciones" /></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="libro" items="${libros}">
                                    <tr class="fila-libro" data-id="${libro.id}"
                                        data-nombre="<c:out value="${libro.nombre}" />"
                                        data-autor="<c:out value="${libro.autor}" />"
                                        data-precio="${libro.precio}">
                                        <td class="celda-nombre"><c:out value="${libro.nombre}" /></td>
                                        <td class="celda-autor"><c:out value="${libro.autor}" /></td>
                                        <td class="celda-precio text-end text-nowrap"><fmt:formatNumber value="${libro.precio}" pattern="$#,##0.00" /></td>
                                        <td class="text-end text-nowrap">
                                            <button type="button" class="btn btn-sm btn-outline-primary btn-editar"><spring:message code="catalogo.editar" /></button>
                                            <button type="button" class="btn btn-sm btn-outline-danger btn-eliminar"><spring:message code="catalogo.eliminar" /></button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr id="sinResultados" class="d-none">
                                    <td colspan="4" class="text-center text-muted py-4"><spring:message code="catalogo.vacio" /></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>
        </div>
    </div>
</main>

<div class="modal fade" id="modalEditar" tabindex="-1" aria-labelledby="tituloEditar" aria-hidden="true">
    <div class="modal-dialog">
        <form class="modal-content" id="formEditar">
            <div class="modal-header">
                <h2 class="modal-title h5" id="tituloEditar"><spring:message code="form.editar.titulo" /></h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="<spring:message code="form.cerrar" />"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="editarNombre" class="form-label"><spring:message code="libro.nombre" /></label>
                    <input type="text" class="form-control" id="editarNombre" name="nombre" maxlength="150" required data-msg="<c:out value="${msgNombre}" />">
                </div>
                <div class="mb-3">
                    <label for="editarAutor" class="form-label"><spring:message code="libro.autor" /></label>
                    <input type="text" class="form-control" id="editarAutor" name="autor" maxlength="150" required data-msg="<c:out value="${msgAutor}" />">
                </div>
                <div class="mb-3">
                    <label for="editarPrecio" class="form-label"><spring:message code="libro.precio" /></label>
                    <input type="number" class="form-control" id="editarPrecio" name="precio" min="0" max="99999999.99" step="0.01" required data-msg="<c:out value="${msgPrecio}" />">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal"><spring:message code="form.cancelar" /></button>
                <button type="submit" class="btn btn-primary"><spring:message code="form.guardar" /></button>
            </div>
        </form>
    </div>
</div>

<div class="modal fade" id="modalEliminar" tabindex="-1" aria-labelledby="tituloEliminar" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="modal-title h5" id="tituloEliminar"><spring:message code="eliminar.titulo" /></h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="<spring:message code="form.cerrar" />"></button>
            </div>
            <div class="modal-body">
                <p><spring:message code="eliminar.pregunta" /></p>
                <p class="fw-bold mb-0" id="libroAEliminar"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal"><spring:message code="form.cancelar" /></button>
                <button type="button" class="btn btn-danger" id="confirmarEliminar"><spring:message code="catalogo.eliminar" /></button>
            </div>
        </div>
    </div>
</div>

<%@ include file="fragmentos/pie.jspf" %>
