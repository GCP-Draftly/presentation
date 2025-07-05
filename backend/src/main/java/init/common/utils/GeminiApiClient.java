package init.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Component
public class GeminiApiClient {
	//cursor
	public String generateText(String prompt) { return "AI 응답"; }

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 슬라이드 컨텐츠 생성
     */
    public String generateSlideContent(String topic, Integer slideCount, String description) throws Exception {
        String prompt = buildSlideContentPrompt(topic, slideCount, description);
        return callGeminiApi(prompt);
    }

    /**
     * 슬라이드 아웃라인만 생성
     */
    public String generateSlideOutline(String topic, Integer slideCount, String description) throws Exception {
        String prompt = buildSlideOutlinePrompt(topic, slideCount, description);
        return callGeminiApi(prompt);
    }

    /**
     * 프롬프트 개선 및 생성
     */
    public String generateImprovedPrompt(String originalPrompt, String context) throws Exception {
        String prompt = buildPromptImprovementPrompt(originalPrompt, context);
        return callGeminiApi(prompt);
    }

    /**
     * 문서 기반 프레젠테이션 생성
     */
    public String generateFromDocument(String documentContent, String topic, Integer slideCount) throws Exception {
        String prompt = buildDocumentBasedPrompt(documentContent, topic, slideCount);
        return callGeminiApi(prompt);
    }

    /**
     * Gemini API 호출
     */
    private String callGeminiApi(String prompt) throws Exception {
        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            
            // contents 배열 생성
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);

            // 생성 설정
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 8192);
            requestBody.put("generationConfig", generationConfig);

            // 안전 설정
            List<Map<String, Object>> safetySettings = new ArrayList<>();
            requestBody.put("safetySettings", safetySettings);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "?key=" + apiKey,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return extractContentFromResponse(response.getBody());
            } else {
                throw new Exception("Gemini API 호출 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new Exception("Gemini API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 응답에서 컨텐츠 추출
     */
    private String extractContentFromResponse(String responseBody) throws Exception {
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
            
            throw new Exception("응답에서 컨텐츠를 찾을 수 없습니다.");
            
        } catch (Exception e) {
            throw new Exception("응답 파싱 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 슬라이드 컨텐츠 생성 프롬프트 구성
     */
    private String buildSlideContentPrompt(String topic, Integer slideCount, String description) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 조건에 맞는 프레젠테이션을 생성해주세요:\n\n");
        prompt.append("주제: ").append(topic).append("\n");
        prompt.append("슬라이드 개수: ").append(slideCount).append("개\n");
        
        if (description != null && !description.trim().isEmpty()) {
            prompt.append("설명: ").append(description).append("\n");
        }
        
        prompt.append("\n각 슬라이드는 다음 형식으로 생성해주세요:\n");
        prompt.append("===== 슬라이드 [번호] =====\n");
        prompt.append("제목: [슬라이드 제목]\n");
        prompt.append("내용:\n");
        prompt.append("- [주요 포인트 1]\n");
        prompt.append("- [주요 포인트 2]\n");
        prompt.append("- [주요 포인트 3]\n");
        prompt.append("노트: [발표자 노트]\n\n");
        
        prompt.append("전체적으로 논리적이고 체계적인 구성으로 만들어주세요.");
        
        return prompt.toString();
    }

    /**
     * 슬라이드 아웃라인 생성 프롬프트 구성
     */
    private String buildSlideOutlinePrompt(String topic, Integer slideCount, String description) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 조건에 맞는 프레젠테이션 아웃라인을 생성해주세요:\n\n");
        prompt.append("주제: ").append(topic).append("\n");
        prompt.append("슬라이드 개수: ").append(slideCount).append("개\n");
        
        if (description != null && !description.trim().isEmpty()) {
            prompt.append("설명: ").append(description).append("\n");
        }
        
        prompt.append("\n각 슬라이드의 제목과 주요 내용만 간단히 나열해주세요:\n");
        prompt.append("1. [슬라이드 1 제목] - [간단한 설명]\n");
        prompt.append("2. [슬라이드 2 제목] - [간단한 설명]\n");
        prompt.append("...\n");
        
        return prompt.toString();
    }

    /**
     * 프롬프트 개선 프롬프트 구성
     */
    private String buildPromptImprovementPrompt(String originalPrompt, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 프롬프트를 더 효과적이고 구체적으로 개선해주세요:\n\n");
        prompt.append("원본 프롬프트: ").append(originalPrompt).append("\n");
        
        if (context != null && !context.trim().isEmpty()) {
            prompt.append("맥락: ").append(context).append("\n");
        }
        
        prompt.append("\n개선된 프롬프트를 제공해주세요. 더 구체적이고 명확하며 실행 가능한 형태로 만들어주세요.");
        
        return prompt.toString();
    }

    /**
     * 문서 기반 프레젠테이션 생성 프롬프트 구성
     */
    private String buildDocumentBasedPrompt(String documentContent, String topic, Integer slideCount) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 문서 내용을 바탕으로 프레젠테이션을 생성해주세요:\n\n");
        prompt.append("문서 내용:\n").append(documentContent).append("\n\n");
        prompt.append("주제: ").append(topic).append("\n");
        prompt.append("슬라이드 개수: ").append(slideCount).append("개\n\n");
        
        prompt.append("문서의 핵심 내용을 바탕으로 논리적인 구성의 프레젠테이션을 만들어주세요.\n");
        prompt.append("각 슬라이드는 다음 형식으로 생성해주세요:\n");
        prompt.append("===== 슬라이드 [번호] =====\n");
        prompt.append("제목: [슬라이드 제목]\n");
        prompt.append("내용:\n");
        prompt.append("- [주요 포인트 1]\n");
        prompt.append("- [주요 포인트 2]\n");
        prompt.append("- [주요 포인트 3]\n");
        
        return prompt.toString();
    }
}