package com.example.health.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoListCO {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("videos")
    private List<VideoResponseCO> videos;

    public VideoListCO(Long total, List<VideoResponseCO> videos) {
        this.total = total;
        this.videos = videos;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<VideoResponseCO> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoResponseCO> videos) {
        this.videos = videos;
    }
}
