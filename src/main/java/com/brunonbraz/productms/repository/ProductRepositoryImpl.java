package com.brunonbraz.productms.repository;

import com.brunonbraz.productms.domain.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Product> findAllBySearch(String q, BigDecimal minPrice, BigDecimal maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        if(q != null && !q.isBlank()){
            predicates.add(cb.or(cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + q.toLowerCase() + "%")));
        }

        if(minPrice != null){
            predicates.add(cb.or(cb.greaterThan(root.get("price"), minPrice)));
        }

        if(maxPrice != null){
            predicates.add(cb.or(cb.lessThan(root.get("price"), maxPrice)));
        }

        Predicate[] predArray = new Predicate[predicates.size()];
        predicates.toArray(predArray);
        cq.where(predArray);
        TypedQuery<Product> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
