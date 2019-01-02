/*
   将表单转换为Json
 */
$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [ o[this.name] ];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$.fn.serializeParam =function(param){
    var a=this.serializeArray();
    $.each(a, function() {
        if(this.value!='') {
            param[this.name] = this.value;
        }
    });
    return param;

};
/**
 * 表单自动填充id与key对应
 * @param data
 */
$.fn.setValue=function (data) {
    console.log(data);
    var a=this.serializeArray();
    $.each(a,function () {

       document.getElementById(this.name).value=data[this.name]||'';

    })
}
$.fn.clear=function(id){
    $(':input',id).not(':button,:submit,:reset,:hidden')

        .val('')  //将input元素的value设为空值

        .removeAttr('checked')

        .removeAttr('checked')
}