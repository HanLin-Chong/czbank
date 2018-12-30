/**
 * 我（hanlin）项目中前端相关的需要的功能
 *
 */


/**
 *
 */
var HanLin = {
    //对iziToast的封装，防止toast重复出现，并且方便更换通知的插件
    info:function (msg){
        iziToast.info({message:msg});
    },
    warn:function (msg){
        iziToast.warn({message:msg});
    },
    error:function (msg){
        iziToast.error({
            message:msg
        });
    },
    success:function (msg){
        iziToast.success({message:msg});
    }
}