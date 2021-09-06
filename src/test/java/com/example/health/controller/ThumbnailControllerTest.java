package com.example.health.controller;

import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
class ThumbnailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Autowired
    private VideoRepository videoRepository;

    @BeforeEach
    public void setup() {
        url = String.format("http://localhost:%d", port);
    }

    @Test
    public void testThumbnailByVideoIdAndSizeWhenNoVideoFoundShouldReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url+"/api/v1/thumbnails/1/64").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(content().json("{'status': 'NOT_FOUND', 'message': 'Video was not found with id: 1'}"));
    }

    @Test
    public void testThumbnailByVideoIdAndSizeWhenThumbnailNotFoundShouldReturn404() throws Exception {
        loadVideo();
        removeThumbnails();
        mockMvc.perform(MockMvcRequestBuilders.get(url+"/api/v1/thumbnails/1/64").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(content().json("{'status': 'NOT_FOUND', 'message': 'Thumbnail with videoId: 1 and size: 64 was not found!'}"));
    }

    @Test
    public void testThumbnailByVideoIdAndSizeWhenThumbnailFoundShouldReturn200() throws Exception {
        loadVideo();
        Video video = videoRepository.findById(1L).orElse(null);
        assert video != null;
        Thumbnail expected = thumbnailRepository.findThumbnailByVideoAndSize(video, "128X128");
        assert expected != null;
        ByteArrayResource bar = new ByteArrayResource(expected.getImage());
        mockMvc.perform(MockMvcRequestBuilders.get(url+"/api/v1/thumbnails/1/128").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().bytes(bar.getByteArray()));
    }

    private void removeThumbnails() {
        thumbnailRepository.deleteAll();
    }

    private void loadVideo() throws Exception {
        File defaultMP4 = ResourceUtils.getFile("classpath:default.mp4");
        InputStream inputStream = new FileInputStream(defaultMP4);
        MockMultipartFile multipartFile = new MockMultipartFile("videoFile", "default.mp4", null, inputStream);
        MockMultipartFile videoRequestCommand = new MockMultipartFile("videoRequest", null, "application/json",
                "{\"title\": \"Title 1\", \"category_id\": 1}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(url+"/api/v1/videos/upload")
                        .file(multipartFile)
                        .file(videoRequestCommand))
                .andExpect(status().is(201));
    }

}