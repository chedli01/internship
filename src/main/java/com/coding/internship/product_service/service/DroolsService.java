package com.coding.internship.product_service.service;

import com.coding.internship.product_service.model.Product;
import lombok.Data;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Data
@Service
public class DroolsService {

    private final KieContainer kieContainer;


    public Product applyDiscount(Product product) {
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            kieSession.insert(product);
            kieSession.fireAllRules();
            return product;
        } finally {
            kieSession.dispose();
        }
    }
}
