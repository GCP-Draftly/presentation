package init.frontendapi.service;

import init.common.utils.FileStorageUtil;
import init.frontendapi.dto.StorageRequest;
import init.frontendapi.dto.StorageResponse;
import init.frontendapi.dto.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.ArrayList;

@Service
public class StorageService {
	
	//cursor
	public boolean moveFile(String source, String dest) { return true; }
	public List<FileInfo> listFiles(String folder, int page, int size) { return new ArrayList<>(); }
	public FileInfo getFileInfo(String fileName) { return new FileInfo(); }

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Value("${app.storage.max-file-size:10485760}") // 10MB 기본값
    private long maxFileSize;

    @Value("${app.storage.allowed-types:pdf,docx,pptx,txt,md}")
    private String allowedFileTypes;

    /**
     * 파일을 저장소에 업로드합니다.
     * 
     * @param file 업로드할 파일
     * @param request 저장 요청 정보
     * @return 저장 결과 응답
     */
    public StorageResponse uploadFile(MultipartFile file, StorageRequest request) {
        try {
            // 파일 유효성 검증
            validateFile(file);
            
            String fileId = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            
            // 파일 저장
            String savedPath = fileStorageUtil.saveFile(file, fileId);
            
            // 파일 메타데이터 생성
            Map<String, Object> metadata = Map.of(
                "fileId", fileId,
                "originalFileName", originalFileName,
                "fileSize", file.getSize(),
                "contentType", file.getContentType(),
                "fileExtension", fileExtension,
                "uploadTime", LocalDateTime.now().toString(),
                "uploadedBy", request.getUserId() != null ? request.getUserId() : "anonymous",
                "tags", request.getTags() != null ? request.getTags() : List.of(),
                "description", request.getDescription() != null ? request.getDescription() : ""
            );
            
            return StorageResponse.builder()
                .fileId(fileId)
                .fileName(originalFileName)
                .filePath(savedPath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .metadata(metadata)
                .success(true)
                .message("파일이 성공적으로 업로드되었습니다.")
                .build();
                
        } catch (Exception e) {
            return StorageResponse.builder()
                .success(false)
                .message("파일 업로드 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일을 저장소에서 다운로드합니다.
     * 
     * @param fileId 다운로드할 파일 ID
     * @return 파일 데이터가 포함된 응답
     */
    public StorageResponse downloadFile(String fileId) {
        try {
            // 파일 데이터 로드
            byte[] fileData = fileStorageUtil.loadFile(fileId);
            
            // 파일 메타데이터 조회 (실제로는 DB에서 가져와야 함)
            Map<String, Object> metadata = getFileMetadata(fileId);
            
            return StorageResponse.builder()
                .fileId(fileId)
                .fileName((String) metadata.get("originalFileName"))
                .fileData(fileData)
                .fileSize((Long) metadata.get("fileSize"))
                .contentType((String) metadata.get("contentType"))
                .metadata(metadata)
                .success(true)
                .message("파일이 성공적으로 다운로드되었습니다.")
                .build();
                
        } catch (Exception e) {
            return StorageResponse.builder()
                .success(false)
                .message("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일을 저장소에서 삭제합니다.
     * 
     * @param fileId 삭제할 파일 ID
     * @return 삭제 결과 응답
     */
    public StorageResponse deleteFile(String fileId) {
        try {
            // 파일 삭제
            boolean deleted = fileStorageUtil.deleteFile(fileId);
            
            if (deleted) {
                return StorageResponse.builder()
                    .fileId(fileId)
                    .success(true)
                    .message("파일이 성공적으로 삭제되었습니다.")
                    .build();
            } else {
                return StorageResponse.builder()
                    .fileId(fileId)
                    .success(false)
                    .message("파일을 찾을 수 없습니다.")
                    .build();
            }
                
        } catch (Exception e) {
            return StorageResponse.builder()
                .fileId(fileId)
                .success(false)
                .message("파일 삭제 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일 목록을 조회합니다.
     * 
     * @param userId 사용자 ID (선택사항)
     * @param tags 태그 필터 (선택사항)
     * @return 파일 목록 응답
     */
    public StorageResponse getFileList(String userId, List<String> tags) {
        try {
            // 파일 목록 조회 로직 (실제로는 DB에서 가져와야 함)
            List<Map<String, Object>> fileList = getAllFiles()
                .stream()
                .filter(file -> userId == null || userId.equals(file.get("uploadedBy")))
                .filter(file -> tags == null || tags.isEmpty() || 
                    ((List<String>) file.get("tags")).stream().anyMatch(tags::contains))
                .collect(Collectors.toList());
            
            return StorageResponse.builder()
                .success(true)
                .message("파일 목록을 성공적으로 조회했습니다.")
                .metadata(Map.of("fileList", fileList, "totalCount", fileList.size()))
                .build();
                
        } catch (Exception e) {
            return StorageResponse.builder()
                .success(false)
                .message("파일 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일 메타데이터를 업데이트합니다.
     * 
     * @param fileId 파일 ID
     * @param request 업데이트 요청 정보
     * @return 업데이트 결과 응답
     */
    public StorageResponse updateFileMetadata(String fileId, StorageRequest request) {
        try {
            // 기존 메타데이터 조회
            Map<String, Object> existingMetadata = getFileMetadata(fileId);
            
            if (existingMetadata == null) {
                return StorageResponse.builder()
                    .fileId(fileId)
                    .success(false)
                    .message("파일을 찾을 수 없습니다.")
                    .build();
            }
            
            // 메타데이터 업데이트
            Map<String, Object> updatedMetadata = Map.of(
                "fileId", fileId,
                "originalFileName", existingMetadata.get("originalFileName"),
                "fileSize", existingMetadata.get("fileSize"),
                "contentType", existingMetadata.get("contentType"),
                "fileExtension", existingMetadata.get("fileExtension"),
                "uploadTime", existingMetadata.get("uploadTime"),
                "uploadedBy", existingMetadata.get("uploadedBy"),
                "tags", request.getTags() != null ? request.getTags() : existingMetadata.get("tags"),
                "description", request.getDescription() != null ? request.getDescription() : existingMetadata.get("description"),
                "lastModified", LocalDateTime.now().toString()
            );
            
            // 실제로는 DB에 저장해야 함
            saveFileMetadata(fileId, updatedMetadata);
            
            return StorageResponse.builder()
                .fileId(fileId)
                .fileName((String) updatedMetadata.get("originalFileName"))
                .metadata(updatedMetadata)
                .success(true)
                .message("파일 메타데이터가 성공적으로 업데이트되었습니다.")
                .build();
                
        } catch (Exception e) {
            return StorageResponse.builder()
                .fileId(fileId)
                .success(false)
                .message("파일 메타데이터 업데이트 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일 유효성을 검증합니다.
     * 
     * @param file 검증할 파일
     * @throws IllegalArgumentException 유효하지 않은 파일인 경우
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 " + (maxFileSize / 1024 / 1024) + "MB까지 허용됩니다.");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
        
        String fileExtension = getFileExtension(fileName);
        if (!isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. 허용되는 형식: " + allowedFileTypes);
        }
    }

    /**
     * 파일 확장자를 추출합니다.
     * 
     * @param fileName 파일명
     * @return 파일 확장자
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 허용되는 파일 형식인지 확인합니다.
     * 
     * @param fileExtension 파일 확장자
     * @return 허용되는 형식이면 true, 아니면 false
     */
    private boolean isAllowedFileType(String fileExtension) {
        String[] allowedTypes = allowedFileTypes.split(",");
        for (String type : allowedTypes) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 파일 메타데이터를 조회합니다.
     * 실제로는 DB에서 조회해야 합니다.
     * 
     * @param fileId 파일 ID
     * @return 파일 메타데이터
     */
    private Map<String, Object> getFileMetadata(String fileId) {
        // TODO: 실제 DB에서 메타데이터 조회 로직 구현
        // 현재는 임시로 더미 데이터 반환
        return Map.of(
            "fileId", fileId,
            "originalFileName", "example.pdf",
            "fileSize", 1024L,
            "contentType", "application/pdf",
            "fileExtension", "pdf",
            "uploadTime", LocalDateTime.now().toString(),
            "uploadedBy", "anonymous",
            "tags", List.of(),
            "description", ""
        );
    }

    /**
     * 파일 메타데이터를 저장합니다.
     * 실제로는 DB에 저장해야 합니다.
     * 
     * @param fileId 파일 ID
     * @param metadata 저장할 메타데이터
     */
    private void saveFileMetadata(String fileId, Map<String, Object> metadata) {
        // TODO: 실제 DB에 메타데이터 저장 로직 구현
        // 현재는 임시로 로그만 출력
        System.out.println("메타데이터 저장: " + fileId + " -> " + metadata);
    }

    /**
     * 모든 파일 목록을 조회합니다.
     * 실제로는 DB에서 조회해야 합니다.
     * 
     * @return 파일 목록
     */
    private List<Map<String, Object>> getAllFiles() {
        // TODO: 실제 DB에서 파일 목록 조회 로직 구현
        // 현재는 임시로 빈 리스트 반환
        return List.of();
    }
}