package dev.mg.battleships.repository;

import dev.mg.battleships.entity.Game;
import dev.mg.battleships.entity.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByCode(String code);

    List<Game> findByStatus(GameStatus status);

}