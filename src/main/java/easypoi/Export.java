package easypoi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Export {
    public static void export(String sourceDir, String destDir) throws Exception {
        File dir = new File(sourceDir);
        File files[] = dir.listFiles();
        File export = null;
        Workbook wb = null;
        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            List<String> listPage = new ArrayList<>();
            InputStream stream = new FileInputStream(file);
            if (file.getName().endsWith("xls")) {
                wb = new HSSFWorkbook(stream);
            } else if (file.getName().endsWith("xlsx")) {
                wb = new XSSFWorkbook(stream);
            } else {
                throw new Exception(file.getName() + "是非Excel文件!");
            }

            for (int num = 0; num < wb.getNumberOfSheets(); num++) {
                Sheet s = wb.getSheetAt(num);
                if (s == null) {
                    continue;
                }
                Row row = s.getRow(4);
                if (row == null) {
                    throw new Exception(file.getName() + "第五行数据有误！");
                }
                if (export == null) {
                    export = file;
                }
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    if (row.getCell(j) == null) {
                        listPage.add("");
                    } else {

                        listPage.add(getStringVal(row.getCell(j)));
                    }
                }

            }
            list.add(listPage);
        }
        writeData(export, list, new File(destDir + "/上市公司2018年1-6月生产经营情况调查汇总表.xlsx"));

    }

    public static void writeData(File file, List<List<String>> list, File dest) throws IOException {
        Workbook wb = new XSSFWorkbook(new FileInputStream(file));

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        OutputStream os = new FileOutputStream(dest);
        for (int x = 0; x < wb.getNumberOfSheets(); x++) {
            Sheet st = wb.getSheetAt(x);
            if (st == null) {
                continue;
            }
            Short height = st.getRow(4).getHeight();
            //获取合并单元格
            int mergedCount = st.getNumMergedRegions();
            //将合并单元格移除
            st.removeMergedRegion(mergedCount - 1);

            for (int i = 4; i < st.getPhysicalNumberOfRows(); i++) {
                st.removeRow(st.getRow(i));
            }

            for (int i = 4; i < list.size() + 4; i++) {
                Row row = st.createRow(i);
                row.setHeight(height);
                for (int j = 0; j < list.get(i - 4).size(); j++) {
                    Cell cell = row.createCell(j);

                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(list.get(i - 4).get(j));
                }
            }
        }
        wb.write(os);
        os.close();

    }

    public static String getStringVal(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }


}
