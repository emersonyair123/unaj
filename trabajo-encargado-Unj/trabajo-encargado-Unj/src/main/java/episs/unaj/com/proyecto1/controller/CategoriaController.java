package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import episs.unaj.com.proyecto1.entity.Categoria;
import episs.unaj.com.proyecto1.service.CategoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping
    public String listar(Model model, HttpSession session) {
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("contenido", "categorias/lista");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, HttpSession session) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("contenido", "categorias/formulario");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes redirect) {
        categoriaService.guardar(categoria);
        redirect.addFlashAttribute("exito", "Categoría guardada exitosamente");
        return "redirect:/categorias";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, HttpSession session) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id).orElseThrow());
        model.addAttribute("contenido", "categorias/formulario");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Categoria categoria, RedirectAttributes redirect) {
        categoriaService.actualizar(id, categoria);
        redirect.addFlashAttribute("exito", "Categoría actualizada exitosamente");
        return "redirect:/categorias";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        categoriaService.eliminar(id);
        redirect.addFlashAttribute("exito", "Categoría eliminada exitosamente");
        return "redirect:/categorias";
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        int size = (carrito != null) ? carrito.size() : 0;
        model.addAttribute("carritoSize", size);
    }
}