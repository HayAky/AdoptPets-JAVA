    package com.adoptpets.AdoptPets.model.enums;

    public enum EstadoAdopcion {
        pendiente, aprobada, rechazada, completada, disponible,en_proceso;

        public boolean isEmpty() {
            return false;
        }
    }
