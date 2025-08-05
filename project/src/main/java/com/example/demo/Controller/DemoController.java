package com.example.demo.Controller;

import com.example.demo.Service.FindNameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class DemoController {

    @Autowired
    private FindNameService findNameService;

    @GetMapping("/hello")
    public String getString(@RequestParam String name) {
        return findNameService.getString(name);
    }
}
