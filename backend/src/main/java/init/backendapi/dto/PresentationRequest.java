package init.backendapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class PresentationRequest {

    @NotBlank(message = "주제는 필수입니다.")
    private String topic;

    @NotNull(message = "슬라이드 개수는 필수입니다.")
    @Min(value = 1, message = "슬라이드 개수는 최소 1개 이상이어야 합니다.")
    @Max(value = 50, message = "슬라이드 개수는 최대 50개까지 가능합니다.")
    private Integer slideCount;

    private String description;

    private String targetAudience;

    private String presentationStyle;

    private String language;

    // 생성자
    public PresentationRequest() {}

    public PresentationRequest(String topic, Integer slideCount, String description) {
        this.topic = topic;
        this.slideCount = slideCount;
        this.description = description;
    }

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getSlideCount() {
        return slideCount;
    }

    public void setSlideCount(Integer slideCount) {
        this.slideCount = slideCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getPresentationStyle() {
        return presentationStyle;
    }

    public void setPresentationStyle(String presentationStyle) {
        this.presentationStyle = presentationStyle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "PresentationRequest{" +
                "topic='" + topic + '\'' +
                ", slideCount=" + slideCount +
                ", description='" + description + '\'' +
                ", targetAudience='" + targetAudience + '\'' +
                ", presentationStyle='" + presentationStyle + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}