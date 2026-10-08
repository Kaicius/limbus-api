package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Sinner;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.SinnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SinnerService {

    private final SinnerRepository sinnerRepository;

    public SinnerService(SinnerRepository sinnerRepository){
        this.sinnerRepository = sinnerRepository;
    }

    public Page<Sinner> listar(Pageable pageable){
        return sinnerRepository.findAll(pageable);
    }

    public Page<Sinner> buscarPorNome(String nome, Pageable pageable){
        return sinnerRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Sinner buscarPorId(Long id){
        return sinnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinner", id));
    }

    public Sinner criar(Sinner sinner){
        return sinnerRepository.save(sinner);
    }

    public Sinner atualizar(Long id, Sinner sinnerAtualizado){
        Sinner sinner = buscarPorId(id);
        sinner.setNome(sinnerAtualizado.getNome());
        return sinnerRepository.save(sinner);
    }

    public void deletar(Long id){
        Sinner sinner = buscarPorId(id);
        sinnerRepository.delete(sinner);
    }
}
