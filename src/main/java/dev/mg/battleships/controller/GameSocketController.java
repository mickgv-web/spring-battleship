package dev.mg.battleships.controller;

import dev.mg.battleships.dto.BoardRequest;
import dev.mg.battleships.dto.BoardStateEvent;
import dev.mg.battleships.dto.FireRequest;
import dev.mg.battleships.dto.FireResponse;
import dev.mg.battleships.entity.User;
import dev.mg.battleships.game.Cell;
import dev.mg.battleships.game.GameManager;
import dev.mg.battleships.game.GameSession;
import dev.mg.battleships.repository.UserRepository;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {

    private final GameManager gameManager;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameSocketController(
            GameManager gameManager,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.gameManager = gameManager;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/board")
    public void sendBoard(BoardRequest request, Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        GameSession session = gameManager.getSession(request.getGameId());

        if (session == null) {
            return;
        }

        BoardStateEvent board =
                new BoardStateEvent(
                        session.getBoardForPlayer(user.getId()).getGrid(),
                        session.getCurrentTurnPlayerId()
                );

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/board",
                board
        );
    }
    @MessageMapping("/fire")
    public void fire(FireRequest request, Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GameSession session = gameManager.getSession(request.getGameId());

        if (session == null) {
            throw new RuntimeException("Game session not found");
        }

        Cell result = session.fire(
                user.getId(),
                request.getX(),
                request.getY()
        );

        FireResponse response = new FireResponse();

        response.setX(request.getX());
        response.setY(request.getY());
        response.setResult(result);
        response.setShooterId(user.getId());
        response.setNextTurnPlayerId(session.getCurrentTurnPlayerId());

        response.setGameOver(session.isGameOver());

        if (session.isGameOver()) {
            response.setWinnerId(session.getWinner());
        }

        messagingTemplate.convertAndSend(
                "/topic/game/" + request.getGameId(),
                response
        );
    }
}