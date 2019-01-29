package easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        ImportParams params = new ImportParams();
        // params.setDataHandler();
        // params.setDataHanlder(new MapImportHanlder());
        Workbook workbook = null;
        FileOutputStream out = null;
        try {
            long start = new Date().getTime();
            List<Map<String, Object>> list = ExcelImportUtil.importExcel(
                    new File("C:\\Users\\K0160014\\Desktop\\项目文档\\项目文档\\绩效管理\\2季度业务量.xlsx"), Map.class, params);
            ExportParams exportParams = new ExportParams();
            //  workbook= ExcelExportUtil.exportExcel(exportParams,Map.class,list);
            // out = new FileOutputStream("C:\\Users\\K0160014\\Desktop\\项目文档\\项目文档\\绩效管理\\2季度业务量111.xlsx");
            workbook.write(out);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
