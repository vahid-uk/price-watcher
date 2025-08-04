package com.example.demo.controller;

import com.example.demo.service.watch.basic.ProductWatcherServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest( ProductWatchController.class)
public class ProductWatchControllerTest {

    @MockitoBean
    ProductWatcherServiceImpl productWatcherService;

    @Autowired
    MockMvcTester mvcTester;

    /**
     * Below test cases cover adding entry to watch list
     */

    @Test
    public void test_adding_an_entry_with_no_content() {
        var watchUrl = """
                {}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_adding_an_entry_with_no_price() {
        var watchUrl = """
                {"url": "http://localhost:8080/product/2"}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_adding_an_entry_with_empty_price() {
        var watchUrl = """
                {"url": "http://localhost:8080/product/2", "price": ""}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_adding_an_entry_with_no_url() {
        var watchUrl = """
                {"price": "22.10"}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_adding_an_entry_with_empty_url() {
        var watchUrl = """
                {"url": "", "price": "22.10"}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_adding_an_entry() {
        var watchUrl = """
                {"url": "http://localhost:8080/product/2", "price": "22.10"}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.CREATED)
                .hasContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * Perhaps they just wish to re-add entry and set watch price to zero, affectively never.
     * Perhaps such situation should call removal of entry in the backend?
     * Room for thought here and test cases can help uncover the edge circumstances.
     */
    @Test
    public void test_adding_an_entry_set_to_zero() {
        var watchUrl = """
                {"url": "http://localhost:8080/product/2", "price": "0"}""";
        assertThat(mvcTester.post().uri("/watch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.CREATED)
                .hasContentType(MediaType.APPLICATION_JSON);
    }

    /**
     *Below test cases cover deletion of entries from watch list
     */
    @Test
    public void test_deleting_an_entry_with_no_content() {
        var watchUrl = """
                {}""";
        assertThat(mvcTester.delete().uri("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_deleting_an_entry_with_empty_url() {
        var watchUrl = """
                {"url": ""}""";
        assertThat(mvcTester.delete().uri("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_deleting_an_entry() {
        var watchUrl = """
                {"url": "http://localhost:8080/product/2"}""";
        assertThat(mvcTester.delete().uri("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(watchUrl)
                .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.NO_CONTENT);

    }
}
