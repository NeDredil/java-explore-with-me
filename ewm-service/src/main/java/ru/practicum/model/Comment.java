package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text", length = 1000)
    private String text;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User creator;

    @JoinColumn(name = "event_id")
    @ManyToOne
    private Event event;

    @Column(name = "created")
    private LocalDateTime created;

}
