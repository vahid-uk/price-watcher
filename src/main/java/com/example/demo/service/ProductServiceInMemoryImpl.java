package com.example.demo.service;

import com.example.demo.dto.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("inMemory")
public class ProductServiceInMemoryImpl implements ProductService {

    private final static Logger log = LoggerFactory.getLogger(ProductServiceInMemoryImpl.class);

    private final ObjectMapper objectMapper;

    private final Map<Long, Product> products;

    public ProductServiceInMemoryImpl(Map<Long, Product> products, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.products = generateProducts();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.of(products.get(id));
    }

    @Override
    public Optional<Product> findByUrl(String url) {
        return products.entrySet().stream()
                .filter(f -> f.getValue().url().equalsIgnoreCase(url))
                .findAny()
                .map(Map.Entry::getValue);

    }

    @Override
    public Optional<Product> updateProduct(Product product) {
        Optional<Product> productOptional = findById(product.id());
        if (productOptional.isEmpty()) {
            return productOptional;
        } else {
            Product actualProduct = productOptional.get();
            products.put(product.id(),  new Product(product.id(),
                    (product.title() != null ? product.title(): actualProduct.title()),
                    (product.url() != null ? product.url(): actualProduct.url()),
                    (product.price())
            ));
        }

        return Optional.of(product);
    }

    private Map<Long,Product> generateProducts() {
        Map<Long,Product> products = new HashMap<>();
        JsonNode json = null;

        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/products.json")) {
            json = objectMapper.readValue(inputStream, JsonNode.class);
        } catch(IOException e) {
            log.atError().log("Error generating product:", e);
        }
        if (json != null && !json.isNull()) {
            JsonNode productJson = getProducts(json);
            for (JsonNode product: productJson) {
                Product prod = createProduct(product);
                products.put(prod.id(), prod);
                log.atInfo().log("Adding product id {}, title {}, price  {}, url {}",
                        prod.id(), prod.title(), prod.price(), prod.url());
            }
        }
        return products;
    }

    private Product createProduct(JsonNode json) {
        Long id = json.get("id").asLong();
        String title = json.get("title").asText();
        BigDecimal price = json.get("price").decimalValue();
        /**
         * Original json does not include url, generating a dummy url instead
         */
        String url = DUMMY_ECOMMERCE_URL + id;
        if (!ObjectUtils.isEmpty(id) && StringUtils.hasLength(title) && !ObjectUtils.isEmpty(price)) {
            return new Product(id,title,url,price);
        }
        throw new IllegalArgumentException("Invalid JSON object, could not locate either id, title or price");
    }

    private JsonNode getProducts(JsonNode json) {
        return Optional.ofNullable(json).map(p-> p.get("products"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid JSON object"));
    }

}
