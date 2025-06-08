package com.jeremyalv.flow.service.product;

import com.jeremyalv.flow.dto.product.request.CreateProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.DeleteProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.EditProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.GetProductRequestDTO;
import com.jeremyalv.flow.dto.product.response.CreateProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.DeleteProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.EditProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetAllProductsResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetProductResponseDTO;

public interface ProductService {
    GetAllProductsResponseDTO getProducts();

    GetProductResponseDTO getProductDetail(GetProductRequestDTO request);
    
    CreateProductResponseDTO createProduct(CreateProductRequestDTO request);

    EditProductResponseDTO editProduct(EditProductRequestDTO request);

    DeleteProductResponseDTO deleteProduct(DeleteProductRequestDTO request);
}
