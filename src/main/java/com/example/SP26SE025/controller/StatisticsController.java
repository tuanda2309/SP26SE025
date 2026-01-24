package com.example.SP26SE025.controller;

import com.example.SP26SE025.repository.SubscriptionRepository;
import jakarta.servlet.http.HttpServletResponse; 

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet; 
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;     
import java.io.PrintWriter;     
import java.text.DecimalFormat; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.util.ArrayList;
import java.util.List;

@Controller
public class StatisticsController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("/clinic/reports/summary")
    public String showStatistics(Model model, @RequestParam(name = "year", defaultValue = "2026") int year) {
        
        List<Long> monthlyRevenueList = new ArrayList<>();
        List<String> pkgLabels = new ArrayList<>();
        List<Long> pkgData = new ArrayList<>();
        
        for (int i = 0; i < 12; i++) monthlyRevenueList.add(0L);

        try {
            List<Object[]> revenueData = subscriptionRepository.getMonthlyRevenue(year);
            List<Object[]> packageData = subscriptionRepository.getPackageUsageStatsByYear(year);
            Long totalRev = subscriptionRepository.getTotalRevenueByYear(year);
            Long totalCount = subscriptionRepository.countByYear(year);

            if (revenueData != null) {
                for (Object[] row : revenueData) {
                    if (row[0] != null && row[1] != null) {
                        int month = ((Number) row[0]).intValue(); 
                        long price = ((Number) row[1]).longValue();
                        
                        if (month >= 1 && month <= 12) {
                            monthlyRevenueList.set(month - 1, price);
                        }
                    }
                }
            }

            if (packageData != null) {
                for (Object[] row : packageData) {
                    if (row[0] != null) {
                        pkgLabels.add((String) row[0]);
                        pkgData.add(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                    }
                }
            }

            model.addAttribute("totalRevenue", totalRev != null ? totalRev : 0L);
            model.addAttribute("totalCount", totalCount != null ? totalCount : 0L);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi xử lý dữ liệu: " + e.getMessage());
        }

        model.addAttribute("selectedYear", year);
        model.addAttribute("monthlyRevenueList", monthlyRevenueList);
        model.addAttribute("pkgLabels", pkgLabels);
        model.addAttribute("pkgData", pkgData);

        return "clinic/statistics";
    }

    // ========================================================================
    // HÀM XUẤT BÁO CÁO CSV
    // ========================================================================
    
    @GetMapping("/clinic/reports/export")
    public void exportToExcel(@RequestParam(name = "year", defaultValue = "2026") int year, 
                              HttpServletResponse response) throws IOException {
        
        // 1. Cấu hình file trả về là Excel (.xlsx)
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"));
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=bao_cao_doanh_thu_" + year + "_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // 2. Lấy dữ liệu (Giữ nguyên logic cũ)
        List<Object[]> revenueData = subscriptionRepository.getMonthlyRevenue(year);
        List<Object[]> packageData = subscriptionRepository.getPackageUsageStatsByYear(year);
        Long totalRev = subscriptionRepository.getTotalRevenueByYear(year);

        // 3. Khởi tạo Workbook (File Excel)
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo " + year);

            // --- TẠO CÁC STYLE ĐỂ FORMAT ĐẸP ---
            
            // Style Tiêu đề lớn (In đậm, Chữ to)
            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeight(16);
            titleFont.setColor(IndexedColors.BLUE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style Header Bảng (Nền xanh, Chữ trắng, In đậm)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Style Tiền tệ (Có dấu phẩy ngăn cách: 1,000,000)
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0"));
            currencyStyle.setBorderBottom(BorderStyle.THIN);
            currencyStyle.setBorderTop(BorderStyle.THIN);
            currencyStyle.setBorderRight(BorderStyle.THIN);
            currencyStyle.setBorderLeft(BorderStyle.THIN);

            // Style Text thường có kẻ khung
            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderBottom(BorderStyle.THIN);
            borderStyle.setBorderTop(BorderStyle.THIN);
            borderStyle.setBorderRight(BorderStyle.THIN);
            borderStyle.setBorderLeft(BorderStyle.THIN);

            // --- BẮT ĐẦU VẼ EXCEL ---
            int rowIdx = 0;

            // 1. Dòng Tiêu đề: BÁO CÁO DOANH THU NĂM ...
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DOANH THU NĂM " + year);
            titleCell.setCellStyle(titleStyle);
            // Merge cột A và B
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            // Dòng ngày xuất
            Row dateRow = sheet.createRow(rowIdx++);
            dateRow.createCell(0).setCellValue("Ngày xuất:");
            dateRow.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            // Dòng tổng doanh thu
            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.createCell(0).setCellValue("Tổng doanh thu:");
            Cell totalValueCell = totalRow.createCell(1);
            totalValueCell.setCellValue(totalRev != null ? totalRev : 0);
            totalValueCell.setCellStyle(currencyStyle); // Format số tiền

            rowIdx++; // Dòng trống

            // 2. BẢNG CHI TIẾT THÁNG
            Row headerRow1 = sheet.createRow(rowIdx++);
            createCell(headerRow1, 0, "Tháng", headerStyle);
            createCell(headerRow1, 1, "Doanh Thu (VNĐ)", headerStyle);

            // Xử lý dữ liệu mảng doanh thu
            long[] monthlyRev = new long[12];
            if (revenueData != null) {
                for (Object[] row : revenueData) {
                    if (row[0] != null && row[1] != null) {
                        int month = ((Number) row[0]).intValue();
                        long price = ((Number) row[1]).longValue();
                        if (month >= 1 && month <= 12) {
                            monthlyRev[month - 1] = price;
                        }
                    }
                }
            }

            // Ghi 12 tháng
            for (int i = 0; i < 12; i++) {
                Row row = sheet.createRow(rowIdx++);
                createCell(row, 0, "Tháng " + (i + 1), borderStyle);
                
                Cell cellPrice = row.createCell(1);
                cellPrice.setCellValue(monthlyRev[i]);
                cellPrice.setCellStyle(currencyStyle); // Format tiền
            }

            rowIdx++; // Dòng trống

            // 3. BẢNG GÓI DỊCH VỤ
            Row headerRow2 = sheet.createRow(rowIdx++);
            createCell(headerRow2, 0, "Tên Gói Dịch Vụ", headerStyle);
            createCell(headerRow2, 1, "Số Lượng Bán", headerStyle);

            if (packageData != null) {
                for (Object[] rowData : packageData) {
                    Row row = sheet.createRow(rowIdx++);
                    String name = (rowData[0] != null) ? (String) rowData[0] : "Không xác định";
                    long count = (rowData[1] != null) ? ((Number) rowData[1]).longValue() : 0;

                    createCell(row, 0, name, borderStyle);
                    createCell(row, 1, count, borderStyle); 
                }
            }
            
            sheet.setColumnWidth(0, 25 * 256); 
            sheet.setColumnWidth(1, 25 * 256); 

            // Ghi workbook ra response
            workbook.write(response.getOutputStream());
        }
    }

    // Hàm phụ để tạo ô nhanh
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
}