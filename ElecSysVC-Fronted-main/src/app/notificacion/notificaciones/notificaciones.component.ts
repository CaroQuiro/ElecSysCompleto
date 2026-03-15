import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para los filtros
import { NotificacionService } from '../notificacion.service';
import { NotificacionDTO } from '../notificacion.model';
import { Router } from '@angular/router';
import { MenuVerticalComponent } from '../../menu-vertical/menu-vertical.component';
import { HeaderUsuarrioComponent } from '../../header-usuarrio/header-usuarrio.component';


@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuVerticalComponent,HeaderUsuarrioComponent],
  templateUrl: './notificaciones.component.html',
  styleUrl: './notificaciones.component.css'
})
export class NotificacionesComponent implements OnInit {
  private service = inject(NotificacionService);
  private router = inject(Router);

  notificaciones: NotificacionDTO[] = [];
  
  // Filtros
  filtroTitulo: string = '';
  filtroTipo: string = '';

  // Modal
  mostrarModal: boolean = false;
  notificacionSeleccionada: NotificacionDTO = {} as NotificacionDTO;

  ngOnInit(): void {
    this.cargarNotificaciones();
  }

  cargarNotificaciones(): void {
    this.service.listar().subscribe(data => this.notificaciones = data);
  }

  // Lógica de Filtrado Dinámico
  get notificacionesFiltradas() {
    return this.notificaciones.filter(n => {
      const coincideTitulo = n.titulo.toLowerCase().includes(this.filtroTitulo.toLowerCase());
      const coincideTipo = this.filtroTipo === '' || n.tipo === this.filtroTipo;
      return coincideTitulo && coincideTipo;
    });
  }

  // Acciones de Estado
  toggleEstado(n: NotificacionDTO): void {
    const accion = n.estado === 'ACTIVA' ? this.service.desactivar(n.idNotificacion!) : this.service.activar(n.idNotificacion!);
    accion.subscribe(() => {
      alert(`Notificación ${n.estado === 'ACTIVA' ? 'pausada' : 'activada'} correctamente.`);
      this.cargarNotificaciones();
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Desea eliminar esta notificación recurrente?')) {
      this.service.borrar(id).subscribe(() => {
        this.cargarNotificaciones();
      });
    }
  }

  // Lógica del Modal de Actualización
  abrirModal(n: NotificacionDTO): void {
    this.notificacionSeleccionada = { ...n }; // Clonamos el objeto
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardarCambios(): void {
    this.service.actualizar(this.notificacionSeleccionada.idNotificacion!, this.notificacionSeleccionada).subscribe(() => {
      alert('Notificación actualizada con éxito.');
      this.cerrarModal();
      this.cargarNotificaciones();
    });
  }

  formatearMensaje(msg: string): string {
    return msg.length > 40 ? msg.substring(0, 40) + '...' : msg;
  }

  irACrear() { this.router.navigate(['/notificaciones/crear']); }

  limpiarYFormatearMensaje(html: string): string {
    // 1. Quitar etiquetas HTML
    const textoPlano = html.replace(/<[^>]*>/g, '');
    // 2. Truncar
    return textoPlano.length > 50 ? textoPlano.substring(0, 50) + '...' : textoPlano;
  }
}