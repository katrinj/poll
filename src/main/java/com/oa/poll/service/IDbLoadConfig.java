package com.oa.poll.service;

import com.oa.poll.entity.Veggie;

import java.util.List;

public interface IDbLoadConfig {
    List<Veggie> getVeggies();

    List<Integer> getVeggieKeys();
}
