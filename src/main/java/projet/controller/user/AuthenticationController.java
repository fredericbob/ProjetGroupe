package projet.controller.user;

import projet.Configurations.JWTManager;
import projet.model.Authentication;
import projet.model.Utilisateur;

import projet.service.UtilisateurService;
import projet.Resultat.Resultat;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final UtilisateurService utilisateurService;

    private Map<String, String> tokens = new HashMap<>();
    @Autowired
    private JWTManager jwt;

    public AuthenticationController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<Resultat> signup(@RequestBody Utilisateur utilisateur) {

        try {
            System.out.println("1");
            Utilisateur savedUtilisateur = utilisateurService.save(utilisateur);
            return buildAuthResponse(savedUtilisateur.getEmail(), utilisateur.getPassword());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Resultat("Error occurred", e.getMessage(), ""));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Resultat> login(@RequestBody Authentication user) {
        try {
            return buildAuthResponse(user.getEmail(), user.getPassword());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Resultat("Authentication failed", e.getMessage(), null));
        }
    }

    private ResponseEntity<Resultat> buildAuthResponse(String email, String password) {
        Resultat res;
        try {
            Optional<Utilisateur> utilisateur = utilisateurService.findByEmail(email);
            if (utilisateur.isPresent()) {
                String token = utilisateurService.login(email, password);
                String refreshToken = jwt.generateRefreshToken(utilisateur.get());

                Map<String, String> tokens = new HashMap<>();
                tokens.put("token", token);
                tokens.put("refreshToken", refreshToken);

                res = new Resultat("OK", null, tokens);
                return ResponseEntity.ok(res);
            } else {
                throw new Exception("No user selected");
            }
        } catch (Exception e) {
            res = new Resultat("Error occurred", e.getMessage(), "");
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> tokenRequest) {
        String refreshToken = tokenRequest.get("refreshToken");
        Resultat res;

        try {
            if (jwt.isTokenExpired(refreshToken)) {
                res = new Resultat("Refresh token expired", "Refresh token is expired", null);
                return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
            }

            String email = jwt.getEmail(refreshToken);
            Optional<Utilisateur> utilisateur = utilisateurService.findByEmail(email);
            if (utilisateur.isPresent()) {
                String newToken = JWTManager.generateToken(utilisateur.get());
                String newRefreshToken = jwt.generateRefreshToken(utilisateur.get());

                tokens.put("token", newToken);
                tokens.put("refreshToken", newRefreshToken);

                res = new Resultat("OK", null, tokens);
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                throw new Exception("No user selected");
            }
        } catch (Exception e) {
            res = new Resultat("Refresh failed", e.getMessage(), null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/reset-attempts")
    public ResponseEntity<Resultat> reset(@RequestBody Map<String, String> request) {
        try {
            utilisateurService.resetEmail(request.get("email"));
            return ResponseEntity.ok(new Resultat("OK", null, "Les tentatives de connexion ont été réinitialisées."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Resultat("Error", e.getMessage(), null));
        }
    }

}
