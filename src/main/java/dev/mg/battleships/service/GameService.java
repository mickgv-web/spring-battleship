package dev.mg.battleships.service;

import dev.mg.battleships.dto.BoardStateEvent;
import dev.mg.battleships.entity.*;
import dev.mg.battleships.game.Cell;
import dev.mg.battleships.game.GameSession;
import dev.mg.battleships.repository.GameRepository;
import dev.mg.battleships.game.GameManager;
import dev.mg.battleships.dto.GameEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameManager gameManager;
    private final SimpMessagingTemplate messagingTemplate;

    public GameService(
            GameRepository gameRepository,
            GameManager gameManager,
            SimpMessagingTemplate messagingTemplate) {

        this.gameRepository = gameRepository;
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
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
            throw new RuntimeException("Game already full");
        }

        game.setGuest(guest);
        game.setStatus(GameStatus.SETUP);

        Game savedGame = gameRepository.save(game);

        GameSession session = gameManager.createSession(
                savedGame.getId(),
                savedGame.getHost().getId(),
                guest.getId()
        );

        BoardStateEvent hostBoard =
                new BoardStateEvent(
                        session.getBoardForPlayer(savedGame.getHost().getId()).getGrid(),
                        session.getCurrentTurnPlayerId()
                );

        BoardStateEvent guestBoard =
                new BoardStateEvent(
                        session.getBoardForPlayer(guest.getId()).getGrid(),
                        session.getCurrentTurnPlayerId()
                );

        messagingTemplate.convertAndSendToUser(
                savedGame.getHost().getUsername(),
                "/queue/board",
                hostBoard
        );

        messagingTemplate.convertAndSendToUser(
                guest.getUsername(),
                "/queue/board",
                guestBoard
        );

        messagingTemplate.convertAndSend(
                "/topic/game/" + savedGame.getId(),
                new GameEvent("PLAYER_JOINED")
        );

        System.out.println("HOST BOARD:");
        Cell[][] hostGrid = session.getBoardForPlayer(savedGame.getHost().getId()).getGrid();

        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                System.out.print(hostGrid[i][j] + " ");
            }
            System.out.println();
        }

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