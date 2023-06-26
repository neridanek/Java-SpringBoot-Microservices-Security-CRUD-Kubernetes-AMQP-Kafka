package com.example.clients.fraud;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudCheckResponse {
   public Boolean isFraudster;
}
