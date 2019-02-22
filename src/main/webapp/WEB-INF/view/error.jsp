<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>错误页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <!-- 禁止用户缩放 -->
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>
    <link rel="icon" href="../img/czyh.png">
    <link rel="stylesheet" href="../css/normalize.css"/>
    <link rel="stylesheet" href="../css/bootstrap.modified.css"/>
    <!-- iziToast
    <link rel="stylesheet" href="css/iziToast.min.css"/>-->

    <script src="../js/jquery-1.11.0.min.js"></script>
    <script src="../js/bootstrap.min.js"></script>
    <!--<script src="js/iziToast.min.js"></script>-->
    <script src="../js/deferredAjax.js"></script>
</head>
<body class="container">
<%
    String code = request.getParameter("code");
%>

<div class="col-lg-12">
    <h1>发生错误 <small style="color: red;" id="errorCode">错误码：<%= code %></small></h1>
</div>
<hr />
<table class="table table-striped table-responsive table-bordered table-hover col-lg-12">
    <thead>
        <tr>
            <td colspan="3" style="text-align: center;">错误码参照表</td>
        </tr>
        <tr>
            <td style="text-align: center;">错误码</td>
            <td style="text-align: center;">内容</td>
            <td style="text-align: center;">参考解决方案</td>
        </tr>
    </thead>
    <tbody>
        <tr id="code0">
            <td style="text-align: center;">-</td>
            <td>未发生错误，您主动访问了此页面</td>
            <td>-</td>
        </tr>
        <tr id="code1">
            <td style="text-align: center;">1</td>
            <td>您没有权限访问请求的资源</td>
            <td>若您拥有该资源的访问权限，造成原因可能是未登录或登录超时，去[<span class="glyphicon glyphicon-link"></span><a href="../login">登录页</a>]重新登录</td>
        </tr>
        <tr id="code2">
            <td style="text-align: center;">2</td>
            <td>500服务器端发生了错误</td>
            <td>详情查看系统运行日志</td>
        </tr>
        <tr id="code3">
            <td style="text-align: center;">3</td>
            <td>404找不到资源</td>
            <td>-</td>
        </tr>
    </tbody>

</table>
<script>
    $(document).ready(function(){
        var code = 0;
        code = "<%= code %>";
        if (code >= 1 && code <=3){
            $("#code"+code).addClass("danger");
        } else {
            $("#code0").addClass("success");
            $("#errorCode").html("错误码：-");
        }
    });
</script>
</body>
</html>
