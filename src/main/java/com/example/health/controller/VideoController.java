package com.example.health.controller;

import com.example.health.co.VideoListCO;
import com.example.health.co.VideoRequestCommand;
import com.example.health.co.VideoResponseCO;
import com.example.health.service.VideoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(path = "/upload",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<VideoResponseCO> uploadVideo(@RequestPart MultipartFile videoFile,
                                         @RequestPart VideoRequestCommand videoRequest) throws Exception {
        VideoResponseCO responseCO = videoService.uploadNewVideoAndThumbnails(videoRequest.getTitle(), videoFile,
                videoRequest.getCategoryId());
        return ResponseEntity.created(new URI("api/v1/videos/"+responseCO.getVideoId())).body(responseCO);
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<VideoListCO> allVideos() {
        VideoListCO videoListCO = videoService.findAllVideos();
        return ResponseEntity.ok(videoListCO);
    }
}
