package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class EthereumConfig {

    @Bean
    public Web3j web3j() {
        // Assuming Ganache is running on the default HTTP provider URL
        // If Ganache is running on a different URL, replace the URL accordingly
        return Web3j.build(new HttpService("http://localhost:7545"));
    }
}
