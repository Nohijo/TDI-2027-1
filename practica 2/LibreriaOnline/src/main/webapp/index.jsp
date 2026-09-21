<%--
    Punto de entrada de la aplicacion. Redirige de inmediato al servlet
    controlador (BookServlet), que arma el listado y reenvia a la vista
    real (libreria.jsp). Asi la vista nunca queda huerfana de datos.
--%>
<%
    response.sendRedirect(request.getContextPath() + "/libreria");
%>
