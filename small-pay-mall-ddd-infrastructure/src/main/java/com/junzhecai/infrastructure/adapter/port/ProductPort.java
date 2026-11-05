package com.junzhecai.infrastructure.adapter.port;

import com.junzhecai.domain.order.adapter.port.IProductPort;
import com.junzhecai.domain.order.model.entity.ProductEntity;
import com.junzhecai.infrastructure.gateway.ProductRPC;
import com.junzhecai.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

@Service
public class ProductPort implements IProductPort {

    private final ProductRPC productRPC;

    public ProductPort(ProductRPC productRPC) {
        this.productRPC = productRPC;
    }


    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);

        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }
}
