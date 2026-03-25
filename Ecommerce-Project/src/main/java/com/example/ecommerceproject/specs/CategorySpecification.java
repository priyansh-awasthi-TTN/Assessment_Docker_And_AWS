package com.example.ecommerceproject.specs;

import org.springframework.data.jpa.domain.Specification;

import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.CategoryMetadataField;

public class CategorySpecification {
    public static Specification<CategoryMetadataField> metadataFieldNameContains(String query){
        return (root, cq, cb) -> {
            if(query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
        };
    }

    public static Specification<Category> categoryNameContains(String query) {
        return (root, cq, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
        };
    }

    public static Specification<Category> categoryHasParentId(Long parentId) {
        return (root, cq, cb) -> {
            if (parentId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("parentCategory").get("id"), parentId);
        };
    }
}
