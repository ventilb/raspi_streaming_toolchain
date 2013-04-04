<%--
  ~ Copyright 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%--
  Renders the home web page.
  
  @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
  @since 04.04.13 - 11:53
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${pageTitle}</title>
    <link href="${pageContext.request.contextPath}/static/css/page.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="content_outer_container">
    <div id="content_inner_container">
        <div id="webcam_image_stream">
            <img src="${pageContext.request.contextPath}/stream/webcam.jpg" width="640" height="480"/>
        </div>
    </div>
</div>
</body>
</html>