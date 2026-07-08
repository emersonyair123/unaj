package episs.unaj.com.proyecto1.controller;

import episs.unaj.com.proyecto1.entity.CarritoItem;
import episs.unaj.com.proyecto1.entity.Producto;
import episs.unaj.com.proyecto1.service.CategoriaService;
import episs.unaj.com.proyecto1.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Value("${upload.path:uploads/}")
    private String uploadPath;
    
    private static final String CARRITO_SESSION = "carrito";
    
    @GetMapping
    public String listar(Model model, HttpSession session) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("contenido", "productos/lista");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, HttpSession session) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("contenido", "productos/formulario");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          RedirectAttributes redirect) {
        try {
            // Guardar la imagen si se subió
            if (!imagenFile.isEmpty()) {
                String nombreImagen = guardarImagen(imagenFile);
                producto.setImagen(nombreImagen);
            }
            
            productoService.guardar(producto);
            redirect.addFlashAttribute("exito", "Producto guardado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/productos";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, HttpSession session) {
        model.addAttribute("producto", productoService.buscarPorId(id).orElseThrow());
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("contenido", "productos/formulario");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Producto producto,
                             @RequestParam("imagenFile") MultipartFile imagenFile,
                             RedirectAttributes redirect) {
        try {
            Producto productoExistente = productoService.buscarPorId(id).orElseThrow();
            
            // Actualizar campos
            productoExistente.setNombre(producto.getNombre());
            productoExistente.setDescripcion(producto.getDescripcion());
            productoExistente.setPrecio(producto.getPrecio());
            productoExistente.setStock(producto.getStock());
            productoExistente.setCategoria(producto.getCategoria());
            
            // Actualizar imagen si se subió una nueva
            if (!imagenFile.isEmpty()) {
                // Eliminar imagen anterior si existe
                if (productoExistente.getImagen() != null) {
                    eliminarImagen(productoExistente.getImagen());
                }
                String nombreImagen = guardarImagen(imagenFile);
                productoExistente.setImagen(nombreImagen);
            }
            
            productoService.actualizar(id, productoExistente);
            redirect.addFlashAttribute("exito", "Producto actualizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
        }
        return "redirect:/productos";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            Producto producto = productoService.buscarPorId(id).orElseThrow();
            // Eliminar imagen asociada
            if (producto.getImagen() != null) {
                eliminarImagen(producto.getImagen());
            }
            productoService.eliminar(id);
            redirect.addFlashAttribute("exito", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar el producto");
        }
        return "redirect:/productos";
    }
    
    @GetMapping("/detalle/{id}")
    public String detalleProducto(@PathVariable Integer id, Model model, HttpSession session) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        model.addAttribute("productosRelacionados", productoService.listarTodos());
        model.addAttribute("contenido", "productos/detalle");
        agregarCarritoSize(model, session);
        return "layout/layout";
    }
    
    private String guardarImagen(MultipartFile imagenFile) throws IOException {
        // Crear directorio si no existe
        Path directorio = Paths.get(uploadPath);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        
        // Generar nombre único para la imagen
        String nombreOriginal = imagenFile.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;
        
        // Guardar imagen
        Path rutaCompleta = directorio.resolve(nombreUnico);
        Files.copy(imagenFile.getInputStream(), rutaCompleta);
        
        return nombreUnico;
    }
    
    private void eliminarImagen(String nombreImagen) {
        try {
            Path ruta = Paths.get(uploadPath).resolve(nombreImagen);
            Files.deleteIfExists(ruta);
        } catch (Exception e) {
            // Ignorar error al eliminar
        }
    }
    
    private void agregarCarritoSize(Model model, HttpSession session) {
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute(CARRITO_SESSION);
        int size = (carrito != null) ? carrito.size() : 0;
        model.addAttribute("carritoSize", size);
    }
}