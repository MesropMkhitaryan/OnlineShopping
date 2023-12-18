package com.example.productservice.controller;

import com.example.productservice.dto.request.ProductRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.service.ProductService;
import com.example.productservice.util.IOUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

    private final ProductService service;
    private final ModelMapper mapper;
    private final IOUtil imgUtil;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") UUID productId) {
        return ResponseEntity.ok(service.findById(productId));
    }

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody ProductRequest request, @RequestHeader ("Authorization") String authHeader){
        return ResponseEntity.ok(mapper.map(service.save(request,authHeader), ProductResponse.class));
    }

    @PostMapping(value = "/upload/{productId}")
    public ResponseEntity<?> uploadImg(@RequestParam("photo") MultipartFile file, @PathVariable("productId") UUID productId){
        return ResponseEntity.ok(service.saveImgIntoProduct(productId,file));
    }

    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity<?> list(){
        return ResponseEntity
                .ok()
                .body(service.list());
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader){
        service.delete(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/update/{id}", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest productRequest){
        return ResponseEntity.ok(service.update(id, productRequest));
    }

    @GetMapping(value = "/getProductPic/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable String fileName) throws IOException {
        return imgUtil.getImageFully(fileName);
    }

    @GetMapping("/getProductByUserId")
    public ResponseEntity<?> getProductsByUserId(@RequestHeader ("Authorization") String authHeader){
        return ResponseEntity.ok(service.findAllByUserId(authHeader));
    }

    @GetMapping("/getUser")
    public ResponseEntity<UUID> getUserId(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(service.findByToken(authHeader));
    }

}
