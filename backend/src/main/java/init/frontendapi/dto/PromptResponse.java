package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponse {
    
    private String promptId;
    private String originalPrompt;
    private String optimizedPrompt;
    private String promptType;
    private String status; // GENERATED, OPTIMIZED, VALIDATED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    
    // 생성 결과
    private String generatedContent;
    private List<String> suggestions;
    private Map<String, Object> analysisResult;
    
    // 검증 결과
    private Boolean isValid;
    private List<String> validationErrors;
    private List<String> validationWarnings;
    private Double qualityScore;
    
    // 최적화 결과
    private String optimizationSummary;
    private List<String> improvements;
    private Double improvementScore;
    
    // 메타데이터
    private String templateId;
    private String templateName;
    private String language;
    private String tone;
    private Integer tokenCount;
    private Double processingTime;
    
    // 모델 정보
    private String model;
    private String modelVersion;
    private Map<String, Object> modelParameters;
    
    private boolean success;
    private String prompt;
    private String message;

    public static class PromptResponseBuilder {
        public PromptResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
    }

  
}