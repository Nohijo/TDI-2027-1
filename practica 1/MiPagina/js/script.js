// Botón "Ver más / Ver menos" 

const botones = document.querySelectorAll('.btn-ver-mas');

botones.forEach((boton) => {
    boton.addEventListener('click', () => { //Haz eso cuando click
        const descripcion = boton.previousElementSibling;//lo dentro de la descr
        descripcion.classList.toggle('visible');//agrega visible si no la tiene y quita si la tiene

        if (descripcion.classList.contains('visible')) {
            boton.textContent = 'Ver menos';
        } else {
            boton.textContent = 'Ver más';
        }//si si se ve eentonces ver menos y al revez 
    });
});
