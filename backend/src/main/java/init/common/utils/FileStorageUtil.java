package init.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class FileStorageUtil {
	
	//cursor
	public byte[] loadFile(String fileId) { return new byte[0]; }
	public String saveFile(MultipartFile file, String fileId) { return "저장경로"; }

    @Value("${file.upload.dir:/tmp/uploads}")
    private String uploadDir;

    @Value("${file.presentation.dir:/tmp/presentations}")
    private String presentationDir;

    @Value("${file.max.size:10485760}") // 10MB
    private long maxFileSize;

    /**
     * 파일 저장
     */
    public String saveFile(String sourceFilePath, String presentationId) throws Exception {
        try {
            // 저장 디렉토리 생성
            Path presentationPath = Paths.get(presentationDir);
            if (!Files.exists(presentationPath)) {
                Files.createDirectories(presentationPath);
            }

            // 파일명 생성
            String fileName = generateFileName(presentationId, "pptx");
            Path targetPath = presentationPath.resolve(fileName);

            // 파일 복사
            Files.copy(Paths.get(sourceFilePath), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();

        } catch (Exception e) {
            throw new Exception("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 업로드된 파일 저장
     */
    public String saveUploadedFile(MultipartFile file) throws Exception {
        try {
            // 파일 크기 검증
            if (file.getSize() > maxFileSize) {
                throw new Exception("파일 크기가 제한을 초과했습니다. 최대 크기: " + (maxFileSize / 1024 / 1024) + "MB");
            }

            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String extension = getFileExtension(originalFileName);
            String fileName = generateFileName(UUID.randomUUID().toString(), extension);
            Path targetPath = uploadPath.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();

        } catch (Exception e) {
            throw new Exception("업로드 파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 읽기
     */
    public String readFileAsString(String filePath) throws Exception {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new Exception("파일 읽기 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 읽기 (바이트 배열)
     */
    public byte[] readFileAsBytes(String filePath) throws Exception {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (Exception e) {
            throw new Exception("파일 읽기 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 파일 크기 조회
     */
    public long getFileSize(String filePath) throws Exception {
        try {
            return Files.size(Paths.get(filePath));
        } catch (Exception e) {
            throw new Exception("파일 크기 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 임시 파일 생성
     */
    public String createTempFile(String prefix, String suffix) throws Exception {
        try {
            Path tempFile = Files.createTempFile(prefix, suffix);
            return tempFile.toString();
        } catch (Exception e) {
            throw new Exception("임시 파일 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 디렉토리 생성
     */
    public void createDirectory(String dirPath) throws Exception {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            throw new Exception("디렉토리 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 파일명 생성
     */
    private String generateFileName(String id, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return id + "_" + timestamp + "." + extension;
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    /**
     * 파일 타입 검증
     */
    public boolean isValidFileType(String fileName, String[] allowedTypes) {
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowedType : allowedTypes) {
            if (allowedType.toLowerCase().equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 안전한 파일명 생성 (특수문자 제거)
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unnamed";
        }
        return fileName.replaceAll("[^a-zA-Z0-9가-힣._-]", "_");
    }
}