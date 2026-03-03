import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificacionService } from '../notificacion.service';
import { ProgramacionRequest } from '../notificacion.model';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { HeaderUsuarrioComponent } from '../../header-usuarrio/header-usuarrio.component';
import { MenuVerticalComponent } from '../../menu-vertical/menu-vertical.component';

@Component({
  selector: 'app-crear-notificacion',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './crear-notificacion.component.html',
  styleUrl: './crear-notificacion.component.css'
})
export class CrearNotificacionComponent implements OnInit {
  private service = inject(NotificacionService);
  private clienteService = inject(ServiceClienteService);
  private router = inject(Router);

  // Listas para selección
  clientes: EntidadCliente[] = [];
  
  // Objeto de solicitud inicializado con valores por defecto
  request: ProgramacionRequest = {
    notificacion: {
      titulo: '',
      mensaje: '',
      tipo: 'UNICA'
    },
    frecuencia: 'DIARIA',
    fechaInicio: new Date().toISOString().slice(0, 16), // Fecha actual para el input datetime-local
    fechaFin: null,
    idsDestinatarios: [],
    tipoDestinatario: 'CLIENTE'
  };

  ngOnInit(): void {
    this.cargarDestinatarios();
  }

  cargarDestinatarios(): void {
    // Por ahora cargamos clientes, podrías añadir lógica para trabajadores si tienes el servicio
    this.clienteService.listarClientes().subscribe(data => this.clientes = data);
  }

  toggleDestinatario(id: number): void {
    const index = this.request.idsDestinatarios.indexOf(id);
    if (index > -1) {
      this.request.idsDestinatarios.splice(index, 1);
    } else {
      this.request.idsDestinatarios.push(id);
    }
  }

  guardar(): void {
    // Validaciones mínimas
    if (!this.request.notificacion.titulo || !this.request.notificacion.mensaje) {
      alert('Por favor complete el título y el mensaje.');
      return;
    }
    if (this.request.idsDestinatarios.length === 0) {
      alert('Debe seleccionar al menos un destinatario.');
      return;
    }

    this.service.programar(this.request).subscribe({
      next: (resp) => {
        alert(resp);
        this.router.navigate(['/notificaciones']);
      },
      error: (err) => {
        console.error(err);
        alert('Error al procesar la programación.');
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/notificaciones']);
  }
}