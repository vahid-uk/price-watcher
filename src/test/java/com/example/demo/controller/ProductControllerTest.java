package com.example.demo.controller;

import com.example.demo.service.ProductServiceInMemoryImpl;
import com.example.demo.service.watch.basic.ProductWatcherServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({ProductServiceInMemoryImpl.class})
public class ProductControllerTest {

    @MockitoBean
    ProductWatcherServiceImpl productWatcherService;

    @Autowired
    MockMvcTester mvcTester;

    @Autowired
    MockMvc mockMvc;

    /**
     * below test cases cover getting a product entry product id, it isn't essential or required
     * as part of the core API requirements and nor is updating product but these can both be used to set
     * a new product price and verify that changes have taken place.
     */
    @Test
    public void test_existing_product_returns_200_code_using_mock_mvc() throws Exception{
        mockMvc.perform(get("/product/{id}", 22)).andExpect(status().is(200));
    }

    @Test
    public void test_existing_product_returns_200_code_using_mock_mvc_tester() {
        assertThat(mvcTester.get().uri("/product/{id}",22).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK);
    }

    @Test
    public void test_getting_product_1_and_parsing_json_using_assert_that() {
        assertThat(mvcTester.get().uri("/product/{id}", 22).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson()
                .satisfies(json-> {
                    json.assertThat().extractingPath("$.id").as("id").isEqualTo(22);
                    json.assertThat().extractingPath("$.title").as("title")
                            .isEqualTo("Dog Food");
                    json.assertThat().extractingPath("$.price").as("price").isEqualTo(10.99);
                });

    }


    /**
     * Below test cases cover updating an existing product price and title
     */

    @Test
    public void test_updating_an_entries_title_and_price() {
        var watchUrl = """
                {"id": "2", "title":"some new title", "price": "22.10"}""";
        Assertions.assertThat(mvcTester.post().uri("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(watchUrl)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON);
        assertThat(mvcTester.get().uri("/product/{id}", 2).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson()
                .satisfies(json-> {
                    json.assertThat().extractingPath("$.id").as("id").isEqualTo(2);
                    json.assertThat().extractingPath("$.title").as("title")
                            .isEqualTo("some new title");
                    json.assertThat().extractingPath("$.price").as("price").isEqualTo(22.10);
                });

    }

    @Test
    public void test_updating_an_entries_price() {
        var watchUrl = """
                {"id": "3",  "price": "22.30"}""";
        Assertions.assertThat(mvcTester.post().uri("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(watchUrl)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON);
        assertThat(mvcTester.get().uri("/product/{id}", 3).accept(MediaType.APPLICATION_JSON))
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson()
                .satisfies(json-> {
                    json.assertThat().extractingPath("$.id").as("id").isEqualTo(3);
                    json.assertThat().extractingPath("$.price").as("price").isEqualTo(22.30);
                });

    }
}
