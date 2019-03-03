function request(url, parameters){
    var defer = $.Deferred();
    $.ajax({
        url:url,
        data:parameters,
        method:"post",
        success:function(data){
            defer.resolve(data);
        }
    });
    return defer.promise();
}


/**
 * 用例
 * $.when(request(url, parameters)).done(function(data){
 *
 * });
 */