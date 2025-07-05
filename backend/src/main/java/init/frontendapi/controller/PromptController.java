package init.frontendapi.controller;

import init.frontendapi.dto.PromptRequest;
import init.frontendapi.service.PromptGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/prompt")
@CrossOrigin(origins = "*")
public class PromptController {

    @Autowired
    private PromptGenerationService promptGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<?> generatePrompt(@RequestBody PromptRequest request) {
        try {
            String prompt = promptGenerationService.generatePrompt(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "프롬프트 생성 완료",
                "data", prompt
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "프롬프트 생성 실패: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/optimize")
    public ResponseEntity<?> optimizePrompt(@RequestBody PromptRequest request) {
        try {
            String optimizedPrompt = promptGenerationService.optimizePrompt(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "프롬프트 최적화 완료",
                "data", optimizedPrompt
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "프롬프트 최적화 실패: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<?> getPromptTemplates(@RequestParam(required = false) String category) {
        try {
            var templates = promptGenerationService.getPromptTemplates(category);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", templates
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "템플릿 조회 실패: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePrompt(@RequestBody PromptRequest request) {
        try {
            boolean isValid = promptGenerationService.validatePrompt(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "valid", isValid,
                "message", isValid ? "유효한 프롬프트입니다." : "프롬프트를 수정해주세요."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "프롬프트 검증 실패: " + e.getMessage()
                ));
        }
    }
}