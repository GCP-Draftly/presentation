package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequest {
	
	// Cursor
    private String documentId;
    private Map<String, Object> parameters;
    private Boolean useAiEnhancement;
    private String additionalInstructions;
    private String prompt;
    
    @NotBlank(message = "프롬프트 내용은 필수입니다.")
    private String content;
    
    @NotBlank(message = "프롬프트 유형은 필수입니다.")
    private String promptType; // PRESENTATION, SUMMARY, ANALYSIS, etc.
    private String targetAudience; // GENERAL, BUSINESS, ACADEMIC, etc.
    private String presentationStyle; // FORMAL, CASUAL, CREATIVE, etc.
    private Integer slideCount;
    private String language; // ko, en, etc.
    private String tone; // PROFESSIONAL, FRIENDLY, PERSUASIVE, etc.
    private List<String> keywords;
    private String documentContext; // 참조할 문서 내용
    private String userId;
    private String sessionId;
    private String templateId;
    private Map<String, Object> customParameters;
    
    // 최적화 관련 옵션
    private Boolean optimizeForClarity;
    private Boolean optimizeForEngagement;
    private Boolean optimizeForLength;
    
    // 검증 관련 옵션
    private Boolean validateGrammar;
    private Boolean validateLogic;
    private Boolean validateCompleteness;
    
    // 생성 옵션
    private Double temperature; // 0.0 ~ 1.0
    private Integer maxTokens;
    private String model; // gemini-pro, gemini-pro-vision, etc.
}
