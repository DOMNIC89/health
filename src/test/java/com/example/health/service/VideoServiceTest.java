package com.example.health.service;

import com.example.health.exception.CategoryNotFoundException;
import com.example.health.exception.FileFormatNotSupportedException;
import com.example.health.exception.FileSizeExceedException;
import com.example.health.model.Category;
import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import com.example.health.repository.CategoryRepository;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.io.IOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VideoServiceTest {

    private VideoRepository videoRepository;
    private CategoryRepository categoryRepository;
    private ThumbnailRepository thumbnailRepository;
    private VideoService videoService;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        videoRepository = Mockito.mock(VideoRepository.class);
        categoryRepository = Mockito.mock(CategoryRepository.class);
        thumbnailRepository = Mockito.mock(ThumbnailRepository.class);
        videoService = new VideoService(videoRepository, categoryRepository, thumbnailRepository);
        Field field = VideoService.class.getDeclaredField("fileSizeAllowedInMB");
        field.setAccessible(true);
        field.set(videoService, 1L);
    }

    @Test
    public void testUploadFileToLocalSystemWithASmallFile() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp4", "mp4", inputStream);
        Category category = new Category(1L, "Exercise");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Field field = VideoService.class.getDeclaredField("allowedFileExtensions");
        field.setAccessible(true);
        field.set(videoService, List.of("mp4", "mov"));
        Video video = Mockito.mock(Video.class);
        Mockito.when(videoRepository.save(Mockito.any(Video.class))).thenReturn(video);
        Mockito.when(video.getId()).thenReturn(1L);
        videoService.uploadNewVideoAndThumbnails("Title 1", multipartFile, 1L);
        Mockito.verify(videoRepository).save(Mockito.any(Video.class));
        String filePath = System.getProperty("user.dir");
        File file = new File(filePath + "/videos/Title 1_default.mp4");
        assertTrue(file.exists());
    }

    @Test
    public void testUploadFileToLocalSystemWithNotAllowedFileExtension() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("default.mp4", inputStream);
        Category category = new Category(1L, "Exercise");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Field field = VideoService.class.getDeclaredField("allowedFileExtensions");
        field.setAccessible(true);
        field.set(videoService, List.of("mov"));
        assertThrows(FileFormatNotSupportedException.class,
                ()-> videoService.uploadNewVideoAndThumbnails("Title 1", multipartFile, 1L));
    }

    @Test
    public void testUploadFileToLocalSystemWithInvalidCategory() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp4", "mp4", inputStream);
        Mockito.when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
        Field field = VideoService.class.getDeclaredField("allowedFileExtensions");
        field.setAccessible(true);
        field.set(videoService, List.of("mov","mp4"));
        assertThrows(CategoryNotFoundException.class,
                ()-> videoService.uploadNewVideoAndThumbnails("Title 1", multipartFile, 2L));
    }

    @Test
    public void testUploadFileToLocalSystemWithLargeFileSize() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:invalid_large_filesize.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "invalid_large_filesize.mp4", "mp4", inputStream);
        Field field = VideoService.class.getDeclaredField("allowedFileExtensions");
        field.setAccessible(true);
        field.set(videoService, List.of("mov","mp4"));
        assertThrows(FileSizeExceedException.class,
                ()-> videoService.uploadNewVideoAndThumbnails("Title 1", multipartFile, 1L));
    }

    private Thumbnail createThumbnail(File originalSource, int size, String title, String fileNameWithoutExtension) throws JCodecException, IOException {
        Picture picture = FrameGrab.getFrameAtSec(originalSource, 1.0);
        Picture cropped = Picture.copyPicture(picture);
        cropped.setCrop(new Rect(picture.getStartX(), picture.getStartY(), size, size));
        Path relativeFilePath = Paths.get("./");
        relativeFilePath = relativeFilePath.resolve(title +"_"+ fileNameWithoutExtension +"_"+size+".png");
        File file = relativeFilePath.toFile();
        JCodecUtil.savePictureAsPPM(cropped, file);
        Thumbnail thumbnail = new Thumbnail(String.valueOf(size), IOUtils.readFileToByteArray(file));
        file.delete();
        return thumbnail;
    }
}