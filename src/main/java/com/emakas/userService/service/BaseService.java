package com.emakas.userService.service;

import com.emakas.userService.model.BaseEntity;
import com.emakas.userService.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public class BaseService<ENTITY extends BaseEntity,ID extends Serializable> {

    private final BaseRepository<ENTITY,ID> baseRepository;

    @Autowired
    public BaseService(BaseRepository<ENTITY,ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<ENTITY> getAll(){
        return baseRepository.findAll();
    }

    public ENTITY getById(ID id){
        return baseRepository.findById(id).orElse(null);
    }

    public void save(ENTITY entity){
        baseRepository.save(entity);
    }

    public void delete(ENTITY entity){
        baseRepository.delete(entity);
    }
}
