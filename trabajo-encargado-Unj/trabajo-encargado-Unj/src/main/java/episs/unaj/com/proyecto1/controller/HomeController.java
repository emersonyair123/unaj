package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import episs.unaj.com.proyecto1.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ProductoService productoService;
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        model.addAttribute("productosDestacados", productoService.listarTodos());
        model.addAttribute("contenido", "inicio/inicio");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String q, Model model, HttpSession session) {
        if (q != null && !q.isEmpty()) {
            model.addAttribute("resultados", productoService.buscarPorNombre(q));
            model.addAttribute("termino", q);
        }
        model.addAttribute("productosDestacados", productoService.listarTodos());
        model.addAttribute("contenido", "inicio/inicio");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        int size = (carrito != null) ? carrito.size() : 0;
        model.addAttribute("carritoSize", size);
    }
}