package com.sctec.empreendimentos.dto;

import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;

import java.time.LocalDateTime;

public record EmpreendimentoResponseDTO(
        Long id,
        String nomeEmpreendimento,
        String nomeEmpreendedorResponsavel,
        String municipio,
        Segmento segmento,
        String contato,
        Status status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        String criadoPor
) {}