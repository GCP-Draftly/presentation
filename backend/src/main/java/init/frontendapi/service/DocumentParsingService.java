package init.frontendapi.service;

import init.frontendapi.dto.DocumentHistory;
import init.common.utils.FileStorageUtil;
import init.frontendapi.dto.DocumentRequest;
import init.frontendapi.dto.DocumentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.util.ArrayList;

@Service
public class DocumentParsingService {

    @Autowired
    private FileStorageUtil fileStorageUtil;
    
    
    //Cursor
    public String analyzeDocument(DocumentRequest request) {
        // 실제 분석 로직 구현
        return "분석 결과";
    }

    public List<DocumentHistory> getDocumentHistory(int page, int size) {
        // 실제 히스토리 조회 로직 구현
        return new ArrayList<>();
    }

    /**
     * 문서 파일을 파싱하여 텍스트 내용을 추출합니다.
     * 
     * @param file 업로드된 문서 파일
     * @return 파싱된 문서 응답
     */
    public DocumentResponse parseDocument(MultipartFile file) {
        try {
            String documentId = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(fileName);
            
            // 파일 저장
            String filePath = fileStorageUtil.saveFile(file, documentId);
            
            // 파일 형식에 따른 텍스트 추출
            String extractedText = extractTextFromFile(file, fileExtension);
            
            // 문서 메타데이터 생성
            Map<String, Object> metadata = Map.of(
                "fileName", fileName,
                "fileSize", file.getSize(),
                "fileType", fileExtension,
                "uploadTime", System.currentTimeMillis(),
                "wordCount", countWords(extractedText)
            );
            
            return DocumentResponse.builder()
                .documentId(documentId)
                .fileName(fileName)
                .content(extractedText)
                .metadata(metadata)
                .success(true)
                .message("문서 파싱이 성공적으로 완료되었습니다.")
                .build();
                
        } catch (Exception e) {
            return DocumentResponse.builder()
                .success(false)
                .message("문서 파싱 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 저장된 문서를 다시 파싱합니다.
     * 
     * @param request 문서 재파싱 요청
     * @return 파싱된 문서 응답
     */
    public DocumentResponse reparseDocument(DocumentRequest request) {
        try {
            // 저장된 파일 불러오기
            byte[] fileData = fileStorageUtil.loadFile(request.getDocumentId());
            String fileName = request.getFileName();
            String fileExtension = getFileExtension(fileName);
            
            // 텍스트 추출
            String extractedText = extractTextFromBytes(fileData, fileExtension);
            
            // 업데이트된 메타데이터 생성
            Map<String, Object> metadata = Map.of(
                "fileName", fileName,
                "fileSize", fileData.length,
                "fileType", fileExtension,
                "lastParsed", System.currentTimeMillis(),
                "wordCount", countWords(extractedText)
            );
            
            return DocumentResponse.builder()
                .documentId(request.getDocumentId())
                .fileName(fileName)
                .content(extractedText)
                .metadata(metadata)
                .success(true)
                .message("문서 재파싱이 성공적으로 완료되었습니다.")
                .build();
                
        } catch (Exception e) {
            return DocumentResponse.builder()
                .success(false)
                .message("문서 재파싱 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 문서 내용을 구조화된 섹션으로 분할합니다.
     * 
     * @param documentId 문서 ID
     * @return 섹션별로 분할된 문서 응답
     */
    public DocumentResponse parseDocumentStructure(String documentId) {
        try {
            byte[] fileData = fileStorageUtil.loadFile(documentId);
            String content = new String(fileData);
            
            // 문서 구조 분석
            List<Map<String, Object>> sections = analyzeDocumentStructure(content);
            
            Map<String, Object> structuredData = Map.of(
                "sections", sections,
                "totalSections", sections.size(),
                "structureType", detectStructureType(content)
            );
            
            return DocumentResponse.builder()
                .documentId(documentId)
                .content(content)
                .metadata(structuredData)
                .success(true)
                .message("문서 구조 분석이 완료되었습니다.")
                .build();
                
        } catch (Exception e) {
            return DocumentResponse.builder()
                .success(false)
                .message("문서 구조 분석 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }

    /**
     * 파일 확장자를 추출합니다.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 파일로부터 텍스트를 추출합니다.
     */
    private String extractTextFromFile(MultipartFile file, String extension) throws IOException {
        byte[] fileData = file.getBytes();
        return extractTextFromBytes(fileData, extension);
    }

    /**
     * 바이트 배열로부터 텍스트를 추출합니다.
     */
    private String extractTextFromBytes(byte[] fileData, String extension) {
        switch (extension) {
            case "txt":
                return new String(fileData);
            case "pdf":
                return extractTextFromPdf(fileData);
            case "docx":
                return extractTextFromDocx(fileData);
            case "pptx":
                return extractTextFromPptx(fileData);
            default:
                return new String(fileData);
        }
    }

    /**
     * PDF 파일에서 텍스트를 추출합니다.
     */
    private String extractTextFromPdf(byte[] fileData) {
        // PDF 파싱 로직 구현
        // Apache PDFBox 또는 iText 사용
        return "PDF 텍스트 추출 구현 필요";
    }

    /**
     * DOCX 파일에서 텍스트를 추출합니다.
     */
    private String extractTextFromDocx(byte[] fileData) {
        // DOCX 파싱 로직 구현
        // Apache POI 사용
        return "DOCX 텍스트 추출 구현 필요";
    }

    /**
     * PPTX 파일에서 텍스트를 추출합니다.
     */
    private String extractTextFromPptx(byte[] fileData) {
        // PPTX 파싱 로직 구현
        // Apache POI 사용
        return "PPTX 텍스트 추출 구현 필요";
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

    /**
     * 문서 구조를 분석합니다.
     */
    private List<Map<String, Object>> analyzeDocumentStructure(String content) {
        // 문서 구조 분석 로직
        // 제목, 섹션, 문단 등으로 분할
        return List.of(
            Map.of("type", "header", "level", 1, "content", "제목"),
            Map.of("type", "paragraph", "content", "본문 내용"),
            Map.of("type", "list", "items", List.of("항목1", "항목2"))
        );
    }

    /**
     * 문서 구조 유형을 감지합니다.
     */
    private String detectStructureType(String content) {
        // 문서 유형 감지 로직
        if (content.contains("# ") || content.contains("## ")) {
            return "markdown";
        } else if (content.contains("<h1>") || content.contains("<h2>")) {
            return "html";
        } else {
            return "plain";
        }
    }
}