package com.practice.functional.practice_functional.services;

import com.practice.functional.practice_functional.dto.ProductDTO;
import com.practice.functional.practice_functional.models.Product;
import com.practice.functional.practice_functional.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  private final Supplier<List<Product>> defaultProductSupplier = () -> List.of(
    new Product("1", "Laptop gamer", 120.00),
    new Product("2", "Teclado", 30.00),
    new Product("3", "Mouse", 12.00)
  );

  private final Consumer<Product> logProduct = product ->
    System.out.println("Producto agregado fue: "+ product.getName()+" con precio de "+product.getPrice());

  private final Predicate<Product> isCheap = product -> product.getPrice()<20.0;

  private final Function<Product, ProductDTO> productToDTO = product -> new ProductDTO(product.getId(), product.getName(), product.getPrice());

  // Obtener los productos - map,  switchIfEmpty
  public Flux<ProductDTO> getAllProducts(){

    return productRepository.findAll()
      .map(productToDTO)
      .switchIfEmpty(Flux.fromIterable(defaultProductSupplier.get()).map(productToDTO)
        .delayElements(Duration.ofMillis(500)));
  }

  // Obtener un producto por ID - flatMap, defaultIfEmpty, doOnError
  public Mono<ProductDTO> getProductById(String id){
    return productRepository.findById(id)
      .flatMap(product -> Mono.just(productToDTO.apply(product)))
      .doOnError(error -> System.out.println("Error al buscar el producto " + error.getMessage()))
      .defaultIfEmpty(new ProductDTO(id,"Producto no encontrado", 0.00));
  }

  // Agregar un producto - doOnNext
  public Mono<ProductDTO> addProduct(ProductDTO productDTO) {
    Product product = new Product();
    product.setName(productDTO.name());
    product.setPrice(productDTO.price());

    return productRepository.save(product)
      .doOnNext(logProduct)
      .map(savedProduct -> new ProductDTO(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice()));
  }

  // Obtener los productos baratos - filter, map
  public Flux<ProductDTO> getCheapProducts(){
    return productRepository.findAll()
      .filter(isCheap)
      .map(productToDTO);
  }

  // Obtener los ultimos 3 agregados - take
  public Flux<ProductDTO> getLastProducts(){
    return productRepository.findAll()
      .take(3)
      .map(productToDTO);
  }

  // Eliminar por id - doOnError
  public Mono<Void> deleteProduct(String id){
    return productRepository.findById(id)
      .flatMap(product -> productRepository.deleteById(id))
      .doOnError(error -> System.out.println("Error al eliminar el producto " + error.getMessage()));
  }

  // Contar productos - collectList /map /do on next
  public Mono<Integer> countProducts(){
    return productRepository.findAll()
      .collectList()
      .map(List::size)
      .doOnNext(count-> System.out.println("Cantidad de productos: " + count));
  }
}
