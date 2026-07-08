package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/info")
public class InfoController {
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping("/nosotros")
    public String nosotros(Model model, HttpSession session) {
        model.addAttribute("contenido", "info/nosotros");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @GetMapping("/contacto")
    public String contacto(Model model, HttpSession session) {
        model.addAttribute("contenido", "info/contacto");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        int size = (carrito != null) ? carrito.size() : 0;
        model.addAttribute("carritoSize", size);
    }
}