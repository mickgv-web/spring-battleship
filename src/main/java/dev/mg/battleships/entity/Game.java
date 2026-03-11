package dev.mg.battleships.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne
    private User host;

    @ManyToOne
    private User guest;

    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;

    @ManyToOne
    private User winner;

}