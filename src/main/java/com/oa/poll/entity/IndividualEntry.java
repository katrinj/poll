package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "individual_entries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class IndividualEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = PersonalData.class)
    @JoinColumn(name = "personal_data_id", referencedColumnName = "id", nullable = false)
    PersonalData personalData;

    @Column(nullable = false)
    int percentage;

    @Column(name = "like_count", nullable = false)
    int likeCount;

    @Column(name = "dislike_count", nullable = false)
    int dislikeCount;
}
