package com.example.productservice.controller;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.dto.ProductUpdateRequest;
import com.example.productservice.dto.SaveImgRequest;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.service.ProductService;
import com.example.productservice.util.ImgUtil;
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
    private final ImgUtil imgUtil;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") UUID productId) {
        return ResponseEntity.ok(service.findById(productId));
    }

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody ProductRequest request, @RequestHeader ("Authorization") String authHeader){
        return ResponseEntity.ok(mapper.map(service.save(request,authHeader), ProductResponse.class));
    }

    @PostMapping(value = "/upload/{productId}")
    public ResponseEntity<?> uploadImg(@RequestParam("photo") MultipartFile file, @PathVariable("productId") UUID productId){
        log.error("start of image uploading");
        return ResponseEntity.ok(service.saveImgIntoProduct(productId,file));
    }

    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity<?> list(){
        return ResponseEntity
                .ok()
                .body(service.list());
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/update/{id}", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProductUpdateRequest productRequest){
        return ResponseEntity.ok(service.update(id, productRequest));
    }

    @GetMapping(value = "/getProductPic/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable String fileName) throws IOException {
        return imgUtil.getImageFully(fileName);
    }

    @GetMapping("/getProductByUserId")
    public ResponseEntity<?> getProductsByUserId(@RequestHeader ("Authorization") String authHeader){
        return ResponseEntity.ok(service.findByUserId(authHeader));
    }

    @GetMapping("/getUser")
    public ResponseEntity<UUID> getUserId(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(service.findByToken(authHeader));
    }

}
