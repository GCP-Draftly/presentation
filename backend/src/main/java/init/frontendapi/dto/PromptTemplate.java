package init.frontendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {

    private String title;
    private String description;
    private String templateBody;
    private Map<String, Object> variables;

}