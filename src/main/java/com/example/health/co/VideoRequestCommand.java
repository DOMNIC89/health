package com.example.health.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoRequestCommand {

    @JsonProperty("title")
    private String title;

    @JsonProperty("category_id")
    private Long categoryId;
    
    private MultipartFile videoFile;

    public VideoRequestCommand(String title, Long categoryId) {
        this.title = title;
        this.categoryId = categoryId;
    }

    public VideoRequestCommand() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public MultipartFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }
}
