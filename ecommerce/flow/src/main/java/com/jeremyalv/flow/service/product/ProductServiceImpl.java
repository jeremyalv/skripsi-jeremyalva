package com.jeremyalv.flow.service.product;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeremyalv.flow.constants.Constants;
import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.dto.product.request.CreateProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.DeleteProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.EditProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.GetProductRequestDTO;
import com.jeremyalv.flow.dto.product.response.CreateProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.DeleteProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.EditProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetAllProductsResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetProductResponseDTO;
import com.jeremyalv.flow.exceptions.product.ProductNotFoundException;
import com.jeremyalv.flow.model.Product;
import com.jeremyalv.flow.repository.ProductRepository;
import com.jeremyalv.flow.strategy.MessagingStrategy;
import com.jeremyalv.flow.utils.Utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final MessagingStrategy<String, Product> productMessagingStrategy;

    private final MeterRegistry meterRegistry;

    @Override
    public GetAllProductsResponseDTO getProducts() {
        List<Product> products = productRepository.findAll();

        return GetAllProductsResponseDTO.builder()
                .products(products)
                .success(true)
                .build();
    }

    @Override
    public GetProductResponseDTO getProductDetail(GetProductRequestDTO request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new ProductNotFoundException(request.getId()));

        publishProductEvent(Constants.VIEW_PRODUCT_EVENT_NAME, request.getUserId(), product);
        
        return GetProductResponseDTO.builder()
                .product(product)
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public CreateProductResponseDTO createProduct(CreateProductRequestDTO request) {
        Product product = Product
                .builder()
                .name(request.getName())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();
        
        Product savedProduct = productRepository.save(product);

        return CreateProductResponseDTO.builder()
                .product(savedProduct)
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public EditProductResponseDTO editProduct(EditProductRequestDTO request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new ProductNotFoundException(request.getId()));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCurrency() != null) {
            product.setCurrency(request.getCurrency());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        return EditProductResponseDTO.builder()
                .product(product)
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public DeleteProductResponseDTO deleteProduct(DeleteProductRequestDTO request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new ProductNotFoundException(request.getId()));

        productRepository.deleteById(request.getId());

        return DeleteProductResponseDTO.builder()
                .id(product.getId())
                .success(true)
                .build();
    }

    private void publishProductEvent(String eventName, Long userId, Product product) {
        String topic = Utils.DetermineTopic(productMessagingStrategy, Constants.PRODUCTS_TOPIC_NAME);
        
        MessageEnvelopeDTO<String, Product> productEventDto = new MessageEnvelopeDTO<String, Product>(
                topic,
                product.getId().toString(),
                product,
                Collections.emptyMap()
        );

        String strategyName = productMessagingStrategy.getClass().getSimpleName();
        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = "unknown";
        try {
            productMessagingStrategy.publish(productEventDto)
                    .whenComplete((result, exception) -> {
                        String finalOutcome = "unknown";
                        if (exception != null) {
                            log.error(String.format("Async publish failed for %s event: %s", eventName, exception.getMessage()));
                            finalOutcome = "failure";
                        } else if (result != null && result.isSuccess()) {
                            finalOutcome = "success";
                        } else {
                            finalOutcome = "completed_unknown";
                        }

                        sample.stop(meterRegistry.timer("messaging.publish.latency", 
                                "eventName", eventName,
                                        "strategy", strategyName,
                                        "topic", topic,
                                        "outcome", finalOutcome));
                    });
        } catch (Exception e) {
            log.error("Synchronous error during publish()");
            outcome = "error_sync";
            sample.stop(meterRegistry.timer("messaging.publish.latency",
                            "eventName", eventName,
                            "strategy", strategyName,
                            "topic", topic,
                            "outcome", outcome));
        }
    }
}
