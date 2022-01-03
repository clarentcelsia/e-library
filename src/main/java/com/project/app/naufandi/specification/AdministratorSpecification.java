package com.project.app.naufandi.specification;

import com.project.app.naufandi.dto.AdministratorDTO;
import com.project.app.naufandi.entity.Administrator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AdministratorSpecification {
    public AdministratorSpecification() {
    }

    public static Specification<Administrator> getSpecification(AdministratorDTO administratorDTO){
        return new Specification<Administrator>() {
//            @Override
            public Predicate toPredicate(Root<Administrator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (administratorDTO.getSearchByUserName() != null){
                    Predicate userNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + administratorDTO.getSearchByUserName().toLowerCase() + "%");
                    predicates.add(userNamePredicate);
                }

                if (administratorDTO.getSearchByUserIdentityNumber() != null){
                    Predicate userIdentityNumberPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + administratorDTO.getSearchByUserIdentityNumber().toLowerCase() + "%" );
                    predicates.add(userIdentityNumberPredicate);
                }
                Predicate[] arrayPredicates = predicates.toArray(new Predicate[predicates.size()]);
                return criteriaBuilder.and(arrayPredicates);
            }
        };
    }
}
