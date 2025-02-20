package com.practice.functional.practice_functional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductDTO(
  String id,

  @NotBlank(message = "El nombre del producto no puede estar vacío")
  String name,

  @Positive(message = "El precio del producto no puede ser negativo")
  Double price
) { }
