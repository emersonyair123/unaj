package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import episs.unaj.com.proyecto1.entity.Usuario;
import episs.unaj.com.proyecto1.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        model.addAttribute("contenido", "auth/login");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, 
                                @RequestParam String password,
                                RedirectAttributes redirect) {
        var usuarioOpt = usuarioService.buscarPorEmail(email);
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            redirect.addFlashAttribute("exito", "¡Bienvenido " + usuarioOpt.get().getNombre() + "!");
            return "redirect:/";
        } else {
            redirect.addFlashAttribute("error", "Correo o contraseña incorrectos");
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/registro")
    public String registro(Model model, HttpSession session) {
        model.addAttribute("contenido", "auth/registro");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String confirmar,
                                   RedirectAttributes redirect) {
        if (!password.equals(confirmar)) {
            redirect.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/auth/registro";
        }
        
        if (password.length() < 8) {
            redirect.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "redirect:/auth/registro";
        }
        
        if (usuarioService.existePorEmail(email)) {
            redirect.addFlashAttribute("error", "Este correo electrónico ya está registrado");
            return "redirect:/auth/registro";
        }
        
        Usuario usuario = new Usuario(nombre, email, password);
        usuarioService.guardar(usuario);
        
        redirect.addFlashAttribute("exito", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/auth/login";
    }
    
    @GetMapping("/recuperar")
    public String recuperarContrasena(Model model, HttpSession session) {
        model.addAttribute("contenido", "auth/recuperar");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        int size = (carrito != null) ? carrito.size() : 0;
        model.addAttribute("carritoSize", size);
    }
}