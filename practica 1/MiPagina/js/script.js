// Botón "Ver más / Ver menos" en las tarjetas de comida

const botones = document.querySelectorAll('.btn-ver-mas');

botones.forEach((boton) => {
    boton.addEventListener('click', () => {
        const descripcion = boton.previousElementSibling;
        descripcion.classList.toggle('visible');

        if (descripcion.classList.contains('visible')) {
            boton.textContent = 'Ver menos';
        } else {
            boton.textContent = 'Ver más';
        }
    });
});
