import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificacionService } from '../notificacion.service';
import { NotificacionDTO } from '../notificacion.model';
import { HeaderUsuarrioComponent } from '../../header-usuarrio/header-usuarrio.component';
import { MenuVerticalComponent } from '../../menu-vertical/menu-vertical.component';

@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './notificaciones.component.html',
  styleUrl: './notificaciones.component.css'
})
export class NotificacionesComponent implements OnInit {
  private service = inject(NotificacionService);
  private router = inject(Router);

  notificaciones: NotificacionDTO[] = [];

  ngOnInit(): void {
    this.cargarNotificaciones();
  }

  cargarNotificaciones(): void {
    this.service.listar().subscribe({
      next: (data) => this.notificaciones = data,
      error: (err) => console.error('Error al cargar notificaciones', err)
    });
  }

  irACrear(): void {
    this.router.navigate(['/notificaciones/crear']);
  }

  desactivar(id: number | undefined): void {
    if (!id) return;
    if (confirm('¿Está seguro de desactivar esta notificación? Ya no se enviará automáticamente.')) {
      this.service.desactivar(id).subscribe({
        next: (resp) => {
          alert(resp);
          this.cargarNotificaciones();
        },
        error: (err) => alert('Error al desactivar')
      });
    }
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    if (confirm('¿Desea eliminar permanentemente este registro?')) {
      this.service.borrar(id).subscribe({
        next: (resp) => {
          alert(resp);
          this.cargarNotificaciones();
        },
        error: (err) => alert('Error al eliminar')
      });
    }
  }

  // Método para truncar mensajes largos en la tabla
  formatearMensaje(mensaje: string): string {
    return mensaje.length > 50 ? mensaje.substring(0, 50) + '...' : mensaje;
  }
}