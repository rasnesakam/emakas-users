package com.emakas.userService.service;

import com.emakas.userService.model.BaseEntity;
import com.emakas.userService.repository.CoreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public class CoreService<ENTITY extends BaseEntity,ID extends Serializable> {

    private final CoreRepository<ENTITY,ID> coreRepository;

    @Autowired
    public CoreService(CoreRepository<ENTITY,ID> coreRepository) {
        this.coreRepository = coreRepository;
    }

    public List<ENTITY> getAll(){
        return coreRepository.findAll();
    }

    public ENTITY getById(ID id){
        return coreRepository.findById(id).orElse(null);
    }

    public ENTITY save(ENTITY entity){
        return coreRepository.save(entity);
    }

    public void delete(ENTITY entity){
        coreRepository.delete(entity);
    }
}
