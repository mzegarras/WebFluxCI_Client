package com.example.lab04client.services;

import com.example.lab04client.config.ApiProperties;

import com.example.lab04client.exceptions.ProductNotFoundException;
import com.example.lab04client.models.Producto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ProductoServiceImplTest.TestConfiguration.class)
@WebFluxTest
public class ProductoServiceImplTest {


    public static class TestConfiguration{

        @Bean
        public MockWebServer mockWebServer(){
            return new MockWebServer();
        }

        @Bean
        public ApiProperties filesProperties(MockWebServer mockWebServer) throws MalformedURLException {
            ApiProperties filesProperties = new ApiProperties();

            filesProperties.setUrl(mockWebServer.url("/").toString());

            URL baseUrl = new URL(filesProperties.getUrl());

            filesProperties.setProductsListAll(new URL(baseUrl, "products").toString());
            filesProperties.setProductsById(new URL(baseUrl, "products/{id}").toString());
            filesProperties.setProductsDeleted(new URL(baseUrl, "products/{id}").toString());
            filesProperties.setProductsCreated(new URL(baseUrl, "products/{id}").toString());
            filesProperties.setProductsEdit(new URL(baseUrl, "products/{id}").toString());

            return filesProperties;
        }

        @Bean
        private ProductoService getProductoService(WebClient webClient,ApiProperties filesProperties){
            return new ProductoServiceImpl(webClient,filesProperties);
        }

        @Bean
        private WebClient webClient(ApiProperties apiProperties){
            return WebClient.create(apiProperties.getUrl());
        }

    }


    @Autowired
    private ProductoService productoService;

    @Autowired
    private ApiProperties filesProperties;

    @Autowired
    private MockWebServer mockWebServer;


    @BeforeEach
    void resetMocksAndStubs() {
    }

    @Test
    public  void sanity() {
        assertThat(productoService).isNotNull();
        assertThat(filesProperties).isNotNull();
    }

    @Test
    public void findById_ok() throws InterruptedException {

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\"id\":\"1\",\"nombre\":\"producto 1\",\"precio\":3.5}");

        mockWebServer.enqueue(mockResponse);

        Mono<Producto> productoMono = productoService.findById("1");

        StepVerifier.create(productoMono.log())
                .consumeNextWith(productoRS->{
                    assertThat(productoRS.getId()).isEqualTo("1");
                    assertThat(productoRS.getNombre()).isEqualTo("producto 1");
                    assertThat(productoRS.getPrecio()).isEqualTo(3.5d);
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/products/1");
        assertThat(request.getMethod()).isEqualTo("GET");
    }


    @Test
    public void findById_thenNoFoundError_ok() throws InterruptedException {

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(404)
                .setBody("{}");

        mockWebServer.enqueue(mockResponse);

        Mono<Producto> productoMono = productoService.findById("1");

        StepVerifier.create(productoMono.log())
                .expectErrorSatisfies(throwable->{
                    assertThat(throwable instanceof ProductNotFoundException).isTrue();
                    ProductNotFoundException ex = (ProductNotFoundException) throwable;
                    assertThat(ex.getId()).isEqualTo("1");
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/products/1");
        assertThat(request.getMethod()).isEqualTo("GET");

    }

    @Test
    public void delete_ok() throws  InterruptedException {

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpURLConnection.HTTP_NO_CONTENT);

        mockWebServer.enqueue(mockResponse);

        Mono<Void> productoMono = productoService.delete("666");

        StepVerifier.create(productoMono.log())
                .expectSubscription()
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/products/666");
        assertThat(request.getMethod()).isEqualTo("DELETE");


    }

    @Test
    public void listAll_ok() throws InterruptedException {

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("[{\"id\":\"1\",\"nombre\":\"producto 1\",\"precio\":3.5},{\"id\":\"2\",\"nombre\":\"producto 2\",\"precio\":4.5}]");

        mockWebServer.enqueue(mockResponse);

        Flux<Producto> productoFlux = productoService.findAll();

        StepVerifier.create(productoFlux.log())
                .consumeNextWith(productoRS->{
                    assertThat(productoRS.getId()).isEqualTo("1");
                    assertThat(productoRS.getNombre()).isEqualTo("producto 1");
                    assertThat(productoRS.getPrecio()).isEqualTo(3.5d);
                })
                .consumeNextWith(productoRS->{
                    assertThat(productoRS.getId()).isEqualTo("2");
                    assertThat(productoRS.getNombre()).isEqualTo("producto 2");
                    assertThat(productoRS.getPrecio()).isEqualTo(4.5d);
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/products");
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getHeader("Accept")).isEqualTo("application/json");

    }


}
