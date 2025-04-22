package com.cobranca.rest.client;


import com.cobranca.rest.client.dto.AsaasCustomerRequest;
import com.cobranca.rest.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "asaasClient", url = "${asaas.url}", configuration = AsaasClientConfig.class)
public interface AsaasClient {

    @PostMapping(value = "/customers", consumes = MediaType.APPLICATION_JSON_VALUE)
    AsaasCustomerResponse criarCliente(@RequestBody AsaasCustomerRequest request);

    @PostMapping(value = "/payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    AsaasPaymentResponse criarCobranca(@RequestBody AsaasPaymentRequest request);

    @GetMapping("/payments/{id}")
    AsaasPaymentResponse consultarPagamento(@PathVariable("id") String paymentId);

    @PostMapping(value = "/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    AsaasSubscriptionResponse criarAssinatura(@RequestBody AsaasSubscriptionRequest request);

    @GetMapping("/subscriptions/{id}")
    AsaasSubscriptionResponse consultarAssinatura(@PathVariable("id") String subscriptionId);

    @PostMapping("/payments/{id}/cancel")
    void cancelarPagamento(@PathVariable("id") String paymentId);


}