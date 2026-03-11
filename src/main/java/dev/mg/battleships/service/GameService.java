package dev.mg.battleships.service;

import dev.mg.battleships.entity.*;
import dev.mg.battleships.repository.GameRepository;
import dev.mg.battleships.game.GameManager;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameManager gameManager;

    public GameService(GameRepository gameRepository, GameManager gameManager) {
        this.gameRepository = gameRepository;
        this.gameManager = gameManager;
    }

    public List<Game> findOpenGames() {
        return gameRepository.findByStatus(GameStatus.WAITING);
    }

    public Game createGame(User host) {

        Game game = new Game();

        game.setCode(generateGameCode());
        game.setStatus(GameStatus.WAITING);
        game.setHost(host);
        game.setCreatedAt(LocalDateTime.now());

        return gameRepository.save(game);
    }

    public Optional<Game> findByCode(String code) {
        return gameRepository.findByCode(code);
    }

    public Game joinGame(Game game, User guest) {


        if (game.getGuest() != null) {
            throw new RuntimeException("Game already has two players");
        }

        game.setGuest(guest);
        game.setStatus(GameStatus.SETUP);

        Game savedGame = gameRepository.save(game);

        System.out.println("Creating GameSession for game " + savedGame.getId());

        // Crear sesión del juego en memoria
        gameManager.createSession(
                savedGame.getId(),
                savedGame.getHost().getId(),
                guest.getId()
        );

        return savedGame;
    }

    private String generateGameCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }
}