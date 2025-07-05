package init.backendapi.service;

import init.backendapi.dto.PresentationRequest;
import init.backendapi.dto.SlideOutline;
import init.common.utils.GeminiApiClient;
import init.common.utils.FileStorageUtil;
import init.common.utils.SlideParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresentationService {

    @Autowired
    private GeminiApiClient geminiApiClient;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private SlideParser slideParser;

    // 프레젠테이션 상태 관리
    private final ConcurrentHashMap<String, String> presentationStatus = new ConcurrentHashMap<>();

    public String generatePresentation(PresentationRequest request) throws Exception {
        String presentationId = UUID.randomUUID().toString();
        
        try {
            // 상태 업데이트: 시작
            presentationStatus.put(presentationId, "PROCESSING");

            // 1. Gemini API를 통해 슬라이드 컨텐츠 생성
            String geminiResponse = geminiApiClient.generateSlideContent(
                request.getTopic(), 
                request.getSlideCount(), 
                request.getDescription()
            );

            // 2. 슬라이드 구조 파싱
            List<SlideOutline> slideOutlines = slideParser.parseSlideStructure(geminiResponse);

            // 3. PPT 파일 생성
            String pptFilePath = createPowerPointFile(slideOutlines, request.getTopic());

            // 4. 파일 저장
            String savedFilePath = fileStorageUtil.saveFile(pptFilePath, presentationId);

            // 상태 업데이트: 완료
            presentationStatus.put(presentationId, "COMPLETED");

            return savedFilePath;

        } catch (Exception e) {
            // 상태 업데이트: 실패
            presentationStatus.put(presentationId, "FAILED");
            throw new Exception("프레젠테이션 생성 실패: " + e.getMessage(), e);
        }
    }

    public List<SlideOutline> generateSlideOutline(PresentationRequest request) throws Exception {
        try {
            // Gemini API를 통해 아웃라인만 생성
            String geminiResponse = geminiApiClient.generateSlideOutline(
                request.getTopic(), 
                request.getSlideCount(), 
                request.getDescription()
            );

            // 슬라이드 구조 파싱
            return slideParser.parseSlideStructure(geminiResponse);

        } catch (Exception e) {
            throw new Exception("슬라이드 아웃라인 생성 실패: " + e.getMessage(), e);
        }
    }

    public String getPresentationStatus(String presentationId) throws Exception {
        String status = presentationStatus.get(presentationId);
        if (status == null) {
            throw new Exception("존재하지 않는 프레젠테이션 ID입니다.");
        }
        return status;
    }

    private String createPowerPointFile(List<SlideOutline> slideOutlines, String topic) throws Exception {
        // PowerPoint 파일 생성 로직
        // Apache POI 등을 사용하여 실제 PPT 파일 생성
        String fileName = topic.replaceAll("[^a-zA-Z0-9가-힣]", "_") + "_" + System.currentTimeMillis() + ".pptx";
        
        // TODO: 실제 PPT 생성 로직 구현
        // 현재는 임시 파일 경로 반환
        return "/tmp/presentations/" + fileName;
    }

    // 프레젠테이션 삭제
    public void deletePresentationStatus(String presentationId) {
        presentationStatus.remove(presentationId);
    }

    // 모든 프레젠테이션 상태 조회 (관리자용)
    public ConcurrentHashMap<String, String> getAllPresentationStatus() {
        return new ConcurrentHashMap<>(presentationStatus);
    }
}