package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentHistory {

    private String documentId;
    private String fileName;
    private String filePath;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
    private boolean success;

}