package com.example.health.controller;

import com.example.health.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @LocalServerPort
    private int port;

    private String url;

    @BeforeEach
    public void setup() {
        url = String.format("http://localhost:%d", port);
    }

    @Test
    public void testListAllCategoriesWhenAllCategoriesPresent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url+"/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{'total':3,'categories':[{'category_id':1,'category_name':'Exercise'},{'category_id':2,'category_name':'Education'},{'category_id':3,'category_name':'Recipe'}]}"));
    }

    @Test
    public void testListAllCategoriesWhenNotPresentShouldReturn404() throws Exception {
        categoryRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get(url+"/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(content().json("{'status': 'NOT_FOUND', 'message': 'Categories not found!'}"));
    }

}