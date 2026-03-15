package com.sctec.empreendimentos.mapper;

import com.sctec.empreendimentos.domain.Empreendimento;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;

public class EmpreendimentoMapper {

    public static Empreendimento toEntity(EmpreendimentoRequestDTO dto) {
        Empreendimento e = new Empreendimento();
        e.setNomeEmpreendimento(dto.nomeEmpreendimento());
        e.setNomeEmpreendedorResponsavel(dto.nomeEmpreendedorResponsavel());
        e.setMunicipio(dto.municipio());
        e.setSegmento(dto.segmento());
        e.setContato(dto.contato());
        e.setStatus(dto.status());
        return e;
    }

    public static void updateEntity(Empreendimento e, EmpreendimentoRequestDTO dto) {
        e.setNomeEmpreendimento(dto.nomeEmpreendimento());
        e.setNomeEmpreendedorResponsavel(dto.nomeEmpreendedorResponsavel());
        e.setMunicipio(dto.municipio());
        e.setSegmento(dto.segmento());
        e.setContato(dto.contato());
        if (dto.status() != null) {
            e.setStatus(dto.status());
        }
    }

    public static EmpreendimentoResponseDTO toResponseDTO(Empreendimento e) {
        return new EmpreendimentoResponseDTO(
                e.getId(),
                e.getNomeEmpreendimento(),
                e.getNomeEmpreendedorResponsavel(),
                e.getMunicipio(),
                e.getSegmento(),
                e.getContato(),
                e.getStatus(),
                e.getDataCriacao(),
                e.getDataAtualizacao(),
                e.getCriadoPor()
        );
    }
}