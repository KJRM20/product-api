package com.practice.functional.practice_functional.repositories;

import com.practice.functional.practice_functional.models.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
