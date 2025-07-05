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
public class DocumentResponse {
    
    private String documentId;
    private String title;
    private String content;
    private String summary;
    private String documentType;
    private String language;
    private String status; // PROCESSING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    
    // 분석 결과
    private List<String> keywords;
    private Map<String, Object> analysisResult;
    private List<String> mainTopics;
    private String sentiment;
    private Double readabilityScore;
    private Integer wordCount;
    private Integer characterCount;
    
    // 파일 정보
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Integer pageCount;
    
    // 추출된 콘텐츠
    private List<String> extractedImages;
    private List<Map<String, Object>> extractedTables;
    private List<String> extractedLinks;
    
    // 메타데이터
    private String author;
    private String subject;
    private LocalDateTime documentCreatedAt;
    private String encoding;
    private String message;


    public static class DocumentResponseBuilder {
        public DocumentResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
    }
	
}