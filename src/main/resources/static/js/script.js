document.addEventListener('DOMContentLoaded', () => {
    const headerUp = document.getElementById('headerup');
    const headAdmin = document.getElementById('headadmin');
    const root = document.documentElement;

    // Variables para el scroll
    let lastScroll = 0;
    const scrollThreshold = 50; // Mínimo scroll para activar

    function calcularAlturas() {
        const hUp = headerUp.offsetHeight;
        const hAdmin = headAdmin.offsetHeight;
        const hTotal = hUp + hAdmin;

        // 1. Asignamos la altura del header superior a una variable CSS
        // Esto coloca el header inferior justo debajo del superior automáticamente
        root.style.setProperty('--altura-header-up', `${hUp}px`);

        // 2. Asignamos la altura TOTAL al spacer
        // Esto empuja el contenido de la web hacia abajo lo justo y necesario
        root.style.setProperty('--altura-header-total', `${hTotal}px`);
    }

    // Calculamos al inicio y si cambia el tamaño de la ventana
    calcularAlturas();
    window.addEventListener('resize', calcularAlturas);

    // Lógica del Scroll
    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset || document.documentElement.scrollTop;

        if (currentScroll <= 0) {
            document.body.classList.remove('scroll-hide-header');
            return;
        }

        // Si bajamos Y pasamos el umbral...
        if (currentScroll > lastScroll && currentScroll > scrollThreshold) {
            document.body.classList.add('scroll-hide-header');
        }
        // Si subimos...
        else if (currentScroll < lastScroll) {
            document.body.classList.remove('scroll-hide-header');
        }

        lastScroll = currentScroll;
    });
});