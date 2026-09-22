<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    Practica 2 - Tecnologias para Desarrollos en Internet.

    View de la arquitectura MVC: unica pagina de la aplicacion. Solo
    muestra lo que el Controller (BookServlet) deja en el request
    ("libros", y al buscar tambien "q", "campo", "orden"); no habla
    con el DAO ni con el Model directamente.

    Autor: Miguel Angel Marquez Cristoval
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Libreria en Linea</title>
    <link rel="stylesheet" href="${ctx}/css/libreria.css">
</head>
<body>

<header class="cabecera">
    <h1>&#128218; Libreria en Linea</h1>
    <p class="subtitulo">Practica 2 &middot; MVC con Servlets y JSP</p>
</header>

<main class="contenido">

    <c:if test="${param.msg == 'ok'}">
        <p class="aviso aviso-ok">Libro agregado correctamente.</p>
    </c:if>
    <c:if test="${param.msg == 'error'}">
        <p class="aviso aviso-error">Revisa los datos: nombre, autor y un precio valido son obligatorios.</p>
    </c:if>
    <c:if test="${param.msg == 'db' or errorDB}">
        <p class="aviso aviso-error">No se pudo conectar con la base de datos. Revisa que MySQL este corriendo y los datos en db.properties.</p>
    </c:if>

    <div class="paneles">

        <section class="tarjeta">
            <h2>Registrar libro</h2>
            <form method="post" action="${ctx}/libreria">
                <input type="hidden" name="accion" value="agregar">

                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" required maxlength="120" placeholder="Ej. Pedro Paramo">

                <label for="autor">Autor</label>
                <input type="text" id="autor" name="autor" required maxlength="120" placeholder="Ej. Juan Rulfo">

                <label for="precio">Precio (MXN)</label>
                <input type="number" id="precio" name="precio" required min="0" step="0.01" placeholder="Ej. 199.00">

                <button type="submit" class="boton boton-primario">Agregar libro</button>
            </form>
        </section>

        <section class="tarjeta">
            <h2>Buscar y filtrar</h2>
            <form method="get" action="${ctx}/libreria">
                <input type="hidden" name="accion" value="buscar">

                <label for="q">Buscar (nombre o autor)</label>
                <input type="text" id="q" name="q" value="${q}" placeholder="Ej. Garcia Marquez">

                <label for="campo">Ordenar por</label>
                <select id="campo" name="campo">
                    <option value="" ${empty campo ? 'selected' : ''}>Sin ordenar</option>
                    <option value="nombre" ${campo == 'nombre' ? 'selected' : ''}>Nombre</option>
                    <option value="autor" ${campo == 'autor' ? 'selected' : ''}>Autor</option>
                    <option value="precio" ${campo == 'precio' ? 'selected' : ''}>Precio</option>
                </select>

                <label for="orden">Direccion</label>
                <select id="orden" name="orden">
                    <option value="asc" ${orden != 'desc' ? 'selected' : ''}>Ascendente</option>
                    <option value="desc" ${orden == 'desc' ? 'selected' : ''}>Descendente</option>
                </select>

                <button type="submit" class="boton boton-primario">Buscar</button>
                <a class="boton boton-secundario" href="${ctx}/libreria">Limpiar</a>
            </form>
        </section>

    </div>

    <section class="tarjeta tarjeta-tabla">
        <h2>Catalogo <span class="contador">(${libros.size()} libro<c:if test="${libros.size() != 1}">s</c:if>)</span></h2>

        <c:choose>
            <c:when test="${empty libros}">
                <p class="vacio">No se encontraron libros con ese criterio.</p>
            </c:when>
            <c:otherwise>
                <div class="tabla-scroll">
                    <table class="tabla-libros">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Autor</th>
                                <th class="col-precio">Precio</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="libro" items="${libros}">
                                <tr>
                                    <td>${libro.nombre}</td>
                                    <td>${libro.autor}</td>
                                    <td class="col-precio">
                                        <fmt:formatNumber value="${libro.precio}" type="currency" currencySymbol="$" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

</main>

<footer class="pie">
    <p>Tecnologias para Desarrollos en Internet &middot; Practica 2</p>
</footer>

</body>
</html>
