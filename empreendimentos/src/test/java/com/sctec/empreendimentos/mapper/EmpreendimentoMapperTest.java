package com.sctec.empreendimentos.mapper;

import com.sctec.empreendimentos.domain.Empreendimento;
import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EmpreendimentoMapperTest {

    @Test
    @DisplayName("toEntity deve mapear todos os campos do DTO para a entidade")
    void toEntity_quandoDtoValido_mapeiaCorretamente() {
        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "João da Silva",
                "Fortaleza",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                Status.ATIVO
        );

        Empreendimento entity = EmpreendimentoMapper.toEntity(dto);

        assertThat(entity.getNomeEmpreendimento()).isEqualTo(dto.nomeEmpreendimento());
        assertThat(entity.getNomeEmpreendedorResponsavel()).isEqualTo(dto.nomeEmpreendedorResponsavel());
        assertThat(entity.getMunicipio()).isEqualTo(dto.municipio());
        assertThat(entity.getSegmento()).isEqualTo(dto.segmento());
        assertThat(entity.getContato()).isEqualTo(dto.contato());
        assertThat(entity.getStatus()).isEqualTo(dto.status());
    }

    @Test
    @DisplayName("updateEntity deve atualizar campos básicos e manter status quando não informado")
    void updateEntity_quandoStatusNulo_mantemStatusExistente() {
        Empreendimento entity = new Empreendimento();
        entity.setNomeEmpreendimento("Antigo");
        entity.setNomeEmpreendedorResponsavel("Responsável Antigo");
        entity.setMunicipio("Fortaleza");
        entity.setSegmento(Segmento.COMERCIO);
        entity.setContato("(85) 98888-8888");
        entity.setStatus(Status.ATIVO);

        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Novo Nome",
                "Novo Responsável",
                "Caucaia",
                Segmento.SERVICOS,
                "(85) 97777-7777",
                null
        );

        EmpreendimentoMapper.updateEntity(entity, dto);

        assertThat(entity.getNomeEmpreendimento()).isEqualTo(dto.nomeEmpreendimento());
        assertThat(entity.getNomeEmpreendedorResponsavel()).isEqualTo(dto.nomeEmpreendedorResponsavel());
        assertThat(entity.getMunicipio()).isEqualTo(dto.municipio());
        assertThat(entity.getSegmento()).isEqualTo(dto.segmento());
        assertThat(entity.getContato()).isEqualTo(dto.contato());
        assertThat(entity.getStatus()).isEqualTo(Status.ATIVO);
    }

    @Test
    @DisplayName("updateEntity deve alterar o status quando informado no DTO")
    void updateEntity_quandoStatusInformado_alteraStatus() {
        Empreendimento entity = new Empreendimento();
        entity.setStatus(Status.ATIVO);

        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "Responsável",
                "Fortaleza",
                Segmento.INDUSTRIA,
                "(85) 90000-0000",
                Status.INATIVO
        );

        EmpreendimentoMapper.updateEntity(entity, dto);

        assertThat(entity.getStatus()).isEqualTo(Status.INATIVO);
    }

    @Test
    @DisplayName("toResponseDTO deve mapear todos os campos da entidade para o DTO de resposta")
    void toResponseDTO_quandoEntidadeValida_mapeiaCorretamente() {
        Empreendimento entity = new Empreendimento();
        entity.setId(1L);
        entity.setNomeEmpreendimento("Empreendimento A");
        entity.setNomeEmpreendedorResponsavel("João da Silva");
        entity.setMunicipio("Fortaleza");
        entity.setSegmento(Segmento.SERVICOS);
        entity.setContato("(85) 99999-9999");
        entity.setStatus(Status.ATIVO);
        LocalDateTime agora = LocalDateTime.now();
        entity.setDataCriacao(agora.minusDays(1));
        entity.setDataAtualizacao(agora);
        entity.setCriadoPor("thiago.andrade");

        EmpreendimentoResponseDTO dto = EmpreendimentoMapper.toResponseDTO(entity);

        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.nomeEmpreendimento()).isEqualTo(entity.getNomeEmpreendimento());
        assertThat(dto.nomeEmpreendedorResponsavel()).isEqualTo(entity.getNomeEmpreendedorResponsavel());
        assertThat(dto.municipio()).isEqualTo(entity.getMunicipio());
        assertThat(dto.segmento()).isEqualTo(entity.getSegmento());
        assertThat(dto.contato()).isEqualTo(entity.getContato());
        assertThat(dto.status()).isEqualTo(entity.getStatus());
        assertThat(dto.dataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(dto.dataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
        assertThat(dto.criadoPor()).isEqualTo(entity.getCriadoPor());
    }
}
