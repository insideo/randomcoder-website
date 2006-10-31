<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<c:url var="homeUrl" value="/" />
<c:url var="aboutUrl" value="/legal/about" />
<c:url var="javadocUrl" value="/docs/apidocs/" />
<div class="sectionHeading">Tags</div>
<div class="sectionContent" align="right">
	<div class="tagCloud">
		<c:forEach var="tagCloudEntry" items="${tagCloud}" varStatus="status">
			<c:url var="tagLink" value="/tags/${rcesc:urlencode(tagCloudEntry.tag.name)}" />
			<c:url var="tagClass" value="cloud${tagCloudEntry.scale}" />
			<c:if test="${status.index > 0}">::</c:if>
			<a rel="tag" class="tag ${tagClass}" href="${tagLink}"><c:url value="${tagCloudEntry.tag.displayName}" /></a>			
		</c:forEach>
	</div>
</div>