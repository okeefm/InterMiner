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
                <thead>
                     <tr><th colspan="1"><font color="green">Positive correlation</font></th></tr>
                     <tr><th colspan="1"><font color="green">Correlation value</font></th></tr>
                </thead>
                
                <tbody
                    <c:forEach items="${posCorrGenes}" var="posCorrGene">
                        <tr>
                            <td>
                                <font color="green"><c:out value="${posCorrGene}"/></font>
                            </td>
                            <td>
                                <font color="green"><%= Math.random() %></font>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
        </table>
        <table>
                <thead>
                     <tr><th colspan="1"><font color="red">Negative Correlation</font></th></tr>
                     <tr><th colspan="1"><font color="red">Correlation value</font></th></tr>

                </thead>
                
                <tbody
                    <c:forEach items="${negCorrGenes}" var="negCorrGene">
                        <tr>
                            <td>
                                <font color="red"><c:out value="${negCorrGene}"/></font>
                            </td>
                            <td>
                                <font color="red">-<%= Math.random() %></font>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
        </table>
      </c:otherwise>
    </c:choose>
</div>

<!-- /keggSimilarityDisplayer.jsp -->
