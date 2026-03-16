package com.sctec.empreendimentos.repository;

import com.sctec.empreendimentos.domain.Empreendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpreendimentoRepository extends JpaRepository<Empreendimento, Long> {
}
