<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- keggSimilarityDisplayer.jsp -->
<div class="basic-table">
    <h3>Similar Genes by KEGG pathways</h3>
    
    <c:choose>
      <c:when test="${!empty noKeggMessage }">
        <p>${noKeggMessage}</p>
      </c:when>
      <c:otherwise>
        <p>KEGG Loaded!</p>
      </c:otherwise>
    </c:choose>
</div>

<!-- /keggSimilarityDisplayer.jsp -->
