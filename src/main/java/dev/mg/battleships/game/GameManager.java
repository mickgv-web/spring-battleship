package dev.mg.battleships.game;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

    private final Map<Long, GameSession> activeGames = new ConcurrentHashMap<>();

    public GameSession createSession(Long gameId, Long player1Id, Long player2Id) {

        GameSession session = new GameSession(gameId, player1Id, player2Id);

        System.out.println("Session created: " + gameId);

        activeGames.put(gameId, session);

        return session;
    }

    public GameSession getSession(Long gameId) {
        return activeGames.get(gameId);
    }

    public void removeSession(Long gameId) {
        activeGames.remove(gameId);
    }
}