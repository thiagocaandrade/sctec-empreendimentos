package com.sctec.empreendimentos.service;

import com.sctec.empreendimentos.domain.Empreendimento;
import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import com.sctec.empreendimentos.repository.EmpreendimentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpreendimentoServiceTest {

    @Mock
    private EmpreendimentoRepository repository;

    @InjectMocks
    private EmpreendimentoService service;

    @Test
    @DisplayName("listar sem filtros deve retornar todos os empreendimentos da página aplicada no mapper")
    void listar_quandoSemFiltros_retornaTodosOsEmpreendimentos() {
        Empreendimento e1 = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        Empreendimento e2 = criarEmpreendimento(2L, "Caucaia", Segmento.COMERCIO, Status.INATIVO);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Empreendimento> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<EmpreendimentoResponseDTO> result = service.listar(null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("id").containsExactly(1L, 2L);
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("listar com filtro de status deve retornar apenas empreendimentos com o status informado")
    void listar_quandoFiltroStatus_retornaSomenteStatusFiltrado() {
        Empreendimento e1 = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        Empreendimento e2 = criarEmpreendimento(2L, "Caucaia", Segmento.COMERCIO, Status.INATIVO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Empreendimento> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<EmpreendimentoResponseDTO> result = service.listar(Status.ATIVO, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(Status.ATIVO);
    }

    @Test
    @DisplayName("listar com filtro de segmento e município deve aplicar ambos filtros de forma case insensitive")
    void listar_quandoFiltroSegmentoEMunicipio_retornaFiltrado() {
        Empreendimento e1 = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        Empreendimento e2 = criarEmpreendimento(2L, "Caucaia", Segmento.SERVICOS, Status.ATIVO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Empreendimento> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<EmpreendimentoResponseDTO> result = service.listar(Status.ATIVO, Segmento.SERVICOS, "fort", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).municipio()).isEqualTo("Fortaleza");
    }

    @Test
    @DisplayName("listar deve ignorar filtro de município quando valor é nulo ou em branco")
    void listar_quandoMunicipioNuloOuEmBranco_ignoraFiltroMunicipio() {
        Empreendimento e1 = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        Empreendimento e2 = criarEmpreendimento(2L, "Caucaia", Segmento.SERVICOS, Status.ATIVO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Empreendimento> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<EmpreendimentoResponseDTO> resultMunicipioNulo = service.listar(Status.ATIVO, Segmento.SERVICOS, null, pageable);
        Page<EmpreendimentoResponseDTO> resultMunicipioVazio = service.listar(Status.ATIVO, Segmento.SERVICOS, "  ", pageable);

        assertThat(resultMunicipioNulo.getTotalElements()).isEqualTo(2);
        assertThat(resultMunicipioVazio.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("buscarPorId deve retornar DTO quando empreendimento existe")
    void buscarPorId_quandoExiste_retornaDto() {
        Empreendimento e = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        when(repository.findById(1L)).thenReturn(Optional.of(e));

        EmpreendimentoResponseDTO dto = service.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nomeEmpreendimento()).isEqualTo(e.getNomeEmpreendimento());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId deve lançar exceção quando empreendimento não existe")
    void buscarPorId_quandoNaoExiste_lancaExcecao() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Empreendimento nao encontrado");
    }

    @Test
    @DisplayName("criar deve definir status ATIVO quando não informado, preencher criadoPor e datas")
    void criar_quandoStatusNaoInformado_definePadroes() {
        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "Responsável",
                "Fortaleza",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                null
        );

        when(repository.save(any(Empreendimento.class))).thenAnswer(invocation -> {
            Empreendimento arg = invocation.getArgument(0);
            arg.setId(10L);
            return arg;
        });

        EmpreendimentoResponseDTO dtoCriado = service.criar(dto);

        ArgumentCaptor<Empreendimento> captor = ArgumentCaptor.forClass(Empreendimento.class);
        verify(repository).save(captor.capture());
        Empreendimento salvo = captor.getValue();

        assertThat(salvo.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(salvo.getCriadoPor()).isEqualTo("thiago.andrade");
        assertThat(salvo.getDataCriacao()).isNotNull();
        assertThat(salvo.getDataAtualizacao()).isNotNull();
        assertThat(salvo.getDataCriacao()).isEqualTo(salvo.getDataAtualizacao());

        assertThat(dtoCriado.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("criar deve manter status informado no DTO")
    void criar_quandoStatusInformado_mantemStatus() {
        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "Responsável",
                "Fortaleza",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                Status.INATIVO
        );

        when(repository.save(any(Empreendimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpreendimentoResponseDTO dtoCriado = service.criar(dto);

        assertThat(dtoCriado.status()).isEqualTo(Status.INATIVO);
    }

    @Test
    @DisplayName("atualizar deve aplicar dados do DTO, preservar status quando não informado e atualizar dataAtualizacao")
    void atualizar_quandoStatusNaoInformado_preservaStatusEAtualizaData() {
        Empreendimento existente = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        existente.setDataCriacao(LocalDateTime.now().minusDays(1));
        existente.setDataAtualizacao(LocalDateTime.now().minusDays(1));

        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Novo Nome",
                "Novo Responsável",
                "Caucaia",
                Segmento.COMERCIO,
                "(85) 97777-7777",
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Empreendimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime antesAtualizacao = existente.getDataAtualizacao();

        EmpreendimentoResponseDTO atualizado = service.atualizar(1L, dto);

        assertThat(atualizado.nomeEmpreendimento()).isEqualTo("Novo Nome");
        assertThat(atualizado.municipio()).isEqualTo("Caucaia");
        assertThat(atualizado.status()).isEqualTo(Status.ATIVO);
        assertThat(atualizado.dataAtualizacao()).isAfter(antesAtualizacao);
    }

    @Test
    @DisplayName("atualizar deve alterar status quando informado no DTO")
    void atualizar_quandoStatusInformado_alteraStatus() {
        Empreendimento existente = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO(
                "Empreendimento A",
                "Responsável",
                "Fortaleza",
                Segmento.SERVICOS,
                "(85) 99999-9999",
                Status.INATIVO
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Empreendimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpreendimentoResponseDTO atualizado = service.atualizar(1L, dto);

        assertThat(atualizado.status()).isEqualTo(Status.INATIVO);
    }

    @Test
    @DisplayName("atualizar deve lançar exceção quando empreendimento não existe")
    void atualizar_quandoNaoExiste_lancaExcecao() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(1L, Mockito.mock(EmpreendimentoRequestDTO.class)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Empreendimento nao encontrado");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deletar deve excluir empreendimento quando existe")
    void deletar_quandoExiste_exclui() {
        Empreendimento existente = criarEmpreendimento(1L, "Fortaleza", Segmento.SERVICOS, Status.ATIVO);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.deletar(1L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("deletar deve lançar exceção quando empreendimento não existe")
    void deletar_quandoNaoExiste_lancaExcecao() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Empreendimento nao encontrado");

        verify(repository, never()).delete(any());
    }

    private Empreendimento criarEmpreendimento(Long id, String municipio, Segmento segmento, Status status) {
        Empreendimento e = new Empreendimento();
        e.setId(id);
        e.setNomeEmpreendimento("Empreendimento " + id);
        e.setNomeEmpreendedorResponsavel("Responsável " + id);
        e.setMunicipio(municipio);
        e.setSegmento(segmento);
        e.setContato("(85) 90000-000" + id);
        e.setStatus(status);
        e.setDataCriacao(LocalDateTime.now().minusDays(2));
        e.setDataAtualizacao(LocalDateTime.now().minusDays(1));
        e.setCriadoPor("thiago.andrade");
        return e;
    }
}
