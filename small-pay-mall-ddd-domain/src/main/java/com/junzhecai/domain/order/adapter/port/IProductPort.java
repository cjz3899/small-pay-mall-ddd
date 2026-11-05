package com.junzhecai.domain.order.adapter.port;

import com.junzhecai.domain.order.model.entity.ProductEntity;

public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);
}
