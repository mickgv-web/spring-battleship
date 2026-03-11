package dev.mg.battleships.controller;

import dev.mg.battleships.dto.FireRequest;
import dev.mg.battleships.dto.FireResponse;
import dev.mg.battleships.game.Cell;
import dev.mg.battleships.game.GameManager;
import dev.mg.battleships.game.GameSession;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {

    private final GameManager gameManager;

    public GameSocketController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @MessageMapping("/fire")
    @SendTo("/topic/game")
    public FireResponse fire(FireRequest request) {

        GameSession session = gameManager.getSession(request.getGameId());

        Cell result = session.fire(
                session.getCurrentTurnPlayerId(),
                request.getX(),
                request.getY()
        );

        FireResponse response = new FireResponse();

        response.setX(request.getX());
        response.setY(request.getY());
        response.setResult(result);
        response.setNextTurnPlayerId(session.getCurrentTurnPlayerId());

        return response;
    }
}