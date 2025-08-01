package com.coding.internship.user.client.model;

import com.coding.internship.order.model.Order;
import com.coding.internship.subscription.model.Subscription;
import com.coding.internship.user.client.enums.ClientType;
import com.coding.internship.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends User {
    private String address;
    private String phoneNumber;
    private String cin;
    private Boolean isStudent;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClientType type = ClientType.CUSTOMER;
    @OneToMany(mappedBy = "client")
    private List<Subscription> subscriptions;
    @OneToMany(mappedBy = "client")
    private List<Order> orders;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + type.name()));
    }
}
