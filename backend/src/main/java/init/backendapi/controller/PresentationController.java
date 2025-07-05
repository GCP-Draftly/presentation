package init.backendapi.controller;

import init.backendapi.dto.PresentationRequest;
import init.backendapi.dto.SlideOutline;
import init.backendapi.service.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backend/presentation")
@CrossOrigin(origins = "*")
public class PresentationController {

    @Autowired
    private PresentationService presentationService;

    @PostMapping("/generate")
    public ResponseEntity<?> generatePresentation(@RequestBody PresentationRequest request) {
        try {
            String pptFilePath = presentationService.generatePresentation(request);
            return ResponseEntity.ok().body(new ApiResponse(true, "프레젠테이션이 성공적으로 생성되었습니다.", pptFilePath));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "프레젠테이션 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    @PostMapping("/outline")
    public ResponseEntity<?> generateOutline(@RequestBody PresentationRequest request) {
        try {
            List<SlideOutline> outline = presentationService.generateSlideOutline(request);
            return ResponseEntity.ok().body(new ApiResponse(true, "슬라이드 아웃라인이 생성되었습니다.", outline));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "아웃라인 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    @GetMapping("/status/{presentationId}")
    public ResponseEntity<?> getPresentationStatus(@PathVariable String presentationId) {
        try {
            String status = presentationService.getPresentationStatus(presentationId);
            return ResponseEntity.ok().body(new ApiResponse(true, "상태 조회 성공", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "프레젠테이션을 찾을 수 없습니다: " + e.getMessage(), null));
        }
    }

    // 내부 응답 클래스
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}