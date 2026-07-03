package br.com.ada.mscomprovantes.repository;

import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComprovanteRepository extends JpaRepository<Comprovante, UUID> {
}
