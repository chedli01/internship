package com.coding.internship.drools.service;

import com.coding.internship.drools.dto.*;
import com.coding.internship.notification.email.EmailService;
import com.coding.internship.notification.sms.service.SmsService;
import com.coding.internship.order.model.Order;
import com.coding.internship.payment.model.Payment;
import com.coding.internship.plan.service.PlanService;
import com.coding.internship.product.model.Product;
import com.coding.internship.product.service.SpeceficService;
import com.coding.internship.subscription.model.Subscription;
import com.coding.internship.user.client.model.Client;
import com.coding.internship.user.client.service.ClientService;
import lombok.Data;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class DroolsService {

    private final KieContainer kieContainer;
    private final SpeceficService speceficService;
    private final ClientService clientService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final PlanService planService;


    public Product applyDiscount(Product product) {
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            kieSession.insert(product);
            kieSession.getAgenda().getAgendaGroup("pricing").setFocus();
            kieSession.fireAllRules();
            return product;
        } finally {
            kieSession.dispose();
        }
    }
//
    public List<Product> applyDiscountForCategory(List<Product> products, String category) {
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");

        try {
            // Track modified products
            List<Product> modifiedProducts = new ArrayList<>();

            // Set global before inserting facts
            kieSession.setGlobal("targetCategory", category);
            kieSession.setGlobal("modifiedProducts", modifiedProducts);

            // Insert all products
            products.forEach(kieSession::insert);

            // Fire rules
            kieSession.fireAllRules();

            return modifiedProducts;
        } finally {
            kieSession.dispose();
        }
    }
    public ResponseObjectDto applyDiscountOnOrder(Order order){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        Subscription subscription =clientService.getActiveSub(order.getClient().getId());
        Client client = order.getClient();
        try {

            kieSession.insert(order);
            kieSession.insert(subscription);
            kieSession.insert(client);
            kieSession.getAgenda().getAgendaGroup("discount").setFocus();
            kieSession.fireAllRules();
            return ResponseObjectDto.builder().order(order).subscription(subscription).client(client).build();
        }
        finally {
            kieSession.dispose();
        }
    }
    public DataVerificationResult verifyData(Subscription subscription,DataVerificationRequest dataVerificationRequest){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            DataVerificationResult dataVerificationResult = new DataVerificationResult(false,false,false,false);
            kieSession.insert(subscription);
            kieSession.insert(dataVerificationRequest);
            kieSession.insert(dataVerificationResult);
            kieSession.getAgenda().getAgendaGroup("data").setFocus();
            kieSession.fireAllRules();
            return dataVerificationResult;
        }
        finally {
            kieSession.dispose();
        }

    }

    public Payment applyPaymentProcess(Payment payment){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            kieSession.insert(payment);
            kieSession.getAgenda().getAgendaGroup("payment").setFocus();
            kieSession.fireAllRules();
            return payment;
        }
        finally {
            kieSession.dispose();
        }
    }
    public Subscription applyProcessOnSub(Subscription subscription){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        Subscription sub = clientService.getLatestInactiveSub(subscription.getClient().getId()).orElse(null);

        try {
            kieSession.setGlobal("previousSubPlanId",sub.getPlan().getId());
            System.out.println("previousSubPlanId: "+sub.getPlan().getId());
            kieSession.insert(subscription);
            kieSession.getAgenda().getAgendaGroup("discount").setFocus();
            kieSession.getAgenda().getAgendaGroup("sub").setFocus();


            kieSession.fireAllRules();
            return subscription;
        }
        finally {
            kieSession.dispose();
        }

    }
    public VerificationResult verifyCalls(Subscription subscription, CallVerificationRequest callVerificationRequest){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            VerificationResult verificationResult = new VerificationResult(false,false);



            kieSession.insert(subscription);
            kieSession.insert(callVerificationRequest);
            kieSession.insert(verificationResult);
            kieSession.getAgenda().getAgendaGroup("calls").setFocus();
            kieSession.fireAllRules();
            return verificationResult;
        }
        finally {
            kieSession.dispose();
        }


    }
    public Boolean verifySms(Subscription subscription, SmsVerificationRequest smsVerificationRequest){
        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
        try {
            VerificationResult verificationResult = new VerificationResult(false,false);



            kieSession.insert(subscription);
            kieSession.insert(smsVerificationRequest);
            kieSession.insert(verificationResult);
            kieSession.getAgenda().getAgendaGroup("sms").setFocus();
            kieSession.fireAllRules();
            return verificationResult.isValid();
        }
        finally {
            kieSession.dispose();
        }


    }

//
//    public Product changeInDb(Product product,Long id){
//        KieSession kieSession = kieContainer.newKieSession("ksession-rules");
//        try {
//            kieSession.setGlobal("productService",speceficService);
//            kieSession.setGlobal("ProductId",id);
//            kieSession.insert(product);
//            kieSession.getAgenda().getAgendaGroup("renaming").setFocus();
//            kieSession.fireAllRules();
//            return product;
//
//
//        }
//        finally {
//            kieSession.dispose();
//        }
//    }
}
