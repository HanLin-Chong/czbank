/**
 * 我（hanlin）项目中前端相关的需要的功能
 *
 */

var HanLin = {
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
            url: "c/ebayApplication",
            data: formData,
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
    }
};

