package init.common.utils;

import init.backendapi.dto.SlideOutline;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SlideParser {

    // 슬라이드 구분 패턴
    private static final Pattern SLIDE_PATTERN = Pattern.compile("=====\\s*슬라이드\\s*(\\d+)\\s*=====");
    private static final Pattern TITLE_PATTERN = Pattern.compile("제목:\\s*(.+)");
    private static final Pattern CONTENT_PATTERN = Pattern.compile("내용:\\s*([\\s\\S]*?)(?=노트:|$)");
    private static final Pattern NOTES_PATTERN = Pattern.compile("노트:\\s*(.+)");
    private static final Pattern BULLET_PATTERN = Pattern.compile("^\\s*[-*•]\\s*(.+)$", Pattern.MULTILINE);

    /**
     * Gemini 응답을 SlideOutline 리스트로 파싱
     */
    public List<SlideOutline> parseSlideStructure(String geminiResponse) throws Exception {
        List<SlideOutline> slideOutlines = new ArrayList<>();
        
        try {
            // 슬라이드별로 분할
            String[] slides = geminiResponse.split("=====\\s*슬라이드\\s*\\d+\\s*=====");
            
            for (int i = 1; i < slides.length; i++) { // 첫 번째는 비어있으므로 건너뛰기
                String slideContent = slides[i].trim();
                SlideOutline outline = parseIndividualSlide(slideContent, i);
                if (outline != null) {
                    slideOutlines.add(outline);
                }
            }
            
            // 파싱 결과가 없으면 대체 파싱 시도
            if (slideOutlines.isEmpty()) {
                slideOutlines = parseAlternativeFormat(geminiResponse);
            }
            
        } catch (Exception e) {
            throw new Exception("슬라이드 구조 파싱 중 오류 발생: " + e.getMessage(), e);
        }
        
        return slideOutlines;
    }

    /**
     * 개별 슬라이드 파싱
     */
    private SlideOutline parseIndividualSlide(String slideContent, int slideNumber) {
        try {
            SlideOutline outline = new SlideOutline();
            outline.setSlideNumber(slideNumber);
            
            // 제목 추출
            Matcher titleMatcher = TITLE_PATTERN.matcher(slideContent);
            if (titleMatcher.find()) {
                outline.setTitle(titleMatcher.group(1).trim());
            }
            
            // 내용 추출
            Matcher contentMatcher = CONTENT_PATTERN.matcher(slideContent);
            if (contentMatcher.find()) {
                String content = contentMatcher.group(1).trim();
                outline.setContent(content);
                
                // 불릿 포인트 추출
                List<String> bulletPoints = extractBulletPoints(content);
                outline.setBulletPoints(bulletPoints);
            }
            
            // 노트 추출
            Matcher notesMatcher = NOTES_PATTERN.matcher(slideContent);
            if (notesMatcher.find()) {
                outline.setNotes(notesMatcher.group(1).trim());
            }
            
            // 슬라이드 타입 추론
            outline.setSlideType(inferSlideType(outline.getTitle(), outline.getContent()));
            
            return outline;
            
        } catch (Exception e) {
            System.err.println("개별 슬라이드 파싱 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 대체 포맷 파싱 (숫자 리스트 형태)
     */
    private List<SlideOutline> parseAlternativeFormat(String geminiResponse) {
        List<SlideOutline> slideOutlines = new ArrayList<>();
        
        try {
            // 숫자 리스트 패턴으로 파싱
            Pattern listPattern = Pattern.compile("(\\d+)\\s*\\.\\s*(.+?)\\s*-\\s*(.+?)(?=\\n\\d+\\.|$)", Pattern.DOTALL);
            Matcher listMatcher = listPattern.matcher(geminiResponse);
            
            while (listMatcher.find()) {
                int slideNumber = Integer.parseInt(listMatcher.group(1));
                String title = listMatcher.group(2).trim();
                String description = listMatcher.group(3).trim();
                
                SlideOutline outline = new SlideOutline();
                outline.setSlideNumber(slideNumber);
                outline.setTitle(title);
                outline.setContent(description);
                outline.setSlideType("standard");
                
                slideOutlines.add(outline);
            }
            
        } catch (Exception e) {
            System.err.println("대체 포맷 파싱 오류: " + e.getMessage());
        }
        
        return slideOutlines;
    }

    /**
     * 불릿 포인트 추출
     */
    private List<String> extractBulletPoints(String content) {
        List<String> bulletPoints = new ArrayList<>();
        
        Matcher bulletMatcher = BULLET_PATTERN.matcher(content);
        while (bulletMatcher.find()) {
            bulletPoints.add(bulletMatcher.group(1).trim());
        }
        
        return bulletPoints;
    }

    /**
     * 슬라이드 타입 추론
     */
    private String inferSlideType(String title, String content) {
        if (title == null && content == null) {
            return "standard";
        }
        
        String text = (title + " " + content).toLowerCase();
        
        if (text.contains("소개") || text.contains("시작") || text.contains("welcome")) {
            return "title";
        } else if (text.contains("목차") || text.contains("agenda") || text.contains("outline")) {
            return "agenda";
        } else if (text.contains("결론") || text.contains("마무리") || text.contains("conclusion")) {
            return "conclusion";
        } else if (text.contains("감사") || text.contains("질문") || text.contains("thank")) {
            return "closing";
        } else {
            return "standard";
        }
    }

    /**
     * 슬라이드 내용 정리
     */
    public String cleanSlideContent(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 불필요한 공백 및 특수문자 제거
        content = content.replaceAll("\\s+", " ").trim();
        
        // HTML 태그 제거 (만약 있다면)
        content = content.replaceAll("<[^>]*>", "");
        
        return content;
    }

    /**
     * 슬라이드 유효성 검증
     */
    public boolean validateSlideOutline(SlideOutline outline) {
        if (outline == null) {
            return false;
        }
        
        // 제목이 있거나 내용이 있어야 함
        boolean hasTitle = outline.getTitle() != null && !outline.getTitle().trim().isEmpty();
        boolean hasContent = outline.getContent() != null && !outline.getContent().trim().isEmpty();
        
        return hasTitle || hasContent;
    }

    /**
     * 슬라이드 아웃라인 요약
     */
    public String summarizeSlideOutlines(List<SlideOutline> outlines) {
        if (outlines == null || outlines.isEmpty()) {
            return "슬라이드 아웃라인이 없습니다.";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("총 ").append(outlines.size()).append("개의 슬라이드:\n\n");
        
        for (SlideOutline outline : outlines) {
            summary.append(outline.getSlideNumber()).append(". ");
            summary.append(outline.getTitle() != null ? outline.getTitle() : "제목 없음");
            summary.append("\n");
        }
        
        return summary.toString();
    }
}