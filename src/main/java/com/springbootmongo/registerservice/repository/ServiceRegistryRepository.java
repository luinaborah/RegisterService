package com.springbootmongo.registerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springbootmongo.registerservice.model.ServiceRegistry;

@Repository
public interface ServiceRegistryRepository extends MongoRepository<ServiceRegistry, String>{

}
