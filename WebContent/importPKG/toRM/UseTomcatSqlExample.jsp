<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sql:query var="rs" dataSource="jdbc/MySQL">
select count(*) count_n from tb_user
</sql:query>

<html>
  <head>
    <title>DB Test</title>
  </head>
  <body>
  
<!--   在contextxml裡面加這段 -->
<!-- <Resource name="jdbc/MySQL" auth="Container" type="javax.sql.DataSource" -->
<!--                maxTotal="100" maxIdle="30" maxWaitMillis="10000" -->
<!--                username="root" password="admin123" driverClassName="com.mysql.jdbc.Driver" -->
<!--                url="jdbc:mysql://192.168.112.164:3306/cdri?useSSL=false"/> -->
  <h2>Use Tomcat Sql Example</h2>
 	Q: select count(*) count_n from tb_user? <br>
<c:forEach var="row" items="${rs.rows}">
    A: ${row.count_n}<br/>
</c:forEach>

  </body>
</html>