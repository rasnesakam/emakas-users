package com.emakas.userService.service;

import com.emakas.userService.entity.Entity;
import com.emakas.userService.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EntityService<E extends Entity, ID> {

    private final EntityRepository<E, ID> entityRepository;

    @Autowired
    public EntityService(EntityRepository<E, ID> entityRepository) {
        this.entityRepository = entityRepository;
    }

    public List<E> getAll(){
        return entityRepository.findAll();
    }

    public E getById(ID id){
        return entityRepository.findById(id).orElse(null);
    }

    public void save(E entity){
        entityRepository.save(entity);
    }

    public void delete(E entity){
        entityRepository.delete(entity);
    }
}
