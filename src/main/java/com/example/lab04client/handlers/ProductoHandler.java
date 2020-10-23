package com.example.lab04client.handlers;

import com.example.lab04client.models.Producto;
import com.example.lab04client.services.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@AllArgsConstructor
public class ProductoHandler {

    private final ProductoService productoService;

    public Mono<ServerResponse> list(ServerRequest rq) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.findAll(), Producto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest rq) {
        String id = rq.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        Mono<Producto> productoMono = this.productoService.findById(id);

        return productoMono
                .flatMap(producto -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(producto)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> create(ServerRequest rq) {
        Mono<Producto> productoMono = rq.bodyToMono(Producto.class);

        return productoMono.flatMap(producto -> {

            if (producto.getCreateAt()==null){
                producto.setCreateAt(new Date());
            }
            return productoService.save(producto);
        }).flatMap(producto->ServerResponse
                .created(URI.create("/productos/" + producto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(producto)))
                .onErrorResume(error->{
                    WebClientResponseException errorResponse = (WebClientResponseException)error;
                    if(errorResponse.getStatusCode()== HttpStatus.BAD_REQUEST){
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(errorResponse.getResponseBodyAsString());
                    }

                    return Mono.error(errorResponse);

                });
    }

    public Mono<ServerResponse> edit(ServerRequest rq) {
        String id = rq.pathVariable("id");
        Mono<Producto> productoMono = rq.bodyToMono(Producto.class);

        return productoMono.flatMap(producto->ServerResponse
                .created(URI.create("/productos/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(producto)));
    }

    public Mono<ServerResponse> delete(ServerRequest rq) {
        String id = rq.pathVariable("id");

        return productoService.delete(id).then(ServerResponse.noContent().build());
    }
}






