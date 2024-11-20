package com.star.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class RandomNumberController {

    private final Random random = new Random();

    @GetMapping("/random")
    public int getRandomNumber() {
        return random.nextInt();
    }
}