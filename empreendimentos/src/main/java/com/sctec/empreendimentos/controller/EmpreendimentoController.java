package com.sctec.empreendimentos.controller;

import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import com.sctec.empreendimentos.dto.PageResponse;
import com.sctec.empreendimentos.service.EmpreendimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmpreendimentoController {

    private final EmpreendimentoService empreendimentoService;

    @Autowired
    public EmpreendimentoController(EmpreendimentoService empreendimentoService) {
        this.empreendimentoService = empreendimentoService;
    }

    @GetMapping("/empreendimentos")
    public PageResponse<EmpreendimentoResponseDTO> listar(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Segmento segmento,
            @RequestParam(required = false) String municipio,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Pageable pageableOrdenadoPorId = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<EmpreendimentoResponseDTO> page = empreendimentoService.listar(status, segmento, municipio, pageableOrdenadoPorId);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @GetMapping("/empreendimentos/{id}")
    public ResponseEntity<EmpreendimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empreendimentoService.buscarPorId(id));
    }

    @PostMapping("/empreendimentos")
    public ResponseEntity<EmpreendimentoResponseDTO> criar(@Valid @RequestBody EmpreendimentoRequestDTO dto) {
        EmpreendimentoResponseDTO criado = empreendimentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/empreendimentos/{id}")
    public ResponseEntity<EmpreendimentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpreendimentoRequestDTO dto) {
        EmpreendimentoResponseDTO atualizado = empreendimentoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/empreendimentos/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empreendimentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
