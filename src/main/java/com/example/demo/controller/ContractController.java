package com.example.demo.controller;

import com.example.demo.controller.request.StoreValueRequest;
import com.example.demo.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/store")
    public String getValue() {
        try {
            var transactionReceipt = contractService.getValue();
            return "Result: " + transactionReceipt.toString();
            // return "Transaction hash: " + transactionReceipt.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while processing the transaction: " + e.getMessage();
        }
    }

    @PostMapping("/store")
    public String storeValue(@RequestBody StoreValueRequest value) {
        try {
            var transactionReceipt2 = contractService.saveValue(value.getValue());
            return "Transaction result: " + transactionReceipt2.getTransactionHash();
            // return "Transaction hash: " + transactionReceipt.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while processing the transaction: " + e.getMessage();
        }
    }
}
