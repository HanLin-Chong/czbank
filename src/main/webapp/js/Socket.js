/**
 *   用于与后台进行socket通信
 */


//websocket = new WebSocket("ws://");

//声明wsOperation对象
//构造方法
function wsOperation(path,userId){
	//this.attr=attr;
	//this.websocket = new WebSocket(path);
	//alert(attr+"构造方法");

    if ('WebSocket' in window) {  
        this.websocket = new WebSocket("ws://" + path + "wsMy?jspCode=" +userId);  
    } else if ('MozWebSocket' in window) {  
        this.websocket = new MozWebSocket("ws://" + path +  "wsMy?jspCode=" + userId);  
    } else {  
        this.websocket = new SockJS("http://" + path + "wsMy/sockjs?jspCode=" + userId);  
    }
/*
	this.websocket.onopen = function(event) {  

        show("WebSocket:已连接");  
        //show(event); 
    };  */
    
    /**
     * 收到消息
     */
    this.websocket.onmessage = function(event) {  
        var data = event.data;  

        msgProcessor(data);
        //console.log("WebSocket:收到一条消息-norm", data);  
        //show("WebSocket:收到一条消息"+data);  
    };  
    
    this.websocket.onerror = function(event) {  
    
    
    
    
        
    };  
    this.websocket.onclose = function(event) {  
          
    }  
}

//方法
wsOperation.prototype.send = function(accepterid,text){

	$.ajax({
		url:"message/send",
		data:{accepterid:accepterid,text:text},
		async:false,method:"post",
		success:function(response){
			console.log("发送成功");
			console.log(response);
		}
	})
}


