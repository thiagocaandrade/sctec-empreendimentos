package com.sctec.empreendimentos.controller;

import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import com.sctec.empreendimentos.dto.PageResponse;
import com.sctec.empreendimentos.service.EmpreendimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class EmpreendimentoControllerTest {

    @InjectMocks
    private EmpreendimentoController controller;

    @Mock
    private EmpreendimentoService service;

    @Test
    @DisplayName("listar deve delegar ao service e retornar PageResponse com filtros aplicados")
    void listar_quandoFiltrosValidos_retornaPageResponseComEmpreendimentosFiltrados() {
        Pageable pageableEntrada = PageRequest.of(0, 5);
        Pageable pageableOrdenado = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

        EmpreendimentoResponseDTO dto1 = criarResponseDTO(1L, "Fortaleza");
        EmpreendimentoResponseDTO dto2 = criarResponseDTO(2L, "Caucaia");
        Page<EmpreendimentoResponseDTO> page = new PageImpl<>(List.of(dto1, dto2), pageableOrdenado, 2);

        Mockito.when(service.listar(Status.ATIVO, Segmento.SERVICOS, "fort", pageableOrdenado))
                .thenReturn(page);

        PageResponse<EmpreendimentoResponseDTO> response = controller.listar(
                Status.ATIVO,
                Segmento.SERVICOS,
                "fort",
                pageableEntrada
        );

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).id()).isEqualTo(1L);
        assertThat(response.content().get(1).id()).isEqualTo(2L);
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("buscarPorId deve delegar ao service e retornar ResponseEntity 200 com body correto")
    void buscarPorId_quandoEmpreendimentoExiste_retornaEmpreendimentoComStatusOk() {
        EmpreendimentoResponseDTO dto = criarResponseDTO(1L, "Fortaleza");
        Mockito.when(service.buscarPorId(1L)).thenReturn(dto);

        ResponseEntity<EmpreendimentoResponseDTO> response = controller.buscarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().municipio()).isEqualTo("Fortaleza");
    }

    @Test
    @DisplayName("criar deve delegar ao service e retornar ResponseEntity 201 com body do empreendimento criado")
    void criar_quandoRequestValido_retornaEmpreendimentoCriadoComStatusCreated() {
        EmpreendimentoRequestDTO requestDTO = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "Responsável",
                "Fortaleza",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                null
        );

        EmpreendimentoResponseDTO responseDTO = criarResponseDTO(1L, "Fortaleza");
        Mockito.when(service.criar(any(EmpreendimentoRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<EmpreendimentoResponseDTO> response = controller.criar(requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().municipio()).isEqualTo("Fortaleza");
    }

    @Test
    @DisplayName("atualizar deve delegar ao service e retornar ResponseEntity 200 com body do empreendimento atualizado")
    void atualizar_quandoRequestValido_retornaEmpreendimentoAtualizadoComStatusOk() {
        EmpreendimentoRequestDTO requestDTO = new EmpreendimentoRequestDTO(
                "Empreendimento Atualizado",
                "Responsável",
                "Caucaia",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                Status.ATIVO
        );

        EmpreendimentoResponseDTO responseDTO = criarResponseDTO(1L, "Caucaia");
        Mockito.when(service.atualizar(eq(1L), any(EmpreendimentoRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<EmpreendimentoResponseDTO> response = controller.atualizar(1L, requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().municipio()).isEqualTo("Caucaia");
    }

    @Test
    @DisplayName("deletar deve delegar ao service e retornar ResponseEntity 204")
    void deletar_quandoEmpreendimentoExiste_retornaStatusNoContentEInvocaServiceDeletar() {
        ResponseEntity<Void> response = controller.deletar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Mockito.verify(service).deletar(1L);
    }

    private EmpreendimentoResponseDTO criarResponseDTO(Long id, String municipio) {
        LocalDateTime agora = LocalDateTime.now();
        return new EmpreendimentoResponseDTO(
                id,
                "Empreendimento " + id,
                "Responsável " + id,
                municipio,
                Segmento.SERVICOS,
                "(85) 99999-000" + id,
                Status.ATIVO,
                agora.minusDays(1),
                agora,
                "thiago.andrade"
        );
    }
}
