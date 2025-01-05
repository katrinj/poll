package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "poll_submissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PollSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String email;
}
