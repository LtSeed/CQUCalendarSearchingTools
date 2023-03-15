package ltseed.cqucalendarsearchingtool;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

public class ReadWordTable {
    public static void read(File file) throws IOException {
        // 读取Word文件
        FileInputStream fis = new FileInputStream(file);
        XWPFDocument document = new XWPFDocument(fis);

        // 获取所有表格
        List<XWPFTable> tables = document.getTables();

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
                    System.out.print(cell.getText() + "\t");
                }
                System.out.println();
            }
        }

        // 关闭文件流
        fis.close();
    }
}
