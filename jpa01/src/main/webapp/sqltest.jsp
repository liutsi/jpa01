<%@ page pageEncoding="UTF-8"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sql:query var="rs" dataSource="jdbc/kthx">
select * from wechatmsgs limit 20
</sql:query>

<html>
  <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>DB Test</title>
  </head>
  <body>

  <h2>Results</h2>
<table>


<c:forEach var="row" items="${rs.rows}">
<tr>
    <td>Username ${row.ToUserName}</td>
    <td>content ${row.Content}</td>
    <td>lpurl ${row.lpurl}</td>
    <td>type ${row.msgtype}</td>
    <td>msgid ${row.msgid}</td>
    <td>createtime ${row.createtime}</td>
</tr>
</c:forEach>


</table>
  </body>
</html>