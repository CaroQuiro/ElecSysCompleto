import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificacionService } from '../notificacion.service';
import { ProgramacionRequest } from '../notificacion.model';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { TrabajadorService } from '../../trabajadores/data/trabajadores.service'; // Ajusta la ruta según tu proyecto
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
  private trabajadorService = inject(TrabajadorService);
  private router = inject(Router);

  // Listas de datos filtrados
  destinatariosFiltrados: any[] = [];
  
  request: ProgramacionRequest = {
    notificacion: {
      titulo: '',
      mensaje: '',
      tipo: 'UNICA'
    },
    frecuencia: 'DIARIA',
    fechaInicio: new Date().toISOString().slice(0, 16),
    fechaFin: null, // Mantenemos null ya que se quitó del formulario
    idsDestinatarios: [],
    tipoDestinatario: 'CLIENTE'
  };

  ngOnInit(): void {
    this.cargarDestinatarios();
  }

  // Cambia el tipo y recarga la lista
  cambiarTipoDestinatario(tipo: 'CLIENTE' | 'TRABAJADOR'): void {
    this.request.tipoDestinatario = tipo;
    this.request.idsDestinatarios = []; // Limpiamos selección al cambiar tipo
    this.cargarDestinatarios();
  }

  cargarDestinatarios(): void {
    if (this.request.tipoDestinatario === 'CLIENTE') {
      this.clienteService.listarClientes().subscribe(data => {
        // Filtramos solo los activos (ajusta 'estado' según tu entidad)
        this.destinatariosFiltrados = data.filter(c => c.estado === 'ACTIVO');
      });
    } else {
      this.trabajadorService.listarTrabajadores().subscribe(data => {
        // Filtramos solo los activos
        this.destinatariosFiltrados = data.filter(t => t.estado === 'ACTIVO');
      });
    }
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
        alert("Notificación programada con éxito");
        this.router.navigate(['/notificaciones']);
      },
      error: (err) => {
        alert('Error al procesar la programación.');
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/notificaciones']);
  }
}