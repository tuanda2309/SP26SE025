package com.example.SP26SE025.service;

import com.example.SP26SE025.entity.AnalysisRecord;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.AnalysisRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AnalysisRecordRepository analysisRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
    private final String AI_SERVICE_URL = "http://localhost:8000/predict";

    public AnalysisRecord saveAnalysis(MultipartFile file, User user) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        Path filePath = uploadPath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Gọi AI Service
        String aiResultJson = callAiService(filePath.toFile());

        AnalysisRecord record = new AnalysisRecord();
        record.setImageName(fileName);
        record.setImageUrl("/images/uploads/" + fileName);
        record.setUser(user);
        record.setCreatedAt(LocalDateTime.now());
        record.setAiResult(aiResultJson);

        return analysisRecordRepository.save(record);
    }

    private String callAiService(File file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(AI_SERVICE_URL, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return "{\"error\": \"AI Service returned " + response.getStatusCode() + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"Could not connect to AI Service: " + e.getMessage() + "\"}";
        }
    }

    public List<AnalysisRecord> getHistory(User user) {
        return analysisRecordRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public AnalysisRecord getById(Long id) {
        return analysisRecordRepository.findById(id).orElse(null);
    }
}
