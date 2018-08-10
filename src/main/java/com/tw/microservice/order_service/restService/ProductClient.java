package com.tw.microservice.order_service.restService;

import com.tw.microservice.order_service.controller.VO.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("product-service")
public interface ProductClient {
    @RequestMapping(method = RequestMethod.GET, value = "/products/{id}")
    Product getProductById(@PathVariable("id") Long id);

}
