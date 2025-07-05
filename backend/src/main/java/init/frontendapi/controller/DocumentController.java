package init.frontendapi.controller;

import init.frontendapi.dto.DocumentRequest;
import init.frontendapi.service.DocumentParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/document")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentParsingService documentParsingService;

    @PostMapping("/parse")
    public ResponseEntity<?> parseDocument(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 비어있습니다.");
            }

            String result = documentParsingService.parseDocument(file);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "문서 파싱 완료",
                "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "문서 파싱 실패: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeDocument(@RequestBody DocumentRequest request) {
        try {
            String analysis = documentParsingService.analyzeDocument(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "문서 분석 완료",
                "data", analysis
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "문서 분석 실패: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getDocumentHistory(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        try {
            var history = documentParsingService.getDocumentHistory(page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "히스토리 조회 실패: " + e.getMessage()
                ));
        }
    }
}