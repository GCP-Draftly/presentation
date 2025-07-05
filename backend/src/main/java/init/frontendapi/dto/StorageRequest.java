package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageRequest {
    
    @NotBlank(message = "파일명은 필수입니다.")
    private String fileName;
    
    private String folderPath;
    
    private String contentType;
    
    private Long fileSize;
    
    private String userId;
    
    private String sessionId;
    
    private Boolean isPublic;
    
    private LocalDateTime expiresAt;
    
    private String description;
    
    private Map<String, String> metadata;
    
    private String tags;
    
    // 파일 이동 관련
    private String sourcePath;
    private String destinationPath;
    
    // 파일 권한 관련
    private String accessLevel; // PUBLIC, PRIVATE, SHARED
    private String[] allowedUsers;
    
    // 파일 처리 옵션
    private Boolean compressImage;
    private String imageFormat; // JPEG, PNG, WEBP
    private Integer imageQuality; // 1-100
    
    // 백업 관련
    private Boolean createBackup;
    private String backupLocation;
    
    // 버전 관리
    private String version;
    private Boolean keepVersionHistory;
}