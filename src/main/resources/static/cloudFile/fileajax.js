var prefix = '/file';
/**
 * 复制移动选择目标文件文件夹并ajax加载选择文件加下的文件夹
 */
$("#thismodal .box-body").on("click", ".openpath", function () {

    var mctoid = $(this).find(".mctopathid").val();

    var mcpathids = $("#thismodal .box-footer .mcpathids").val();

    $(".box-footer .mctoid").val(mctoid);

    if ($(this).hasClass("modalajax")) {
        console.log("modalajax");
        var $ul = $(this).parents(".box-header").next();
        $(this).parents(".box-header").find(".jiajian").addClass("glyphicon-minus").removeClass("glyphicon-plus");
        $ul.css("display", "block");

        $ul.load("mcloadpath", {mctoid: mctoid, mcpathids: mcpathids});
    } else {
        console.log("box-header");
    }
});


/**
 * 搜索js
 */
$(".loadfiletype").on("click", ".findfileandpathgo", function () {
    var findfileandpath = $(".loadfiletype .box-header .findfileandpath").val();
    loadFiles(findfileandpath);

});

function loadFiles(search, type) {
    var loadType = !type ? $("#loadType").val() : type;
    switch (loadType) {
        case 'all':
            if (search == null || search == '') {
                $(".loadfiletype").load(prefix + "/all?fileId=1");
            } else {
                $(".loadfiletype").load(prefix + "/all?fileName=" + search);
            }
            break;
        case 'share':
            if (search == null || search == '') {
                $(".loadfiletype").load(prefix + "/share");
            } else {
                $(".loadfiletype").load(prefix + "/share?fileName=" + search);
            }
            break;
        case 'trash':
            if (search == null || search == '') {
                $(".loadfiletype").load(prefix + "/trash");
            } else {
                $(".loadfiletype").load(prefix + "/trash?fileName=" + search);
            }
            break;
        default :
            if (search == null || search == '') {
                $(".loadfiletype").load(prefix + "/" + loadType);
            } else {
                $(".loadfiletype").load(prefix + "/" + loadType + "?fileName=" + search);
            }

    }
}

/**
 * 删除load js
 */
$(".loadfiletype").on("click", ".loaddelete", function () {
    var checkpathids = new Array();
    var checkfileids = new Array();
    checkedpaths2(checkpathids, checkfileids);

    var loadtype = $(".loadfiletype .box-header .loadfilestype").val();

    console.log(checkpathids);
    console.log(checkfileids);
    console.log(loadtype);

    $(".loadfiletype").load("fileloaddeletefile", {
        type: loadtype,
        'checkpathids[]': checkpathids,
        'checkfileids[]': checkfileids
    });

});
$(".loadfiletype").on("click", ".loadokshare", function () {
    var checkpathids = new Array();
    var checkfileids = new Array();
    checkedpaths2(checkpathids, checkfileids);
    var loadtype = $(".loadfiletype .box-header .loadfilestype").val();

    $(".loadfiletype").load("fileloadshare", {type: loadtype, 'checkfileids[]': checkfileids});

});


$(".loadfiletype").on("click", ".filereturnback", function () {
    var checkpathids = new Array();
    var checkfileids = new Array();
    checkedpaths2(checkpathids, checkfileids);
    console.log("filereturnback");
    var loadtype = $(".loadfiletype .box-header .loadfilestype").val();

    $(".loadfiletype").load("filereturnback", {
        type: loadtype,
        'checkpathids[]': checkpathids,
        'checkfileids[]': checkfileids
    });

});

/**
 * 部分页面刷新
 * @param fileId
 */
function refresh(fileId) {
    if (fileId != null && fileId != "") {
        var loadType = $("#loadType").val()
        if (loadType == 'share') {
            $(".loadfiletype").load(prefix + '/all?isShare=1&fileId=' + fileId);
        } else {
            $(".loadfiletype").load(prefix + '/all?fileId=' + fileId);
        }
    } else {
        //id不存在则是分类或者搜索结果标签

        var searchValue = $("#searchValue").val();
        loadFiles(searchValue);
    }

}

function restore() {
    var checkedFilesId = getCheckedFilesId();
    $.post(prefix + "/restore", {ids: checkedFilesId}, function (res) {

        if (res.code != '00') {
            msgError(res.data);
        } else {
            refresh();
        }
    })
}

function share() {
    var checkedFilesId = getCheckedFilesId();
    $.post(prefix + "/share", {ids: checkedFilesId}, function (res) {

        if (res.code != '00') {
            msgError(res.data);
        } else {
            msgSuccess('分享文件成功！');
        }
    });
}

function cancelShare() {
    var checkedFilesId = getCheckedFilesId();
    $.post(prefix + "/cancelShare", {ids: checkedFilesId}, function (res) {
        if (res.code != '00') {
            msgError(res.data);
        } else {
            msgSuccess('取消分享成功！');
        }
    });
}

function rename(a) {
    var fileId = $(a).parent("button").prev().val();
    var newName = $(a).parent("button").prev().prev().val();
    console.log(fileId + newName);
    $.post(prefix + '/rename/' + fileId, {newName: newName}, function (res) {
        if (res.code != '00') {
            msgError(res.data);

        }
        refresh($('#parentId').val());
    })

}
function remove() {
    var names = [];
    var checkedFilesId = getCheckedFilesId(names);
    console.log(checkedFilesId);
    if (checkedFilesId.length == 0) {
        msgError("请选择文件或文件夹！");
    } else {
        if ($('#loadType').val() == 'trash') {
            layConfirm('确定要将"' + names.join(',') + '"彻底删除吗?删除后不可恢复', function () {
                $.post(prefix + "/remove", {ids: checkedFilesId}, function (res) {
                    if (res.code != '00') {
                        msgError(res.data);
                    } else {
                        refresh($("#currentPathId").val());
                    }
                });
            });
        } else {
            layConfirm('确定要将"' + names.join(',') + '"放入回收站吗?', function () {
                $.post(prefix + "/trash", {ids: checkedFilesId}, function (res) {
                    if (res.code != '00') {
                        msgError(res.data);
                    } else {
                        refresh($("#currentPathId").val());
                    }
                });
            });
        }
    }

}

function createDir() {
    var name = $("#dirName").val();
    var parentId = $("#dirParentId").val();

    $(".loadfiletype").load(prefix + '/createDir', {fileId: parentId, dirName: name});
}

function fileInfo() {
    var text = "";
    var names = [];
    var checkeIds = getCheckedFilesId(names);
    if (checkeIds.length == 1) {
        text += names[0] + "<br>";
    }
    console.log(text);
    var dir = 0;
    var file = 0;
    var updateTime = "";
    $.each(clouFiles, function (n, value) {

        for (var i in checkeIds) {
            if (value.fileId == checkeIds[i]) {
                if (checkeIds.length == 1) {
                    updateTime = value.updateTime;
                }
                if (value.isDirectory == 1) {
                    dir++
                } else {
                    file++;
                }
            }
        }

    });
    if (dir == 0 && file > 0) {
        if (file > 1) {
            text += file + "个文件<br>";
        }
        text += "文件类型：文件<br>";
    } else if (dir > 0 && file == 0) {
        text += "包含：<span class='fileContent'>正在计算</span><br>";
        text += "文件类型：文件夹<br>";
    } else if (dir > 0 && file > 0) {
        text += "包含：<span class='fileContent'>正在计算</span><br>";
        text += "文件类型：文件&文件夹<br>";
    } else {
        return;
    }
    text += "文件大小：<span class='size'>正在计算。</span><br>"
    text += "位置：";
    var lay;
    $.getJSON("/file/commonPath?ids[]=" + checkeIds, function (res) {
        console.log(res);
        if (res.code == '00') {
            var result = res.data;
            if (checkeIds.length > 1) {
                text += "全部位于&nbsp;";
            }
            text += result + "<br>";
            if (checkeIds.length == 1) {
                text += "修改时间：" + updateTime;
            }
            $('#layerContent').html(text);
            lay = layuiOpen($('#layerContent'), 1, "文件信息", 400, 250);
        } else {
            msgError(res.data);
        }
    });

    $.getJSON('/file/filesInfo?ids[]=' + checkeIds, function (res) {
        if (res.code != '00') {
            msgError(res.data);
        } else {
            var result = res.data;
            if ($('#layerContent').find('.fileContent').length > 0) {
                $('#layerContent').find('.fileContent').html(result.filesNum + "个文件" + "&nbsp" + result.dirsNum + "个文件夹");
            }
            $('#layerContent').find('.size').html(result.size);
        }
    })
}

/**
 *
 */
$("#upload").click(function () {
    var formData = new FormData();
    formData.append("multipartFile", document.getElementById("files").files[0]);
    formData.append("parentId",$("#currentPathId").val());
    /*    window.setInterval(function(){$.getJSON("/file/uploadProgress",function (res) {
            console.log(res);
        })},100);*/
    $.ajax({
        url: prefix+"/upload",
        type: "POST",
        data: formData,
        /**
         *必须false才会自动加上正确的Content-Type
         */
        contentType: false,
        /**
         * 必须false才会避开jQuery对 formdata 的默认处理
         * XMLHttpRequest会对 formdata 进行正确的处理
         */
        processData: false,
        success: function (data) {
            if (data.code=='00') {
                msgSuccess("上传成功");
                $("#files").val("");
                refresh($('#currentPathId').val());
            }else{
                msgError(data.data);
            }
        },
        xhr: function () {
            var xhr = $.ajaxSettings.xhr();
            if (xhr.upload) {
                //处理进度条的事件
                xhr.upload.addEventListener("progress", progressHandle, false);
                //加载完成的事件
                xhr.addEventListener("load", completeHandle, false);
                //加载出错的事件
                xhr.addEventListener("error", failedHandle, false);
                //加载取消的事件
                xhr.addEventListener("abort", canceledHandle, false);
                //开始显示进度条
                showProgress();
                return xhr;
            }
        }
    });
});
var start = 0;

//显示进度条的函数
function showProgress() {
    start = new Date().getTime();
    $(".progress-body").css("display", "block");
}

//隐藏进度条的函数
function hideProgress() {
    $("#uploadFile").val('');
    $('.progress-body .progress-speed').html("0 M/S, 0/0M");
    $('.progress-body percentage').html("0%");
    $(".progress-body").css("display", "none");
}

//进度条更新
function progressHandle(e) {
    $('.progress-body progress').attr({value: e.loaded, max: e.total});
    var percent = e.loaded / e.total * 100;
    var time = ((new Date().getTime() - start) / 1000).toFixed(3);
    if (time == 0) {
        time = 1;
    }
    $('.progress-body .progress-speed').html(((e.loaded / 1024) / 1024 / time).toFixed(2) + "M/S, " + ((e.loaded / 1024) / 1024).toFixed(2) + "/" + ((e.total / 1024) / 1024).toFixed(2) + " MB. ");
    $('.progress-body percentage').html(percent.toFixed(2) + "%");
    if (percent == 100) {
        $('.progress-body .progress-info').html("上传完成,后台正在处理...");
    } else {
        $('.progress-body .progress-info').html("文件上传中...");
    }
};

//上传完成处理函数
function completeHandle(e) {
    $('.progress-body .progress-info').html("上传文件完成。");
    setTimeout(hideProgress, 3000);
};

//上传出错处理函数
function failedHandle(e) {
    $('.progress-body .progress-info').html("上传文件出错, 服务不可用或文件过大。");
};

//上传取消处理函数
function canceledHandle(e) {
    $('.progress-body .progress-info').html("上传文件取消。");
};




