package com.practice.functional.practice_functional.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class Product implements Comparable<Product> {
  @Id
  private String id;
  private String name;
  private Double price;

  public Product(String name, Double price) {
    this.name = name;
    this.price = price;
  }

  @Override
  public int compareTo(Product other) {
    return Double.compare(this.price, other.price);
  }
}
