package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personal_data")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PersonalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String email;
}
