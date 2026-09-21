package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.ppfurtado.desafiovotacao.client.CpfValidationFacade;
import com.ppfurtado.desafiovotacao.client.CpfValidationResponse;
import com.ppfurtado.desafiovotacao.client.CpfValidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CpfValidationControllerTest {

    @Mock
    private CpfValidationFacade cpfValidationFacade;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CpfValidationController(cpfValidationFacade)).build();
    }

    @Test
    void validarCpf_shouldReturnStatusForVersionedEndpoint() throws Exception {
        CpfValidationResponse response = CpfValidationResponse.builder().status(CpfValidationStatus.ABLE_TO_VOTE).build();

        when(cpfValidationFacade.validateCpf("12345678909")).thenReturn(response);

        mockMvc.perform(get("/v1/users/12345678909"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABLE_TO_VOTE"));

        verify(cpfValidationFacade).validateCpf("12345678909");
    }

    @Test
    void validarCpf_shouldReturnStatusForLegacyEndpoint() throws Exception {
        CpfValidationResponse response = CpfValidationResponse.builder().status(CpfValidationStatus.UNABLE_TO_VOTE).build();

        when(cpfValidationFacade.validateCpf("98765432100")).thenReturn(response);

        mockMvc.perform(get("/users/98765432100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNABLE_TO_VOTE"));

        verify(cpfValidationFacade).validateCpf("98765432100");
    }
}
