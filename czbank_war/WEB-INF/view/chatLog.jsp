<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.relesee.service.MessageService" %>
<%@ page import="com.relesee.domains.layim.Message" %>
<%@ page import="java.util.List" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>消息记录</title>
    <!-- 禁止用户缩放 -->
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>

    <!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
    <!--[if lt IE 9]>
    <script src="../../js/html5.min.js"></script>
    <script src="../../js/respond.min.js"></script>
    <![endif]-->

    <link rel="icon" href="../../img/czyh.png">
    <link rel="stylesheet" href="../../css/bootstrap.modified.css"/>
    <!-- iziToast -->
    <link rel="stylesheet" href="../../css/iziToast.min.css"/>
    <link rel="stylesheet" href="../../layuiIM/css/layui.css"/>

    <script src="../../js/jquery-1.11.0.min.js"></script>
    <script src="../../js/bootstrap.min.js"></script>
    <script src="../../js/iziToast.min.js"></script>
    <script src="../../js/deferredAjax.js"></script>
    <script src="../../js/hanlinCommons.js"></script>
    <script src="../../layuiIM/layui.all.js"></script>
</head>
<body>
<%--<div class="col-lg-12">
    <div class="col-lg-12">
        <ul class="list-group">
            <li class="list-group-item">无消息记录</li>
        </ul>
    </div>
</div>--%>
<script>
    <%
        String id = request.getParameter("id");
        String type = request.getParameter("type");
    %>
    $(document).ready(function (){
        var id = "<%= id %>";
        var type = "<%= type %>";
        HanLin.requestTypical("message/getChatLog", {id:id, type:type}, function (data){
            data = JSON.parse(data);
            writeChatLog(data, id);
        }, "获取离线消息出错");
    });
function writeChatLog(data, targetId){
    var html = "";
    for (var i in data){
        console.log(data[i]);
        if (data[i].recipientId == targetId){
            //我发送的消息
            html += '<li class="layim-chat-mine">';
            html += '   <div class="layim-chat-user">';
            //html += '       <img src="'+data[i].avatar+'">';
            html += '       <cite>'+data[i].username+'<i>'+format(data[i].timestamp)+'</i><span class="label label-info">我发送的</span>:</cite>';
            html += '   </div><br/>';
            html += '   <div class="layim-chat-text" style="word-break:break-all">';
            html += '       '+data[i].content;
            html += '   </div>';
            html += '</li><hr/>';
        } else {
            //发给我的消息
            html += '<li>';
            html += '   <div class="layim-chat-user">';
            //html += '       <img src="'+data[i].avatar+'">';
            html += '       <cite>'+data[i].username+'<i>'+format(data[i].timestamp)+'</i>:</cite>';
            html += '   </div><br/>';
            html += '   <div class="layim-chat-text"  style="word-break:break-all">';
            html += '       '+data[i].content;
            html += '   </div>';
            html += '</li><hr/>';
        }
    }
    $("body").html(html);
}

function format(timestamp) {
    //timestamp是整数，否则要parseInt转换,不会出现少个0的情况
    var time = new Date(timestamp);
    var year = time.getFullYear();
    var month = time.getMonth() + 1;
    var date = time.getDate();
    var hours = time.getHours();
    var minutes = time.getMinutes();
    var seconds = time.getSeconds();
    return year + '-' + add0(month) + '-' + add0(date) + ' ' + add0(hours) + ':' + add0(minutes) + ':' + add0(seconds);
}
function add0(m){
    return m<10?'0'+m:m;
}
</script>
</body>
</html>