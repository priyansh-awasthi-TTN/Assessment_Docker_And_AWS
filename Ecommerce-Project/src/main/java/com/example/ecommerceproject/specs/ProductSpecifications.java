package com.example.ecommerceproject.specs;

import com.example.ecommerceproject.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductSpecifications {

    public static Specification<Product> buildFilter(Map<String, String> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.get("categoryId") != null) {
                predicates.add(cb.equal(root.get("category").get("id"), params.get("categoryId")));
            }

            if (params.get("sellerId") != null) {
                predicates.add(cb.equal(root.get("seller").get("id"), params.get("sellerId")));
            }

            if (params.get("id") != null) {
                predicates.add(cb.equal(root.get("id"), params.get("id")));
            }

            if (params.get("name") != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + params.get("name").toLowerCase() + "%"));
            }

            predicates.add(cb.equal(root.get("isDeleted"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
