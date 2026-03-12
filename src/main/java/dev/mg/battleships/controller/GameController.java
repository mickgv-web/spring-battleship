package dev.mg.battleships.controller;

import dev.mg.battleships.entity.Game;
import dev.mg.battleships.entity.User;
import dev.mg.battleships.repository.UserRepository;
import dev.mg.battleships.service.GameService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final UserRepository userRepository;

    public GameController(GameService gameService, UserRepository userRepository) {
        this.gameService = gameService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public String createGame(Authentication authentication) {

        String username = authentication.getName();

        User host = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = gameService.createGame(host);

        return "redirect:/games/" + game.getCode();
    }

    @PostMapping("/join")
    public String joinGame(@RequestParam String code,
                           Authentication authentication) {

        String username = authentication.getName();

        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = gameService.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        gameService.joinGame(game, guest);

        return "redirect:/games/" + game.getCode();
    }

    @GetMapping("/{code}")
    public String game(@PathVariable String code,
                       Principal principal,
                       Model model) {

        Game game = gameService.findByCode(code)
                .orElseThrow();

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();

        model.addAttribute("game", game);
        model.addAttribute("playerId", user.getId());

        return "game";
    }
}