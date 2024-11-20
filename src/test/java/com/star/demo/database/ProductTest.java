package com.star.demo.database;

import com.star.demo.mapper.ProductMapper;
import com.star.demo.model.Product;
import com.star.demo.service.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<Product> userList = productRepository.list();
        userList.forEach(System.out::println);
    }
}