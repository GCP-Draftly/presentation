package init.backendapi.dto;

import java.util.List;

public class SlideOutline {
    
    private int slideNumber;
    private String title;
    private String content;
    private String slideType;
    private List<String> bulletPoints;
    private String imageDescription;
    private String notes;

    // 생성자
    public SlideOutline() {}

    public SlideOutline(int slideNumber, String title, String content) {
        this.slideNumber = slideNumber;
        this.title = title;
        this.content = content;
    }

    public SlideOutline(int slideNumber, String title, String content, String slideType, List<String> bulletPoints) {
        this.slideNumber = slideNumber;
        this.title = title;
        this.content = content;
        this.slideType = slideType;
        this.bulletPoints = bulletPoints;
    }

    // Getters and Setters
    public int getSlideNumber() {
        return slideNumber;
    }

    public void setSlideNumber(int slideNumber) {
        this.slideNumber = slideNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String slideType) {
        this.slideType = slideType;
    }

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "SlideOutline{" +
                "slideNumber=" + slideNumber +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", slideType='" + slideType + '\'' +
                ", bulletPoints=" + bulletPoints +
                ", imageDescription='" + imageDescription + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}