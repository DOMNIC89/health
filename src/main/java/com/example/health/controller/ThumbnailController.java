package com.example.health.controller;

import com.example.health.service.ThumbnailService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/v1/thumbnails")
public class ThumbnailController {

    private final ThumbnailService thumbnailService;

    public ThumbnailController(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @RequestMapping(value = "/{video_id}/{size}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> findThumbnailForSize(@PathVariable("video_id") Long videoId,
                                                         @PathVariable("size") String size) throws FileNotFoundException {
        byte[] thumbnailInBytes = thumbnailService.findThumbnailByVideoIdAndSize(videoId, size);
        ByteArrayResource baos = new ByteArrayResource(thumbnailInBytes);
        return ResponseEntity.status(HttpStatus.OK).contentLength(baos.contentLength()).body(baos);
    }
}
