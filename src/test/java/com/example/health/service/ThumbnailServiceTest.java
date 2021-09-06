package com.example.health.service;

import com.example.health.exception.NoVideosFoundException;
import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ThumbnailServiceTest {

    private VideoRepository videoRepository;
    private ThumbnailRepository thumbnailRepository;
    private ThumbnailService thumbnailService;

    @BeforeEach
    public void setup() {
        videoRepository = Mockito.mock(VideoRepository.class);
        thumbnailRepository = Mockito.mock(ThumbnailRepository.class);
        thumbnailService = new ThumbnailService(thumbnailRepository, videoRepository);
    }

    @Test
    public void testFindThumbnailByVideoAndSizeWhenPresentShouldReturnByteArray() throws FileNotFoundException {
        Video video = Mockito.mock(Video.class);
        Mockito.when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        Thumbnail thumbnail = new Thumbnail("64X64", "dummytest".getBytes());
        Mockito.when(thumbnailRepository.findThumbnailByVideoAndSize(video,"64X64")).thenReturn(thumbnail);
        byte[] actual = thumbnailService.findThumbnailByVideoIdAndSize(1L, "64");
        assertEquals("dummytest", new String(actual));
    }

    @Test
    public void testFindThumbnailByVideoAndSizeWhenVideoNotPresentShouldThrowNoVideosFoundException() throws FileNotFoundException {
        Mockito.when(videoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoVideosFoundException.class, () -> thumbnailService.findThumbnailByVideoIdAndSize(1L, "64"));
    }

    @Test
    public void testFindThumbnailByVideoAndSizeWhenThumbnailNotFound() {
        Video video = Mockito.mock(Video.class);
        Mockito.when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        Mockito.when(thumbnailRepository.findThumbnailByVideoAndSize(video, "64X64")).thenReturn(null);
        assertThrows(FileNotFoundException.class, () -> thumbnailService.findThumbnailByVideoIdAndSize(1L, "64"));
    }

}