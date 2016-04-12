<%@ tag body-content="empty" %>

<%@ attribute name="type" required="true" description="Type of resource (text/css)" %>
<%@ attribute name="rel" required="true" description="rel (stylesheet)" %>
<%@ attribute name="href" required="true" description="Specifies the path to the class" %>

<link type="${type}"
      rel="${rel}"
      href="${href}?b=<%=com.infusionsoft.cas.support.BuildVersion.getBuildVersion()%>"
/>