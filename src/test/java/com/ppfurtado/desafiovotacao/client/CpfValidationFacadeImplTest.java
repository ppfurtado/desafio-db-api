package com.ppfurtado.desafiovotacao.client;

import com.ppfurtado.desafiovotacao.domain.exception.CpfInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class CpfValidationFacadeImplTest {

    @Test
    void validateCpf_invalid_throws() {
        CpfValidationFacadeImpl impl = new CpfValidationFacadeImpl();
        assertThrows(CpfInvalidoException.class, () -> impl.validateCpf("123"));
    }

    @Test
    void isAbleToVote_delegatesToValidateCpf() {
        CpfValidationFacadeImpl impl = spy(new CpfValidationFacadeImpl());
        CpfValidationResponse resp = CpfValidationResponse.builder().status(CpfValidationStatus.ABLE_TO_VOTE).build();
        try {
            doReturn(resp).when(impl).validateCpf("52998224725");
        } catch (Exception e) {
            fail(e);
        }

        boolean result = impl.isAbleToVote("52998224725");
        assertTrue(result);

        CpfValidationResponse resp2 = CpfValidationResponse.builder().status(CpfValidationStatus.UNABLE_TO_VOTE).build();
        try {
            doReturn(resp2).when(impl).validateCpf("98765432100");
        } catch (Exception e) {
            fail(e);
        }

        boolean result2 = impl.isAbleToVote("98765432100");
        assertFalse(result2);
    }
}
