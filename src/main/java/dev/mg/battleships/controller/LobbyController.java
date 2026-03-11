package dev.mg.battleships.controller;

import dev.mg.battleships.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyController {

    private final GameService gameService;

    public LobbyController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/lobby")
    public String lobby(Model model) {

        model.addAttribute("games", gameService.findOpenGames());

        return "lobby";
    }

    @GetMapping("/ws-test")
    public String wsTest() {
        return "ws-test";
    }
}