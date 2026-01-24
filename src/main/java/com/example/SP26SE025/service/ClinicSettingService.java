package com.example.SP26SE025.service;

import com.example.SP26SE025.entity.ClinicProfile;
import com.example.SP26SE025.entity.VerificationStatus;
import com.example.SP26SE025.repository.ClinicProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
public class ClinicSettingService {

    @Autowired
    private ClinicProfileRepository clinicProfileRepository;

    private static final String UPLOAD_DIR =
            "src/main/resources/static/uploads/verification/";

    /**
     * 🔹 LẤY PROFILE
     * ❌ KHÔNG TỰ TẠO
     */
    public ClinicProfile getProfile(String username) {

    Optional<ClinicProfile> optional =
            clinicProfileRepository.findByUsernameLink(username);

    if (optional.isPresent()) {
        return optional.get();
    }

    // 👉 CHƯA CÓ → TẠO MỚI
    ClinicProfile profile = new ClinicProfile();
    profile.setUsernameLink(username);
    profile.setClinicName("Phòng khám mới");
    profile.setVerificationStatus(VerificationStatus.PENDING);

    return clinicProfileRepository.save(profile);
}


    /**
     * 🔹 TẠO PROFILE LẦN ĐẦU (gọi khi clinic vào setting)
     */
    public ClinicProfile createProfileIfNotExist(String username) {

        Optional<ClinicProfile> existing =
                clinicProfileRepository.findByUsernameLink(username);

        if (existing.isPresent()) {
            return existing.get();
        }

        ClinicProfile profile = new ClinicProfile();
        profile.setUsernameLink(username);
        profile.setClinicName("Phòng khám mới");
        profile.setVerificationStatus(VerificationStatus.PENDING);

        return clinicProfileRepository.save(profile);
    }

    /**
     * 🔹 CẬP NHẬT THÔNG TIN CHUNG
     */
    public void updateGeneralInfo(String username, ClinicProfile updatedInfo) {

        ClinicProfile profile = getProfile(username);

        profile.setClinicName(updatedInfo.getClinicName());
        profile.setRepresentativeName(updatedInfo.getRepresentativeName());
        profile.setAddress(updatedInfo.getAddress());
        profile.setPhone(updatedInfo.getPhone());
        profile.setWebsite(updatedInfo.getWebsite());
        profile.setDescription(updatedInfo.getDescription());

        clinicProfileRepository.save(profile);
    }

    /**
     * 🔹 UPLOAD HỒ SƠ XÁC MINH
     */
    public void uploadVerificationDocs(
            String username,
            String taxId,
            MultipartFile businessFile,
            MultipartFile medicalFile
    ) throws IOException {

        ClinicProfile profile = getProfile(username);
        profile.setTaxId(taxId);

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        if (businessFile != null && !businessFile.isEmpty()) {
            String fileName =
                    System.currentTimeMillis() + "_business_" +
                            businessFile.getOriginalFilename();

            Files.write(
                    Paths.get(UPLOAD_DIR + fileName),
                    businessFile.getBytes()
            );

            profile.setBusinessLicenseUrl(
                    "/uploads/verification/" + fileName
            );
        }

        if (medicalFile != null && !medicalFile.isEmpty()) {
            String fileName =
                    System.currentTimeMillis() + "_medical_" +
                            medicalFile.getOriginalFilename();

            Files.write(
                    Paths.get(UPLOAD_DIR + fileName),
                    medicalFile.getBytes()
            );

            profile.setMedicalLicenseUrl(
                    "/uploads/verification/" + fileName
            );
        }

        // ⭐ Khi upload → quay về PENDING
        profile.setVerificationStatus(VerificationStatus.PENDING);

        clinicProfileRepository.save(profile);
    }

    /**
     * 🔹 ĐỔI MẬT KHẨU (chưa dùng)
     */
    public boolean changePassword(
            String username,
            String currentPass,
            String newPass
    ) {
        return true;
    }
}
