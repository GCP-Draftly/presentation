package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StorageResponse {
    
    private String fileId;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String downloadUrl;
    private String contentType;
    private Long fileSize;
    private String status; // UPLOADING, UPLOADED, PROCESSING, READY, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private String userId;
    
    // 파일 정보
    private String folderPath;
    private String fullPath;
    private String fileHash;
    private String encoding;
    private Boolean isPublic;
    
    // 이미지 정보 (이미지 파일인 경우)
    private Integer width;
    private Integer height;
    private String format;
    private Boolean isCompressed;
    private Integer quality;
    
    // 문서 정보 (문서 파일인 경우)
    private Integer pageCount;
    private String author;
    private String subject;
    private String keywords;
    
    // 메타데이터
    private Map<String, String> metadata;
    private String description;
    private String tags;
    private String version;
    
    // 권한 정보
    private String accessLevel;
    private String[] allowedUsers;
    
    // 통계 정보
    private Integer downloadCount;
    private LocalDateTime lastAccessedAt;
    private String lastAccessedBy;
    
    private byte[] fileData;
    private String filePath;
    private boolean success;
    
    private String message;
    
}