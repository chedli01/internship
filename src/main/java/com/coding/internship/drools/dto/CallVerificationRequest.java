package com.coding.internship.drools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallVerificationRequest {
    private double minutesConsumed;
    private double totalConsumed;

}
