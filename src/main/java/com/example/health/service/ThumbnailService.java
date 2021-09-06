package com.example.health.service;

import com.example.health.exception.NoVideosFoundException;
import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Objects;

@Service
public class ThumbnailService {

    private final ThumbnailRepository thumbnailRepository;
    private final VideoRepository videoRepository;

    public ThumbnailService(ThumbnailRepository thumbnailRepository, VideoRepository videoRepository) {
        this.thumbnailRepository = thumbnailRepository;
        this.videoRepository = videoRepository;
    }

    public byte[] findThumbnailByVideoIdAndSize(Long videoId, String size) throws FileNotFoundException {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (Objects.isNull(video)) {
            throw new NoVideosFoundException("Video was not found with id: "+ videoId);
        }
        Thumbnail thumbnail = thumbnailRepository.findThumbnailByVideoAndSize(video, String.format("%sX%s", size, size));
        if (Objects.isNull(thumbnail)) {
            throw new FileNotFoundException(String.format("Thumbnail with videoId: %d and size: %s was not found!", videoId, size));
        }
        return thumbnail.getImage();
    }
}
