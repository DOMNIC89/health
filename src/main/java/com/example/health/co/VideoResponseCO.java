package com.example.health.co;

import com.example.health.model.Category;
import com.example.health.model.Video;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoResponseCO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("video_url")
    private String videoUrl;

    @JsonProperty("thumbnail_url")
    private List<String> thumbnail;

    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty
    private CategoryResponseCO category;

    public VideoResponseCO(String title, String videoUrl, List<String> thumbnail, Long videoId, CategoryResponseCO category) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.thumbnail = thumbnail;
        this.videoId = videoId;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public CategoryResponseCO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseCO category) {
        this.category = category;
    }

    public static VideoResponseCO toVideoResponseCO(Video video) {
        String thumbnailUrl = String.format("/thumbnail/%s/", video.getId());
        return new VideoResponseCO(video.getTitle(), video.getVideoUrl(),
                Arrays.asList(thumbnailUrl+"64", thumbnailUrl+"128", thumbnailUrl+"256"), video.getId(),
                CategoryResponseCO.toCategoryResponseCO(video.getCategory()));
    }
}
