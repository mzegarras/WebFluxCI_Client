package com.example.lab04client.services;

import com.example.lab04client.config.ApiProperties;
import com.example.lab04client.models.Producto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    public ProductoServiceImpl(WebClient webClient,ApiProperties apiProperties) {
        this.webClient = webClient;
        this.apiProperties=apiProperties;
    }

    @Override
    public Flux<Producto> findAll() {
        return webClient.get()
                //.uri(this.apiProperties.getProductsListAll())
                //.accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMapMany(response->response.bodyToFlux(Producto.class));
    }

    @Override
    public Mono<Producto> findById(String id) {

        Map<String,String> params = new HashMap<>();
        params.put("id",id);

        return webClient.get()
                .uri( this.apiProperties.getProductsById(),params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Producto> update(Producto producto,String id) {
        return webClient.post()
                .uri("/{id}", Collections.singletonMap("id",id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.delete()
                .uri(this.apiProperties.getProductsDeleted(), Collections.singletonMap("id",id))
                .exchange()
                .then();
    }
}
