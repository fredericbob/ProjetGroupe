package projet.service;

import projet.model.Role;
import projet.model.Utilisateur;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
// import java.util.stream.Collectors;

@Service
public class CustomUtilisateurService implements UserDetailsService {

    private final UtilisateurService utilisateurService;

    public CustomUtilisateurService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Use loadUserByEmail instead");
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = this.utilisateurService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(utilisateur.getEmail(), utilisateur.getPassword(), createAuth(utilisateur.getRole()));
    }

    // private List<GrantedAuthority> createAuth(List<Role> roles) {
    //     return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNom())).collect(Collectors.toList());
    // }

    private List<GrantedAuthority> createAuth(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getNom().toUpperCase()));
    }
}