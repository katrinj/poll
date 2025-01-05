package com.oa.poll.repository;

import com.oa.poll.entity.Veggie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeggieRepo extends JpaRepository<Veggie, Long> {
    List<Veggie> findAllByOrderByLikeCountDesc();

    List<Veggie> findAllByOrderByDislikeCountDesc();

    Veggie findByName(String name);
}
