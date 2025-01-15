package com.oa.poll.entity;


import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "veggies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veggie {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    int id;

    @Column(nullable = false, unique = true)
    String name_et;

    @Column(nullable = false, unique = true)
    String name_en;

    @Column(name = "like_count")
    long likeCount;

    @Column(name = "dislike_count")
    long dislikeCount;
}
