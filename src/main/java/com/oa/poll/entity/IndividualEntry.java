package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "individual_entries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = PollSubmission.class)
    @JoinColumn(name = "poll_submission_id", referencedColumnName = "id", nullable = false)
    PollSubmission pollSubmission;

    @Column(nullable = false)
    int percentage;

    @Column(name = "like_count", nullable = false)
    int likeCount;

    @Column(name = "dislike_count", nullable = false)
    int dislikeCount;
}
