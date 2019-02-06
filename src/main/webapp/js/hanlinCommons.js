/**
 * 我（hanlin）项目中前端相关的需要的功能
 *
 */

var HanLin = {
    //常量，防止魔法值
    nraStatus:{//Nra状态码
        QUEUING:0,//排队中
        LOCKED:1,//已锁定
        PASS:2,//通过审核
        REFUSED:3,//拒绝申请
        CANCELED:4//主动取消申请
    },
    userStatus:{//用户状态
        BLOCK_UP:0,//停用
        NORMAL:1,//正常
        ROOT:2//ROOT账号
    },
    nraPriorityStatus:{//插队申请状态
        NORMAL:0,//未申请优先，普通文件
        WAITING:1,//申请已经提交
        REFUSED:2,//拒绝插队
        PRIORITY:3//插队文件
    },
    //对iziToast的封装，防止toast重复出现，并且方便更换通知的插件
    info: function (msg){
        iziToast.info({
            message:msg,
            layout:1
        });
    },
    warn: function (msg){
        iziToast.warning({
            message:msg,
            layout:1
        });
    },
    error: function (msg){
        iziToast.error({
            message:msg,
            layout:1
        });
    },
    success: function (msg){
        iziToast.success({
            message:msg,
            layout:1
        });
    },
    bootstrapInputGroup: function (target, options){
        //TODO 实现功能：使用json快速渲染出与(像经理页面ebay账号申请页中的表单)一样的表单
    },
    getFrom: function (formSelector){
        /**
         * 注意：用此方法获取form的值时，要保证所有要被取值input拥有class：HanLinGetForm和name：（返回JSON中的name）
         * @type {string}
         * @result [
         *      { #{name}:#{value} }
         *      ...
         * ]
         */
        /*
        var result = "[";
        $(".HanLinGetForm").each(function (index, e){
            var name = e.attr("name");
            var value = e.val();
            result.push({});
        });
        result += "]";*/
        //return JSON.parse(result);
        return $(formSelector).serializeArray();
    },
    request: function (url, parameters, functionDone, errorMsg){
        $.ajax({
            url: url,
            data: parameters,
            processData: false,  //tell jQuery not to process the data
            contentType: false,  //tell jQuery not to set contentType
            async: false,
            method: 'post',
            error: function(){

                HanLin.error(errorMsg);
            }
        }).done(function (data){
            functionDone(data);
        });
    },
    requestTypical: function (url, parameters, functionDone, errorMsg){
        $.ajax({
            url: url,
            data: parameters,
            async:false,
            method: "post",
            success:function (data){
                functionDone(data);
            },
            error: function (){
                HanLin.error(errorMsg);
            }
        });
    },
    //第四个参数targetDivSelector不传的时候则返回Html
    //level有: danger、warning、info、success
    bootstrapAlert: function (level, title, content, targetDivSelector){
        var legalLevels = ["danger", "warning", "info", "success"];
        var isLevelLegal = false;
        for (var i in legalLevels){
            if (legalLevels[i] == level){
                isLevelLegal = true;
                break;
            }
        }
        if (isLevelLegal){

            var alertHtml = "";
            if (targetDivSelector == undefined){
                alertHtml += '<div class="alert alert-'+level+' fade in">';
                alertHtml += '  <a href="#" class="close" data-dismiss="alert">&times;</a>';
                alertHtml += '  <strong>'+title+'</strong>&nbsp;&nbsp;&nbsp;&nbsp;'+content;
                alertHtml += '</div>';
            } else {
                var e = $(targetDivSelector);
                e.addClass("alert-"+level);
                if (!e.hasClass("alert")){
                    e.addClass("alert")
                }
                if (!e.hasClass("fade")){
                    e.addClass("fade");
                }
                if (!e.hasClass("in")){
                    e.addClass("in");
                }
                alertHtml += '<a href="#" class="close" data-dismiss="alert">&times;</a>';
                alertHtml += '<strong>'+title+'</strong>&nbsp;&nbsp;&nbsp;&nbsp;';
                alertHtml += content;
                e.html(alertHtml);
            }
        } else {
            console.error("hanlinCommons：不合法的bootstrapAlertLevel,合法值为[danger, warning, info, success]");
        }
    },
    isNumber: function (value){

        var patrn = /^(-)?\d+(\.\d+)?$/;
        if (patrn.exec(value) == null || value == "") {
            return false;
        } else {
            return true;
        }

    },
    isIntegerBetween: function (value, min, max){
        var pattern =  /^\d+$/;
        console.log(pattern.test(value));
        if (!pattern.test(value)) {
            console.log(1);
            return false

        } else {
            if (value < min || value > max){
                console.log(2);
                return false;
            }
            return true;
        }
    }
};

