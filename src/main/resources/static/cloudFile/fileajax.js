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
/**
 * 回收战load js
 */
$(".loadfiletype").on("click", ".loadtrash", function () {
    var checkpathids = new Array();
    var checkfileids = new Array();
    checkedpaths2(checkpathids, checkfileids);

    var loadtype = $(".loadfiletype .box-header .loadfilestype").val();

    $(".loadfiletype").load("fileloadtrashfile", {
        type: loadtype,
        'checkpathids[]': checkpathids,
        'checkfileids[]': checkfileids
    });
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
        $(".loadfiletype").load(prefix + '/all?fileId=' + fileId);
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

function remove() {
    var names = [];
    var checkedFilesId = getCheckedFilesId(names);
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

/**
 * 重命名load js
 */
$(".loadfiletype").on("click", ".okfilerename", function () {
    var checkedfile = $(this).parents(".file-one.file-one-check");
    var loadtype = $(".loadfiletype .box-header .loadfilestype").val();

    var renamefp = checkedfile.find(".renamefp").val();
    var creatpathinput = checkedfile.find(".creatpathinput").val();
    var isfile = checkedfile.find(".isfile").val();
    var pathid = checkedfile.find(".pathid").val();

    console.log(renamefp);
    console.log(creatpathinput);
    console.log(isfile);
    console.log(pathid);

    $(".loadfiletype").load("fileloadrename", {
        type: loadtype,
        renamefp: renamefp,
        creatpathinput: creatpathinput,
        isfile: isfile,
        pathid: pathid
    })
});


/**
 * 得到选择的 文件和文件夹
 * @param pathids
 * @param fileids
 * @returns
 */
function checkedpaths2(pathids, fileids) {
    var checkedpaths = $(".file-one.file-one-check");
    var i = 0;
    var j = 0;
    console.log(checkedpaths);
    checkedpaths.each(function () {
        if ($(this).find(".file-img").hasClass("path")) {
            pathids[i] = $(this).find(".pathmessage").val();
            i += 1;
        } else {
            if (!$(this).hasClass("diplaynone")) {
                fileids[j] = $(this).find(".filemessage").val();
                console.log($(this).find(".filemessage").val());
                j += 1;
            }
        }
    });
}