package com.example.userservice.endpoint;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserEndpoint {


    private final UserService service;

//    @GetMapping("/make/admin")
//    public ResponseEntity<?> makeAdmin(@AuthenticationPrincipal User user){
//        service.makeAdmin(user.getId());
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/{email}")
    public User findByEmail(@PathVariable String email){
        return service.findByEmail(email);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> list(){
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/test")
    public String testService()
    {
        log.error("working");
        return "working";
    }
//
//    @GetMapping("/get/role")
//    public ResponseEntity<?> getRole(@AuthenticationPrincipal User user){
//        return ResponseEntity.ok(user.getRole());
//    }

}

