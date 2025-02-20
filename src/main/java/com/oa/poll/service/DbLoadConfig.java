package com.oa.poll.service;

import com.oa.poll.entity.Veggie;
import com.oa.poll.exceptions.DbConfigException;
import com.oa.poll.repository.VeggieRepo;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Component;

import java.util.List;

@DependsOnDatabaseInitialization
@Component
public class DbLoadConfig implements IDbLoadConfig {
    private List<Veggie> veggies = null;
    private List<Integer> veggieKeys = null;

    private final VeggieRepo veggieRepo;

    public DbLoadConfig(VeggieRepo veggieRepo) {
        this.veggieRepo = veggieRepo;

        veggies = loadVeggies();
        veggieKeys = loadVeggieKeys();
    }

    List<Veggie> loadVeggies() {
        List<Veggie> veggies = veggieRepo.findAll();
        if (veggies.isEmpty()) {
            throw new DbConfigException("DbLoadConfig.loadVeggies(): No veggies defined. Probably a bad DB " +
                    "configuration. Check data.sql");
        }
       return veggies;
    }

    List<Integer> loadVeggieKeys() {
        return veggies.stream().map(Veggie::getId).toList();
    }

    @Override
    public List<Veggie> getVeggies() {
        return veggies;
    }

    @Override
    public List<Integer> getVeggieKeys() {
        return veggieKeys;
    }
}
