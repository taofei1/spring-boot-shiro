var xmlData = "<operation_name>form_check_plan</operation_name><param>" + plan_no.value + "</param>";
var doc = common_execute("zfkj.core.Common.getCommonData", xmlData);
if (doc != null) {
    if (doc.selectSingleNode("//plan_name").text != null) {
        plan_word.innerHTML = "预案修订时间";
    }
}
xmlData = "<operation_name>query_all_machine</operation_name><param>" + system_no.getAttribute("code") + "</param>";
var doc2 = common_execute("zfkj.core.Common.getCommonData", xmlData);
loadFilterData(doc2);
xmlData = "<operation_name>query_plan_equip</operation_name><param>" + plan_no.value + "</param>";
doc2 = common_execute("zfkj.core.Common.getCommonData", xmlData);
if (typeof yjxt_equipment_list != 'undefined') {

    bindTable(yjxt_equipment_list, doc2.selectSingleNode("//query_plan_equip"), 'machine_name', doc2);
} else {

    bindTable(yjxt_equipment_list_log, doc2.selectSingleNode("//query_plan_equip"), 'machine_name', doc2);
}
console.log(doc2);
getFloatWindow();



xmlData = "<operation_name>query_plan_equip_log</operation_name><param>" + plan_no.value + "</param>";
doc2 = common_execute("zfkj.core.Common.getCommonData", xmlData);