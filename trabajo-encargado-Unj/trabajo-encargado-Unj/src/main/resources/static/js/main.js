// Toggle de tema oscuro/claro
function toggleTheme() {
    const body = document.body;
    const icon = document.querySelector('.theme-toggle i');

    body.classList.toggle('dark-theme');

    if (body.classList.contains('dark-theme')) {
        icon.className = 'fas fa-sun';
        localStorage.setItem('theme', 'dark');
    } else {
        icon.className = 'fas fa-moon';
        localStorage.setItem('theme', 'light');
    }
}

// Cargar tema guardado
document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
        document.querySelector('.theme-toggle i').className = 'fas fa-sun';
    }

    console.log('HIP URBAN STORE - Cargado exitosamente');
});

// Estilos para tema oscuro (se agregan dinámicamente)
const darkThemeStyles = `
    body.dark-theme {
        background-color: #1a1a2e;
        color: #e0e0e0;
    }
    
    body.dark-theme .subtitle-section {
        background: #16213e;
    }
    
    body.dark-theme .subtitle-section h2 {
        color: white;
    }
    
    body.dark-theme .subtitle-section p {
        color: #8892b0;
    }
    
    body.dark-theme .featured-products {
        background: #1a1a2e;
    }
    
    body.dark-theme .featured-products h2 {
        color: white;
    }
    
    body.dark-theme .product-card {
        background: #16213e;
        border: 1px solid rgba(255,255,255,0.05);
    }
    
    body.dark-theme .product-info h3 {
        color: white;
    }
    
    body.dark-theme .product-desc {
        color: #8892b0;
    }
    
    body.dark-theme .product-footer {
        border-top-color: rgba(255,255,255,0.05);
    }
    
    body.dark-theme .btn-ver-mas {
        background: #e94560;
    }
    
    body.dark-theme .search-bar input {
        background: #2d2d4a;
        color: white;
    }
    
    body.dark-theme .search-bar input::placeholder {
        color: #8892b0;
    }
    
    body.dark-theme .img-placeholder {
        background: linear-gradient(135deg, #2d2d4a, #3d3d5c);
        border-color: rgba(255,255,255,0.1);
    }
    
    body.dark-theme .product-image {
        background: #2d2d4a;
    }
    
    body.dark-theme .image-placeholder {
        color: #666;
    }
`;

// Aplicar estilos de tema oscuro
const styleSheet = document.createElement("style");
styleSheet.textContent = darkThemeStyles;
document.head.appendChild(styleSheet);

// Confirmación para eliminaciones
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('btn-eliminar')) {
        if (!confirm('¿Estás seguro de eliminar este registro?')) {
            e.preventDefault();
        }
    }
});

// Auto-ocultar mensajes de éxito
setTimeout(() => {
    document.querySelectorAll('.mensaje-exito').forEach(el => {
        el.style.transition = 'opacity 0.5s';
        el.style.opacity = '0';
        setTimeout(() => el.remove(), 500);
    });
}, 5000);