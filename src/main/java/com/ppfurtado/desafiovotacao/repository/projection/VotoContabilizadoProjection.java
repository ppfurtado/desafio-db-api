package com.ppfurtado.desafiovotacao.repository.projection;

import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;

public interface VotoContabilizadoProjection {
    OpcaoVoto getOpcao();
    Long getTotal();
}
