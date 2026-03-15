import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { TrabajadorService } from '../data/trabajadores.service';
import { TrabajadorDTO } from '../data/trabajadores.models';

@Component({
  selector: 'app-crear-trabajador',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './crear-trabajador.component.html',
  styleUrl: './crear-trabajador.component.css'
})
export class CrearTrabajadorComponent implements OnInit {
  private trabajadorService = inject(TrabajadorService);
  private router = inject(Router);

  // Lista para verificar duplicados localmente
  trabajadoresExistentes: TrabajadorDTO[] = [];

  trabajador: TrabajadorDTO = {
    id_trabajador: 0, 
    nombre: '',
    telefono: '',
    direccion: '',
    correo: '',
    tipo_usuario: 'USUARIO',
    password: '',
    estado: 'ACTIVO'
  };

  ngOnInit(): void {
    // Cargamos los trabajadores actuales al iniciar para validar duplicados
    this.trabajadorService.listarTrabajadores().subscribe(data => {
      this.trabajadoresExistentes = data;
    });
  }

  /**
   * Valida que solo se ingresen números en el teléfono
   */
  validarSoloNumeros(event: KeyboardEvent): void {
  const pattern = /[0-9]/;
  const inputChar = String.fromCharCode(event.charCode);

  if (!pattern.test(inputChar)) {
    event.preventDefault();
  }
}

formatearTelefono(event: any): void {
  const valorInput = event.target.value;
  // Reemplaza todo lo que no sea número con un string vacío
  this.trabajador.telefono = valorInput.replace(/[^0-9]/g, '');
}


  guardarTrabajador(): void {
    // 1. Validaciones de campos vacíos
    if (!this.trabajador.id_trabajador || !this.trabajador.nombre || !this.trabajador.correo || !this.trabajador.password) {
      alert("Por favor, complete todos los campos obligatorios.");
      return;
    }

    // 2. Validación de Duplicados Local
    const idDuplicado = this.trabajadoresExistentes.find(t => t.id_trabajador === this.trabajador.id_trabajador);
    const correoDuplicado = this.trabajadoresExistentes.find(t => t.correo.toLowerCase() === this.trabajador.correo.toLowerCase());

    if (idDuplicado) {
      alert(`Error: Ya existe un trabajador registrado con la identificación ${this.trabajador.id_trabajador}.`);
      return;
    }

    if (correoDuplicado) {
      alert(`Error: El correo electrónico ${this.trabajador.correo} ya está en uso.`);
      return;
    }

    if (this.trabajador.telefono && this.trabajador.telefono.length < 7) {
    alert("El número de teléfono parece estar incompleto.");
    return;
  }

    // 4. Envío al servidor
    this.trabajadorService.agregarTrabajador(this.trabajador).subscribe({
      next: (res) => {
        alert("Trabajador registrado exitosamente."); 
        this.router.navigate(['/trabajadores']);
      },
      error: (err) => {
        alert("Error en el servidor: " + (err.error || "No se pudo completar el registro."));
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/trabajadores']);
  }
}