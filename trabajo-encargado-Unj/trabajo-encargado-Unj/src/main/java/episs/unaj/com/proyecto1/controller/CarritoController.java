package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import episs.unaj.com.proyecto1.entity.Producto;
import episs.unaj.com.proyecto1.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @Autowired
    private ProductoService productoService;
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping
    public String verCarrito(Model model, HttpSession session) {
        List<CarritoItem> carrito = getCarritoFromSession(session);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));
        model.addAttribute("contenido", "carrito/carrito");  // Cambiado de "carrito/ver" a "carrito/carrito"
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer productoId,
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   HttpSession session,
                                   RedirectAttributes redirect) {
        Optional<Producto> productoOpt = productoService.buscarPorId(productoId);
        
        if (productoOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }
        
        Producto producto = productoOpt.get();
        List<CarritoItem> carrito = getCarritoFromSession(session);
        
        boolean encontrado = false;
        for (CarritoItem item : carrito) {
            if (item.getProducto().getId().equals(productoId)) {
                item.setCantidad(item.getCantidad() + cantidad);
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            carrito.add(new CarritoItem(producto, cantidad));
        }
        
        session.setAttribute(CARRITO_SESSION, carrito);
        redirect.addFlashAttribute("exito", "Producto agregado al carrito");
        return "redirect:/carrito";
    }
    
    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Integer productoId,
                                     @RequestParam Integer cantidad,
                                     HttpSession session) {
        List<CarritoItem> carrito = getCarritoFromSession(session);
        
        for (CarritoItem item : carrito) {
            if (item.getProducto().getId().equals(productoId)) {
                if (cantidad <= 0) {
                    carrito.remove(item);
                } else {
                    item.setCantidad(cantidad);
                }
                break;
            }
        }
        
        session.setAttribute(CARRITO_SESSION, carrito);
        return "redirect:/carrito";
    }
    
    @GetMapping("/eliminar/{productoId}")
    public String eliminarDelCarrito(@PathVariable Integer productoId,
                                     HttpSession session) {
        List<CarritoItem> carrito = getCarritoFromSession(session);
        carrito.removeIf(item -> item.getProducto().getId().equals(productoId));
        session.setAttribute(CARRITO_SESSION, carrito);
        return "redirect:/carrito";
    }
    
    @GetMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute(CARRITO_SESSION);
        return "redirect:/carrito";
    }
    
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        List<CarritoItem> carrito = getCarritoFromSession(session);
        if (carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));
        model.addAttribute("contenido", "carrito/checkout");  // Ya está bien
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/checkout")
    public String procesarCheckout(HttpSession session, RedirectAttributes redirect) {
        session.removeAttribute(CARRITO_SESSION);
        redirect.addFlashAttribute("exito", "¡Compra realizada con éxito!");
        return "redirect:/";
    }
    
    @SuppressWarnings("unchecked")
    private List<CarritoItem> getCarritoFromSession(HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(CARRITO_SESSION, carrito);
        }
        return carrito;
    }
    
    private BigDecimal calcularTotal(List<CarritoItem> carrito) {
        return carrito.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = getCarritoFromSession(session);
        model.addAttribute("carritoSize", carrito.size());
    }
}