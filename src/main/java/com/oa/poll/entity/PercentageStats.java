package com.oa.poll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "percentage_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PercentageStats {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;

        @Column(nullable = false)
        int min;

        @Column(nullable = false)
        int max;

        @Column(name = "entry_count", nullable = false)
        long entryCount;

        @Column(name = "running_total", nullable = false)
        long runningTotal;

        @Column(nullable = false)
        int average;
}
