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
        <table>
                <c:if test="${empty primaryIdentifier}">
                    <p>Primary Identifier Empty</p>
                </c:if>
                <thead>
                     <tr><th colspan="2">KEGG Loaded for ${primaryIdentifier}</th></tr>
                </thead>
                
                <tbody>
                    <c:forEach items="${pathwayNames}" var="pathwayName">
                        <tr>
                            <td>
                                <c:out value="${pathwayName}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
        </table>
      </c:otherwise>
    </c:choose>
</div>

<!-- /keggSimilarityDisplayer.jsp -->
