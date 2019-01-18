/** 弹窗状态码 */
var modal_status = {
    SUCCESS: "success",
    FAIL: "error",
    WARNING: "warning"
};
layui.use("layer", function () {

    var layer = layui.layer;
    window.msgError = function (content) {
        msg(content, modal_status.WARNING);
    };
    window.msg = function (content, type) {
        if (type != undefined) {
            layer.msg(content, {icon: type, time: 1000, shift: 5});
        } else {
            layer.msg(content);
        }
    };
    window.layConfirm = function (content, callBack) {
        layer.confirm(content, {
            icon: 3,
            title: "系统提示",
            btn: ['确认', '取消'],
            btnclass: ['btn btn-primary', 'btn btn-danger'],
        }, function (index) {
            layer.close(index);
            if (callBack) {
                callBack(true);
            }
        });

    }
    window.icon = function (type) {
        var icon = "";
        if (type == modal_status.WARNING) {
            icon = 0;
        } else if (type == modal_status.SUCCESS) {
            icon = 1;
        } else if (type == modal_status.FAIL) {
            icon = 2;
        } else {
            icon = 3;
        }
        return icon;
    };
});