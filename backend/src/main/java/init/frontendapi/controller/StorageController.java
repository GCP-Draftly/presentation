package init.frontendapi.controller;

import init.frontendapi.dto.FileInfo;
import init.frontendapi.dto.StorageRequest;
import init.frontendapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                       @RequestParam(required = false) String folder) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 비어있습니다.");
            }

            String fileUrl = storageService.uploadFile(file, folder);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "파일 업로드 완료",
                "fileUrl", fileUrl,
                "fileName", file.getOriginalFilename()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "파일 업로드 실패: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileData = storageService.downloadFile(fileName);
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(fileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", "파일 다운로드 실패: " + e.getMessage()
                ));
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            boolean deleted = storageService.deleteFile(fileName);
            return ResponseEntity.ok(Map.of(
                "success", deleted,
                "message", deleted ? "파일 삭제 완료" : "파일 삭제 실패"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "파일 삭제 실패: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestParam(required = false) String folder,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        try {
            var files = storageService.listFiles(folder, page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", files
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "파일 목록 조회 실패: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/info/{fileName}")
    public ResponseEntity<?> getFileInfo(@PathVariable String fileName) {
        try {
            var fileInfo = storageService.getFileInfo(fileName);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", fileInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", "파일 정보 조회 실패: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/move")
    public ResponseEntity<?> moveFile(@RequestBody StorageRequest request) {
        try {
            boolean moved = storageService.moveFile(request.getSourcePath(), request.getDestinationPath());
            return ResponseEntity.ok(Map.of(
                "success", moved,
                "message", moved ? "파일 이동 완료" : "파일 이동 실패"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "파일 이동 실패: " + e.getMessage()
                ));
        }
    }
}