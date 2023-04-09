package com.example.cafebackend.service;

import com.example.cafebackend.constants.CafeConstants;
import com.example.cafebackend.model.binding.category.GetProductCategoryModel;
import com.example.cafebackend.model.binding.product.AddProductModel;
import com.example.cafebackend.model.binding.product.GetProductModel;
import com.example.cafebackend.model.binding.product.UpdateProductStatusModel;
import com.example.cafebackend.model.entity.Category;
import com.example.cafebackend.model.entity.Product;
import com.example.cafebackend.repository.ProductRepository;
import com.example.cafebackend.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public ResponseEntity<String> addNewProduct(AddProductModel addProductModel) {
        try {
            Product product = em.createQuery("select p from Product p", Product.class)
                    .getResultStream()
                    .filter(c -> c.getName().equals(addProductModel.getName()))
                    .findAny()
                    .orElse(null);

            if (Objects.isNull(product)) {
                product = constructProduct(addProductModel);
                em.persist(product);
                return CafeUtils.getResponseEntity("Product added successfully.", HttpStatus.OK);
            }

            return CafeUtils.getResponseEntity("Product already exists.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product constructProduct(AddProductModel addProductModel) {
        Product newProduct = new Product();

        newProduct.setName(addProductModel.getName());
        newProduct.setDescription(addProductModel.getDescription());
        newProduct.setPrice(new BigDecimal(addProductModel.getPrice()));
        newProduct.setStatus("true");

        Category productCategory = categoryService.findCategoryById(addProductModel.getCategoryId());

        if (!Objects.isNull(productCategory)) {
            newProduct.setCategory(productCategory);
        }

        return newProduct;
    }

    public List<GetProductModel> getAllProducts() {
        log.info("Fetching products..");

        return em.createQuery("select p from Product p", Product.class)
                .getResultStream()
                .map(this::mapToProductModel)
                .collect(Collectors.toList());
    }

    private GetProductModel mapToProductModel(Product product) {
        return new GetProductModel(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                //    it is important that GetProductModel contains both
                //    categoryId and categoryName, so that get and edit can display category name
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    @Transactional
    public ResponseEntity<String> updateProduct(AddProductModel updateProductModel) {
        log.info("Updating product...");

        try {

            Product product = em.find(Product.class, updateProductModel.getId());

            boolean isChanged = false;

            if (!Objects.isNull(product)) {
                if (!product.getName().equals(updateProductModel.getName())) {
                    product.setName(updateProductModel.getName());
                    isChanged = true;
                }

                if (product.getCategory().getId() != Long.parseLong(updateProductModel.getCategoryId())) {
                    product.setCategory(categoryService.findCategoryById(updateProductModel.getCategoryId()));
                    isChanged = true;
                }

                if (!product.getDescription().equals(updateProductModel.getDescription())) {
                    product.setDescription(updateProductModel.getDescription());
                    isChanged = true;
                }

                if (!product.getPrice().equals(new BigDecimal(updateProductModel.getPrice()))) {
                    product.setPrice(new BigDecimal(updateProductModel.getPrice()));
                    isChanged = true;
                }

                if (isChanged) {
                    log.info("Product Updated Successfully.");
                    return CafeUtils.getResponseEntity("Product Updated Successfully.", HttpStatus.OK);
                }

                log.info("Product has no changes to commit.");
                return CafeUtils.getResponseEntity("Product has no changes to commit.", HttpStatus.OK);
            }

            log.info("Product not found.");
            return CafeUtils.getResponseEntity("Product not found", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<String> deleteProduct(Long id) {
        log.info("Deleting product...");

        try {
            Product product = em.find(Product.class, id);

            if (Objects.nonNull(product)) {
                em.remove(product);
                log.info("Product Deleted Successfully");
                return CafeUtils.getResponseEntity("Product Deleted Successfully", HttpStatus.OK);
            }

            log.info("Product with id {} does not exist.", id);
            return CafeUtils.getResponseEntity("Product does not exist.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<String> updateStatus(UpdateProductStatusModel updateProductStatus) {
        log.info("Updating product status...");

        try {
            Product product = em.find(Product.class, updateProductStatus.getId());

            if (Objects.nonNull(product)) {

                if (!product.getStatus().equals(updateProductStatus.getStatus())) {
                    product.setStatus(updateProductStatus.getStatus());

                    log.info("Product Status Updated Successfully");
                    return CafeUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);
                }

                log.info("Product status is the same.");
                return CafeUtils.getResponseEntity("Product status is the same.", HttpStatus.BAD_REQUEST);
            }

            log.info("Product with id {} does not exist.", updateProductStatus.getId());
            return CafeUtils.getResponseEntity("Product does not exist.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> getProductsByCategory(Long categoryId) {
        log.info("Fetching products by category...");

        List<GetProductModel> products = em.createQuery("select p from Product p where p.category.id=:categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultStream()
                .map(this::mapToProductModel)
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            if (Objects.isNull(em.find(Category.class, categoryId))) {
                return CafeUtils.getResponseEntity("Category with the given id does not exist.", HttpStatus.BAD_REQUEST);
            }

            return CafeUtils.getResponseEntity("Category has no products.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(products);
    }

    public ResponseEntity<?> getProductById(Long id) {
        log.info("Fetching products by id...");

        try {
            Product foundProduct = em.find(Product.class, id);

            if (Objects.nonNull(foundProduct)) {
                log.info("Product with id {} found", id);
                return ResponseEntity.ok(mapToProductModel(foundProduct));
            }

            log.info("Product not found");
            return CafeUtils.getResponseEntity("Product not found", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}