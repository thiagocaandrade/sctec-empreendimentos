package com.sctec.empreendimentos.service;

import com.sctec.empreendimentos.domain.Empreendimento;
import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import com.sctec.empreendimentos.mapper.EmpreendimentoMapper;
import com.sctec.empreendimentos.repository.EmpreendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpreendimentoService {

    private final EmpreendimentoRepository repository;

    @Autowired
    public EmpreendimentoService(EmpreendimentoRepository repository) {
        this.repository = repository;
    }

    public Page<EmpreendimentoResponseDTO> listar(Status status, Segmento segmento, String municipio, Pageable pageable) {
        Page<Empreendimento> page = repository.findAll(pageable);

        List<Empreendimento> empreendimentosFiltrados = page.getContent().stream()
                .filter(e -> status == null || e.getStatus() == status)
                .filter(e -> segmento == null || e.getSegmento() == segmento)
                .filter(e -> {
                    if (municipio == null || municipio.isBlank()) return true;
                    return e.getMunicipio() != null
                            && e.getMunicipio().toLowerCase().contains(municipio.toLowerCase());
                })
                .toList();

        List<EmpreendimentoResponseDTO> content = empreendimentosFiltrados.stream()
                .map(EmpreendimentoMapper::toResponseDTO)
                .toList();

        return new PageImpl<>(content, pageable, empreendimentosFiltrados.size());
    }

    public EmpreendimentoResponseDTO buscarPorId(Long id) {
        Empreendimento e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empreendimento nao encontrado"));
        return EmpreendimentoMapper.toResponseDTO(e);
    }

    public EmpreendimentoResponseDTO criar(EmpreendimentoRequestDTO dto) {
        Empreendimento e = EmpreendimentoMapper.toEntity(dto);
        if (e.getStatus() == null) {
            e.setStatus(Status.ATIVO);
        }

        e.setCriadoPor("thiago.andrade");
        LocalDateTime now = LocalDateTime.now();
        e.setDataCriacao(now);
        e.setDataAtualizacao(now);
        Empreendimento salvo = repository.save(e);
        return EmpreendimentoMapper.toResponseDTO(salvo);
    }

    public EmpreendimentoResponseDTO atualizar(Long id, EmpreendimentoRequestDTO dto) {
        Empreendimento e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empreendimento nao encontrado"));
        EmpreendimentoMapper.updateEntity(e, dto);
        e.setDataAtualizacao(LocalDateTime.now());
        Empreendimento salvo = repository.save(e);
        return EmpreendimentoMapper.toResponseDTO(salvo);
    }

    public void deletar(Long id) {
        Empreendimento e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empreendimento nao encontrado"));
        repository.delete(e);
    }
}
