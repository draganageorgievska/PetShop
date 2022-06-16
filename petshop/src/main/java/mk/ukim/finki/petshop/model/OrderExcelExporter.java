package mk.ukim.finki.petshop.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class OrderExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ShoppingCart> orders;

    public OrderExcelExporter(List<ShoppingCart> orders) {
        this.orders = orders;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Orders");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Shopping Cart ID", style);
        createCell(row, 1, "Date created", style);
        createCell(row, 2, "Customer email", style);
        createCell(row, 3, "Products ordered information", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (ShoppingCart shoppingCart : orders) {
            StringBuilder sb = new StringBuilder();
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            List<ProductInCart> products=shoppingCart.getProducts();
            for (ProductInCart product: products){
                sb.append("Product: "+product.getProduct().getName()+", with quantity of: "+product.getQuantity()+", and price of: "+product.getProduct().getPrice()+"\r\n");
            }
            createCell(row, columnCount++, shoppingCart.getId().toString(), style);
            createCell(row, columnCount++, shoppingCart.getDateCreated().toString(), style);
            createCell(row, columnCount++, shoppingCart.getUser().getEmail(), style);
            createCell(row, columnCount++, sb.toString(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
