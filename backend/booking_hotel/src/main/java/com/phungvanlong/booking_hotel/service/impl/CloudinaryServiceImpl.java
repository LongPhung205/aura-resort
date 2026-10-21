package com.phungvanlong.booking_hotel.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.phungvanlong.booking_hotel.config.CloudinaryConfig;
import com.phungvanlong.booking_hotel.exception.BusinessException;
import com.phungvanlong.booking_hotel.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    private final CloudinaryConfig cloudinaryConfig;

    @Value("${app.upload-dir:frontend/public/assets/images/uploads}")
    private String uploadDir;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Vui lòng chọn tệp hình ảnh hợp lệ!");
        }

        // 1. Nếu đã cấu hình Cloudinary credentials -> Upload lên Cloudinary
        if (cloudinaryConfig.isConfigured()) {
            try {
                log.info("Bắt đầu tải ảnh lên Cloudinary folder: {}", folder);
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", (folder != null && !folder.isBlank()) ? folder : "aura-resort",
                                "resource_type", "image"
                        )
                );
                String secureUrl = (String) uploadResult.get("secure_url");
                log.info("Tải ảnh lên Cloudinary thành công: {}", secureUrl);
                return secureUrl;
            } catch (Exception e) {
                log.warn("Không thể tải ảnh lên Cloudinary ({}), chuyển sang lưu trữ cục bộ...", e.getMessage());
            }
        }

        // 2. Fallback: Lưu trữ trên ổ cứng máy chủ (Local Storage)
        return uploadLocally(file);
    }

    private String uploadLocally(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueName = "img_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path targetDir = Paths.get(uploadDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetPath = targetDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Lưu ảnh thành công vào local storage: {}", uniqueName);
            return "/assets/images/uploads/" + uniqueName;
        } catch (IOException e) {
            log.error("Lỗi lưu file ảnh local: {}", e.getMessage(), e);
            throw new BusinessException("Không thể lưu tệp ảnh: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        if (cloudinaryConfig.isConfigured() && publicId != null && !publicId.isBlank()) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Đã xóa ảnh trên Cloudinary: {}", publicId);
            } catch (Exception e) {
                log.warn("Lỗi khi xóa ảnh trên Cloudinary: {}", e.getMessage());
            }
        }
    }
}
