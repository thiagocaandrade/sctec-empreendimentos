package com.sctec.empreendimentos.dto;

import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmpreendimentoRequestDTO(
        @NotBlank String nomeEmpreendimento,
        @NotBlank String nomeEmpreendedorResponsavel,
        @NotBlank String municipio,
        @NotNull Segmento segmento,
        @NotBlank String contato,
        Status status
) {}
