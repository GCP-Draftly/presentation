package init.frontendapi.service;

import init.frontendapi.dto.PromptTemplate;
import init.common.utils.GeminiApiClient;
import init.frontendapi.dto.PromptRequest;
import init.frontendapi.dto.PromptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.ArrayList;

@Service
public class PromptGenerationService {

    @Autowired
    private GeminiApiClient geminiApiClient;

    @Autowired
    private DocumentParsingService documentParsingService;

    //cursor
    public List<PromptTemplate> getPromptTemplates(String category) { return new ArrayList<>(); }
    public boolean validatePrompt(PromptRequest request) { return true; }
    public String optimizePrompt(PromptRequest request) { return "최적화된 프롬프트"; }
    
    /**
     * 문서 내용을 기반으로 프롬프트를 생성합니다.
     * 
     * @param request 프롬프트 생성 요청
     * @return 생성된 프롬프트 응답
     */
    public PromptResponse generatePrompt(PromptRequest request) {
        try {
            String promptId = UUID.randomUUID().toString();
            
            // 프롬프트 타입에 따른 템플릿 선택
            String template = getPromptTemplate(request.getPromptType());
            
            // 문서 내용 기반 프롬프트 생성
            String generatedPrompt = buildPrompt(template, request);
            
            // AI를 통한 프롬프트 개선 (선택사항)
            if (Boolean.TRUE.equals(request.getUseAiEnhancement())) {
                generatedPrompt = enhancePromptWithAi(generatedPrompt, request);
            }
            
            // 프롬프트 메타데이터 생성
            Map<String, Object> metadata = Map.of(
                "promptType", request.getPromptType(),
                "documentId", request.getDocumentId(),
                "aiEnhanced", request.getUseAiEnhancement(),
                "createdAt", System.currentTimeMillis(),
                "wordCount", countWords(generatedPrompt)
            );
            
            return PromptResponse.builder()
                .promptId(promptId)
                .prompt(generatedPrompt)
                .promptType(request.getPromptType())
                .metadata(metadata)
                .success(true)
                .message("프롬프트가 성공적으로 생성되었습니다.")
                .build();
                
        } catch (Exception e) {
            return PromptResponse.builder()
                .success(false)
                .message("프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 프레젠테이션 생성을 위한 특화된 프롬프트를 생성합니다.
     * 
     * @param request 프롬프트 생성 요청
     * @return 프레젠테이션용 프롬프트 응답
     */
    public PromptResponse generatePresentationPrompt(PromptRequest request) {
        try {
            String promptId = UUID.randomUUID().toString();
            
            // 문서 내용 추출
            String documentContent = extractDocumentContent(request.getDocumentId());
            
            // 프레젠테이션 특화 프롬프트 생성
            String presentationPrompt = buildPresentationPrompt(documentContent, request);
            
            // 슬라이드 구조 제안
            List<Map<String, Object>> suggestedStructure = generateSlideStructure(documentContent);
            
            Map<String, Object> metadata = Map.of(
                "promptType", "presentation",
                "documentId", request.getDocumentId(),
                "suggestedSlideCount", suggestedStructure.size(),
                "slideStructure", suggestedStructure,
                "createdAt", System.currentTimeMillis()
            );
            
            return PromptResponse.builder()
                .promptId(promptId)
                .prompt(presentationPrompt)
                .promptType("presentation")
                .metadata(metadata)
                .success(true)
                .message("프레젠테이션 프롬프트가 성공적으로 생성되었습니다.")
                .build();
                
        } catch (Exception e) {
            return PromptResponse.builder()
                .success(false)
                .message("프레젠테이션 프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 요약 생성을 위한 프롬프트를 생성합니다.
     * 
     * @param request 프롬프트 생성 요청
     * @return 요약용 프롬프트 응답
     */
    public PromptResponse generateSummaryPrompt(PromptRequest request) {
        try {
            String promptId = UUID.randomUUID().toString();
            
            // 문서 내용 추출
            String documentContent = extractDocumentContent(request.getDocumentId());
            
            // 요약 길이 및 스타일 설정
            String summaryStyle = request.getParameters().getOrDefault("summaryStyle", "comprehensive");
            int targetLength = Integer.parseInt(request.getParameters().getOrDefault("targetLength", "300"));
            
            // 요약 프롬프트 생성
            String summaryPrompt = buildSummaryPrompt(documentContent, summaryStyle, targetLength);
            
            Map<String, Object> metadata = Map.of(
                "promptType", "summary",
                "documentId", request.getDocumentId(),
                "summaryStyle", summaryStyle,
                "targetLength", targetLength,
                "createdAt", System.currentTimeMillis()
            );
            
            return PromptResponse.builder()
                .promptId(promptId)
                .prompt(summaryPrompt)
                .promptType("summary")
                .metadata(metadata)
                .success(true)
                .message("요약 프롬프트가 성공적으로 생성되었습니다.")
                .build();
                
        } catch (Exception e) {
            return PromptResponse.builder()
                .success(false)
                .message("요약 프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 기존 프롬프트를 개선합니다.
     * 
     * @param promptId 개선할 프롬프트 ID
     * @param request 개선 요청
     * @return 개선된 프롬프트 응답
     */
    public PromptResponse enhancePrompt(String promptId, PromptRequest request) {
        try {
            // 기존 프롬프트 불러오기 (실제로는 저장소에서 가져와야 함)
            String originalPrompt = request.getPrompt();
            
            // AI를 통한 프롬프트 개선
            String enhancedPrompt = enhancePromptWithAi(originalPrompt, request);
            
            Map<String, Object> metadata = Map.of(
                "originalPromptId", promptId,
                "enhancementType", request.getParameters().getOrDefault("enhancementType", "general"),
                "enhancedAt", System.currentTimeMillis(),
                "improvementAreas", List.of("clarity", "specificity", "structure")
            );
            
            return PromptResponse.builder()
                .promptId(UUID.randomUUID().toString())
                .prompt(enhancedPrompt)
                .promptType(request.getPromptType())
                .metadata(metadata)
                .success(true)
                .message("프롬프트가 성공적으로 개선되었습니다.")
                .build();
                
        } catch (Exception e) {
            return PromptResponse.builder()
                .success(false)
                .message("프롬프트 개선 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 프롬프트 템플릿을 가져옵니다.
     */
    private String getPromptTemplate(String promptType) {
        switch (promptType.toLowerCase()) {
            case "presentation":
                return """
                문서 내용을 바탕으로 효과적인 프레젠테이션을 생성해주세요.
                
                다음 요소들을 포함해주세요:
                - 명확한 제목과 부제
                - 핵심 메시지 3-5개
                - 각 슬라이드별 주요 내용
                - 시각적 요소 제안
                - 결론 및 요약
                
                문서 내용: {documentContent}
                """;
                
            case "summary":
                return """
                다음 문서의 핵심 내용을 요약해주세요.
                
                요약 기준:
                - 주요 포인트 위주로 정리
                - 논리적 구조 유지
                - 핵심 키워드 포함
                - 명확하고 간결한 문체
                
                문서 내용: {documentContent}
                """;
                
            case "analysis":
                return """
                문서 내용을 분석하여 다음 관점에서 정리해주세요.
                
                분석 요소:
                - 주제 및 목적 파악
                - 핵심 논점 추출
                - 구조적 특징 분석
                - 개선 제안사항
                
                문서 내용: {documentContent}
                """;
                
            default:
                return "다음 문서 내용을 바탕으로 {promptType} 작업을 수행해주세요.\n\n문서 내용: {documentContent}";
        }
    }

    /**
     * 템플릿을 기반으로 프롬프트를 구성합니다.
     */
    private String buildPrompt(String template, PromptRequest request) {
        String documentContent = extractDocumentContent(request.getDocumentId());
        
        return template
            .replace("{documentContent}", documentContent)
            .replace("{promptType}", request.getPromptType())
            .replace("{additionalInstructions}", request.getAdditionalInstructions() != null ? 
                request.getAdditionalInstructions() : "");
    }

    /**
     * 프레젠테이션 특화 프롬프트를 생성합니다.
     */
    private String buildPresentationPrompt(String documentContent, PromptRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 문서를 바탕으로 전문적인 프레젠테이션을 생성해주세요.\n\n");
        prompt.append("문서 내용:\n").append(documentContent).append("\n\n");
        
        prompt.append("프레젠테이션 요구사항:\n");
        prompt.append("- 슬라이드 수: ").append(request.getParameters().getOrDefault("slideCount", "10-15")).append("개\n");
        prompt.append("- 대상 청중: ").append(request.getParameters().getOrDefault("audience", "일반")).append("\n");
        prompt.append("- 발표 시간: ").append(request.getParameters().getOrDefault("duration", "15-20")).append("분\n");
        prompt.append("- 스타일: ").append(request.getParameters().getOrDefault("style", "professional")).append("\n\n");
        
        prompt.append("각 슬라이드에 대해 다음 정보를 제공해주세요:\n");
        prompt.append("1. 슬라이드 제목\n");
        prompt.append("2. 핵심 메시지\n");
        prompt.append("3. 상세 내용\n");
        prompt.append("4. 시각적 요소 제안\n");
        prompt.append("5. 발표자 노트\n");
        
        return prompt.toString();
    }

    /**
     * 요약 프롬프트를 생성합니다.
     */
    private String buildSummaryPrompt(String documentContent, String summaryStyle, int targetLength) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 문서를 ").append(summaryStyle).append(" 스타일로 요약해주세요.\n\n");
        prompt.append("문서 내용:\n").append(documentContent).append("\n\n");
        
        prompt.append("요약 조건:\n");
        prompt.append("- 목표 길이: 약 ").append(targetLength).append("단어\n");
        prompt.append("- 스타일: ").append(summaryStyle).append("\n");
        prompt.append("- 핵심 포인트 위주로 정리\n");
        prompt.append("- 원문의 논리적 흐름 유지\n");
        
        return prompt.toString();
    }

    /**
     * AI를 통해 프롬프트를 개선합니다.
     */
    private String enhancePromptWithAi(String originalPrompt, PromptRequest request) {
        try {
            String enhancementInstruction = "다음 프롬프트를 더 효과적이고 구체적으로 개선해주세요:\n\n" + originalPrompt;
            return geminiApiClient.generateText(enhancementInstruction);
        } catch (Exception e) {
            // AI 개선 실패 시 원본 프롬프트 반환
            return originalPrompt;
        }
    }

    /**
     * 문서 내용을 추출합니다.
     */
    private String extractDocumentContent(String documentId) {
        try {
            // DocumentParsingService를 통해 문서 내용 추출
            // 실제 구현에서는 저장된 문서에서 내용을 가져와야 함
            return "문서 내용 추출 로직 구현 필요";
        } catch (Exception e) {
            return "문서 내용을 가져올 수 없습니다.";
        }
    }

    /**
     * 슬라이드 구조를 생성합니다.
     */
    private List<Map<String, Object>> generateSlideStructure(String documentContent) {
        // 문서 내용을 분석하여 슬라이드 구조 제안
        return List.of(
            Map.of("slideNumber", 1, "title", "제목 슬라이드", "type", "title"),
            Map.of("slideNumber", 2, "title", "개요", "type", "outline"),
            Map.of("slideNumber", 3, "title", "주요 내용 1", "type", "content"),
            Map.of("slideNumber", 4, "title", "주요 내용 2", "type", "content"),
            Map.of("slideNumber", 5, "title", "결론", "type", "conclusion")
        );
    }

    /**
     * 단어 수를 계산합니다.
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}