package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    
	//Cursor
	// DocumentRequest.java (클래스 내)
	private String documentId;
	// getter/setter도 추가
	public String getDocumentId() { return documentId; }
	public void setDocumentId(String documentId) { this.documentId = documentId; }
	
    @NotBlank(message = "문서 내용은 필수입니다.")
    private String content;
    @NotBlank(message = "문서 제목은 필수입니다.")
    private String title;
    private String documentType; // PDF, DOCX, TXT, etc.
    private String language; // ko, en, etc.
    private String analysisType; // SUMMARY, KEYWORD, STRUCTURE, etc.
    private Boolean extractImages;
    private Boolean extractTables;
    private String userId;
    private String sessionId;
    private LocalDateTime createdAt;
    private String fileUrl;
    private Long fileSize;
    private String fileName;
    
    // 추가 메타데이터
    private String encoding;
    private Integer pageCount;
    private String author;
    private String subject;
    private String keywords;
}