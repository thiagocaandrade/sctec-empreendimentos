package com.sctec.empreendimentos.controller;

import com.sctec.empreendimentos.domain.enums.Segmento;
import com.sctec.empreendimentos.domain.enums.Status;
import com.sctec.empreendimentos.dto.EmpreendimentoRequestDTO;
import com.sctec.empreendimentos.dto.EmpreendimentoResponseDTO;
import com.sctec.empreendimentos.dto.PageResponse;
import com.sctec.empreendimentos.service.EmpreendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Empreendimentos", description = "Operações de CRUD de empreendimentos")
public class EmpreendimentoController {

    private final EmpreendimentoService empreendimentoService;

    @Autowired
    public EmpreendimentoController(EmpreendimentoService empreendimentoService) {
        this.empreendimentoService = empreendimentoService;
    }

    @GetMapping("/empreendimentos")
    @Operation(summary = "Listar empreendimentos", description = "Lista empreendimentos com filtros opcionais de status, segmento e município, utilizando paginação.")
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
    @Operation(summary = "Buscar empreendimento por ID", description = "Busca um empreendimento específico pelo seu identificador.")
    public ResponseEntity<EmpreendimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empreendimentoService.buscarPorId(id));
    }

    @PostMapping("/empreendimentos")
    @Operation(summary = "Criar empreendimento", description = "Cria um novo empreendimento com os dados informados.")
    public ResponseEntity<EmpreendimentoResponseDTO> criar(@Valid @RequestBody EmpreendimentoRequestDTO dto) {
        EmpreendimentoResponseDTO criado = empreendimentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/empreendimentos/{id}")
    @Operation(summary = "Atualizar empreendimento", description = "Atualiza os dados de um empreendimento existente.")
    public ResponseEntity<EmpreendimentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpreendimentoRequestDTO dto) {
        EmpreendimentoResponseDTO atualizado = empreendimentoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/empreendimentos/{id}")
    @Operation(summary = "Deletar empreendimento", description = "Remove um empreendimento existente pelo seu identificador.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empreendimentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
