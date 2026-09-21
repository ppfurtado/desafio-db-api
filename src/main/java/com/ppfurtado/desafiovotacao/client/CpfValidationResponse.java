package com.ppfurtado.desafiovotacao.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpfValidationResponse {
    private CpfValidationStatus status;
}
