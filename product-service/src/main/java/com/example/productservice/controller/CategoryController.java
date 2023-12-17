package com.example.productservice.controller;

import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService service;

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request){
        return ResponseEntity.ok(service.save(request));
    }

    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity<?> register(){
        return ResponseEntity
                .ok()
                .body(service.list());
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
