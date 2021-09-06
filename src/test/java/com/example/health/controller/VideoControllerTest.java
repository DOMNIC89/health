package com.example.health.controller;

import com.example.health.repository.CategoryRepository;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
class VideoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @LocalServerPort
    private int port;

    private String url;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        url = String.format("http://localhost:%d", port);
    }

    @Test
    public void testUploadOfVideoWithinSizeLimitationShouldReturnVideoDetails() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp4", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 1}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                .file(multipartFile)
                .file(videoRequestCommand))
                .andExpect(status().is(201))
                .andExpect(content().json("{'title': 'Title 1', 'video_url': '/videos/Title 1_default.mp4', 'thumbnail_url': ['/thumbnail/1/64','/thumbnail/1/128', '/thumbnail/1/256'], 'video_id':1, 'category': {'category_id': 1, 'category_name': 'Exercise'}}"));
    }

    @Test
    public void testUploadOfVideoWithinSizeLimitationButWithNoCategoryShouldReturnErrorResponse() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp4", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 7}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                        .file(multipartFile)
                        .file(videoRequestCommand))
                .andExpect(status().is(404))
                .andExpect(content().json("{'status': 'NOT_FOUND', 'message': 'Category not found with ID: 7'}"));
    }

    @Test
    public void testUploadOfVideoWithinSizeLimitationButWithInvalidFormatShouldReturnErrorResponse() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp3");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp3", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 1}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                        .file(multipartFile)
                        .file(videoRequestCommand))
                .andExpect(status().is(422))
                .andExpect(content().json("{'status': 'UNPROCESSABLE_ENTITY', 'message': 'The mp3 file format is not supported'}"));
    }

    @Test
    public void testUploadOfVideoWithinSizeLimitationWithAnotherFileFormatShouldReturnVideoDetails() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mov");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mov", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 1}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                        .file(multipartFile)
                        .file(videoRequestCommand))
                .andExpect(status().is(201))
                .andExpect(content().json("{'title': 'Title 1', 'video_url': '/videos/Title 1_default.mov', 'thumbnail_url': ['/thumbnail/1/64','/thumbnail/1/128', '/thumbnail/1/256'], 'video_id':1, 'category': {'category_id': 1, 'category_name': 'Exercise'}}"));
    }

    @Test
    public void testUploadOfVideoExceedingSizeLimitationShouldReturnVideoDetails() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:invalid_large_filesize.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "invalid_large_filesize.mp4", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 1}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                        .file(multipartFile)
                        .file(videoRequestCommand))
                .andExpect(status().is(422))
                .andExpect(content().json("{'status':'UNPROCESSABLE_ENTITY','message':'Maximum size allowed in MB is: 1'}"));
    }
}