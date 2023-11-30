package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.*;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Service
public class ContractService {
    private final Web3j web3j;

    @Autowired
    public ContractService(Web3j web3j) {
        this.web3j = web3j;
    }

    public TransactionReceipt saveValue(String value) throws Exception {
        String pk = "0x10a095925d89e01edd85dae6dc05a91afe2df5c4572a1636fb39414a7cf285e4"; // Replace with your private key
        Credentials credentials = Credentials.create(pk);

        String contractAddress = "0xdb9F448690D38095a1B8D3565aB140813f1fcfca";

        Function function = new Function(
                "store", // Use the correct function name
                Arrays.asList(new Uint(BigInteger.valueOf(Integer.parseInt(value)))),
                Collections.emptyList()
        );

        // Encode function values in transaction data format
        String txData = FunctionEncoder.encode(function);

        TransactionManager txManager = new RawTransactionManager(web3j, credentials);

        // Increase the gas limit
        BigInteger gasPrice = BigInteger.valueOf(20000000000L);
        BigInteger gasLimit = BigInteger.valueOf(6721975);

        EthSendTransaction ethSendTransaction = txManager.sendTransaction(
                gasPrice,
                gasLimit,
                contractAddress,
                txData,
                BigInteger.ZERO);

        // Check if there is an error in the response
        if (ethSendTransaction.hasError()) {
            Response.Error error = ethSendTransaction.getError();
            throw new RuntimeException("Error sending transaction: " + error.getMessage());
        }

        // Get the transaction hash from the EthSendTransaction response
        String transactionHash = ethSendTransaction.getTransactionHash();

        // Wait for transaction to be mined
        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                web3j,
                TransactionManager.DEFAULT_POLLING_FREQUENCY,
                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);

        return receiptProcessor.waitForTransactionReceipt(transactionHash);
    }

    public BigInteger getValue() throws Exception {
        String pk = "0x10a095925d89e01edd85dae6dc05a91afe2df5c4572a1636fb39414a7cf285e4"; // Replace with your private key

        String contractAddress = "0xdb9F448690D38095a1B8D3565aB140813f1fcfca";

        Function function = new Function(
                "retrieve", // Use the correct function name
                Collections.emptyList(), // Function returned parameters
                Collections.emptyList() // Function input parameters
        );

        // Encode function values in transaction data format
        String encodedFunction = FunctionEncoder.encode(function);

        // Make a call to the smart contract (view function, no transaction sent)
        EthCall ethCall = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                        pk,
                        contractAddress,
                        encodedFunction), DefaultBlockParameterName.LATEST)
                .send();

        // Parse the result from the call
        String result = ethCall.getResult();
        return Numeric.decodeQuantity(result);
    }
}
