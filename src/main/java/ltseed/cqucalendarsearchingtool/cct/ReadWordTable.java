package ltseed.cqucalendarsearchingtool.cct;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.*;

public class ReadWordTable {
    public static StuInfoInWord read(File file) throws IOException {
        StuInfoInWord s;
        // 读取Word文件
        POIFSFileSystem fis = null;
        try {
            fis = new POIFSFileSystem(file);
        } catch (Exception ignore) {
            return null;
        }
        try {
            XWPFDocument document;
            document = new XWPFDocument(new FileInputStream(file));
            // 获取所有表格
            List<XWPFTable> tables = document.getTables();
            StringBuilder sb = new StringBuilder();
            // 遍历每个表格
            for (XWPFTable table : tables) {
                // 获取表格中的所有行
                List<XWPFTableRow> rows = table.getRows();

                // 遍历每一行
                for (XWPFTableRow row : rows) {
                    // 获取行中的所有单元格
                    List<XWPFTableCell> cells = row.getTableCells();

                    // 遍历每个单元格
                    for (XWPFTableCell cell : cells) {
                        // 输出单元格内容
                        sb.append(cell.getText()).append("%");
                    }
                }
            }
            s = new StuInfoInWord(sb.toString());
            // 关闭文件流
            fis.close();
        } catch (NotOfficeXmlFileException e) {
            HWPFDocument document = new HWPFDocument(fis);
            // 获取所有表格
            Range r = document.getRange();
            TableIterator it = new TableIterator(r);

            // 遍历每个表格
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                Table table = it.next();
                for (int i = 0; i < table.numRows(); i++) {
                    TableRow row = table.getRow(i);
                    for (int i1 = 0; i1 < row.numCells(); i1++) {
                        String text = row.getCell(i1).text();
                        sb.append(text.replaceAll("\u0007", "").replaceAll("\u0013", "")).append("%");
                    }
                    sb.append("%");
                }
            }
            s = new StuInfoInWord(sb.toString());
            // 关闭文件流
            fis.close();
        }

        return s;
    }
    public static String getString(String d){
        return "";
    }
}
