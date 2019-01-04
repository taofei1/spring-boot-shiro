(function ($) {
    $.extend({
            // 表格封装处理
            table: {
                _option: {},
                _params: {},
                init: function (options) {
                    $.table._option = options;
                    $.table._params = $.common.isEmpty(options.queryParams) ? $.table.queryParams : options.queryParams;
                    _sortOrder = $.common.isEmpty(options.sortOrder) ? "asc" : options.sortOrder;
                    _sortName = $.common.isEmpty(options.sortName) ? "" : options.sortName;
                    _striped = $.common.isEmpty(options.striped) ? false : options.striped;
                    _escape = $.common.isEmpty(options.escape) ? false : options.escape;
                    layui.use(['table', 'layer', 'laydate'], function () {
                        var tableIns = table.render({
                            elem: options.id
                            , defaultToolbar: options.defaultToolbar
                            , toolbar: "#toolbar"
                            , title: options.title
                            , autoSort: $.common.isEmpty(options.autoSort) ? false : options.autoSort
                            , page: $.common.isEmpty(options.page) ? false : options.page
                            , request: {
                                pageName: 'pageNum' //页码的参数名称，默认：page
                                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                            }
                            , url: options.url //数据接口
                            , parseData: $.table.reponseHandler
                            //开启分页
                            , cols: options.cols
                        });

                    })

                },
                reponseHandler: function (res) {
                    var result = {
                        "code": res.code, //解析接口状态
                        "count": res.data.total, //解析数据长度
                        "data": res.data.list //解析数据列表
                    };
                    if (res.code != '00') {
                        msg = res.data || res.message;
                        result['msg'] = msg;
                    }
                    return result;
                },

            }
        }
    )
}