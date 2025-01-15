package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "poll_submissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PollSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String email;
}
