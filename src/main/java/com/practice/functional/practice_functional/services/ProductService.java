package com.practice.functional.practice_functional.services;

import com.practice.functional.practice_functional.dto.ProductDTO;
import com.practice.functional.practice_functional.models.Product;
import com.practice.functional.practice_functional.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

  @Autowired
  private final ProductRepository productRepository;

  private final Supplier<List<Product>> defaultProductSupplier = () -> List.of(
    new Product(1L,"Laptop Gamer", 120.00),
    new Product(2L,"Teclado", 30.00),
    new Product(2L,"Mouse", 12.00)
  );

  private final Consumer<Product> logProduct = (product) -> {
    System.out.println("Producto agregado fue: " + product.getName() + " con precio: $" + product.getPrice());
  };

  private final Predicate<Product> isCheap = (product) -> product.getPrice() < 20.00;

  private final Function<Product, ProductDTO> productToDTO = (product) -> new ProductDTO(product.getName(), product.getPrice());

  // Cache con ConcurrentHashMap para evitar consulta repetitiva
  private final Map<Long, ProductDTO> productCache = new ConcurrentHashMap<>();

  // Cola para manejar productos mas recientes FIFO
  private final Queue<Product> recentProducts = new LinkedList<>();

  // Mantiene productos ordenados por precio en tiempo real
  private final NavigableSet<Product> sortedProducts = new TreeSet<>(Comparator.comparing(Product::getPrice));

  // Lista enlazada para histórico de modificaciones
  private final Deque<Product> modifiedProducts = new LinkedList<>();

  // Set para evitar productos duplicados por nombre
  private final Set<String> uniqueProductNames = new HashSet<>();

  public List<ProductDTO> getAllProducts() {
    List<Product> products = productRepository.findAll();
    if(products.isEmpty()) {
      System.out.println("No hay productos en la base de datos. Cargando productos de prueba...");
      products = defaultProductSupplier.get();
    }
    return products.stream().map(this.productToDTO).toList();
  }

  public ProductDTO addProduct(ProductDTO productDTO) {
    Product product = new Product();
    product.setName(productDTO.name());
    product.setPrice(productDTO.price());

    productRepository.save(product);
    logProduct.accept(product);

    // Agregar el producto a las estructuras de datos
    sortedProducts.add(product);
    recentProducts.offer(product);
    modifiedProducts.push(product);
    productCache.putIfAbsent(product.getId(), productDTO);

    if(recentProducts.size() > 5) {
      recentProducts.poll();
    }

    return productToDTO.apply(product);
  }

  public List<ProductDTO> getSortedProducts() {
    if(sortedProducts.isEmpty()) {
      sortedProducts.addAll(productRepository.findAll());
    }
    return sortedProducts.stream().map(this.productToDTO).toList();
  }

  public List<ProductDTO> getRecentProducts(int limit) {
    List<Product> recentList = new ArrayList<>(recentProducts);
    if(recentList.isEmpty()) {
      recentList.addAll(productRepository.findAll()
        .stream()
        .sorted(Comparator.comparing(Product::getId).reversed())
        .limit(limit)
        .toList()
      );
    }
    return recentList.stream().map(this.productToDTO).toList();
  }

  public List<ProductDTO> getModifiedProducts(int limit) {
    return modifiedProducts.stream().limit(limit).map(this.productToDTO).toList();
  }

  // Agrupando productos por precio
  public Map<Double, List<ProductDTO>> groupProductsByPrice() {
    return productRepository.findAll().stream()
      .map(this.productToDTO)
      .collect(Collectors.groupingBy(ProductDTO::price));
  }

  // Obteniendo máx y min
  public Map<String,ProductDTO> getMinMaxPriceProducts() {
    return Map.of(
      "Min", Objects.requireNonNull(productRepository.findAll().stream()
        .min(Comparator.comparing(Product::getPrice))
        .map(this.productToDTO)
        .orElse(null)),
      "Max", Objects.requireNonNull(productRepository.findAll().stream()
        .max(Comparator.comparing(Product::getPrice))
        .map(this.productToDTO)
        .orElse(null))
    );
  }

  // Obteniendo los productos más baratos
  public List<ProductDTO> getCheapestProducts() {
    return productRepository.findAll().stream()
      .filter(isCheap)
      .map(this.productToDTO)
      .toList();
  }
}
