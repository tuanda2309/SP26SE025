package com.example.SP26SE025.controller;

import com.example.SP26SE025.entity.AnalysisRecord;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.service.ReportService;
import com.example.SP26SE025.service.UserService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/customer/reports")
public class AnalysisController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/analysis")
    public String showAnalysis(Model model, Principal principal) {
        return "redirect:/customer/reports/history";
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("currentUser", user);
        return "customer/analysis_upload";
    }

    @GetMapping("/history")
    public String showHistory(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<AnalysisRecord> history = reportService.getHistory(user);
        model.addAttribute("historyList", history);
        return "customer/analysis_history";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        User user = userService.findByEmail(principal.getName());
        reportService.saveAnalysis(file, user);
        return "redirect:/customer/reports/history?success";
    }

    @GetMapping("/visualize/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> visualize(@PathVariable Long id) {
        AnalysisRecord record = reportService.getById(id);
        if (record == null)
            return ResponseEntity.notFound().build();

        // Forward request to AI Service for Grad-CAM
        String aiServiceUrl = "http://localhost:8000/predict_with_cam";

        // In a real scenario, we might need to send the original file path or re-upload
        // For simplicity, let's assume the AI service has access to the same storage or
        // we re-fetch
        // But predict_with_cam expects an UploadFile.
        // As a workaround, we'll just proxy the call if possible, or for now, return
        // 404 if not implemented

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/export/{id}")
    public void exportPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        AnalysisRecord record = reportService.getById(id);
        if (record == null)
            return;

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Analysis_Report_" + id + ".pdf");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            document.add(new Paragraph("KẾT QUẢ PHÂN TÍCH VÕNG MẠC - AI").setBold().setFontSize(18));
            document.add(new Paragraph("Mã bệnh nhân: " + record.getUser().getId()));
            document.add(new Paragraph("Họ tên: " + record.getUser().getFullName()));
            document.add(new Paragraph("Ngày phân tích: " + record.getCreatedAt()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Kết quả phân tích (JSON RAW):"));
            document.add(new Paragraph(record.getAiResult()));

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Lưu ý: Kết quả này chỉ mang tính chất tham khảo."));
        }
    }
}
