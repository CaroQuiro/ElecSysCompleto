import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { TrabajadorService } from '../data/trabajadores.service';
import { TrabajadorDTO } from '../data/trabajadores.models';
import { AuthService } from '../../servicios/auth.service'; // Asegúrate de que la ruta sea correcta

@Component({
  selector: 'app-listar-trabajadores',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './listar-trabajadores.component.html',
  styleUrl: './listar-trabajadores.component.css'
})
export class ListarTrabajadoresComponent implements OnInit {
  private trabajadorService = inject(TrabajadorService);
  private authService = inject(AuthService);
  private router = inject(Router);

  trabajadoresOriginales: TrabajadorDTO[] = [];
  trabajadoresFiltrados: TrabajadorDTO[] = [];

  // Variables de Seguridad
  esAdmin: boolean = false;
  usuarioActualEmail: string | null = '';

  // Filtros de UI
  filtroId = '';
  filtroNombre = '';
  filtroTipo = '';

  ngOnInit(): void {
    // 1. Validar el rol y obtener el email del usuario logueado
    this.esAdmin = this.authService.getUserRole() === 'ADMIN';
    this.usuarioActualEmail = this.authService.getUsuarioActual();
    
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.trabajadorService.listarTrabajadores().subscribe({
      next: (datos) => {
        // 2. Aplicar restricción de visualización por rol
        if (this.esAdmin) {
          this.trabajadoresOriginales = datos;
        } else {
          // Si no es admin, solo ve el registro que coincida con su correo
          this.trabajadoresOriginales = datos.filter(t => t.correo === this.usuarioActualEmail);
        }
        
        this.trabajadoresFiltrados = [...this.trabajadoresOriginales];
      },
      error: (err) => console.error("Error al cargar trabajadores:", err)
    });
  }

  aplicarFiltros(): void {
    // Los filtros ahora operan sobre la lista ya restringida por el rol
    this.trabajadoresFiltrados = this.trabajadoresOriginales.filter(t => {
      const coincideId = t.id_trabajador.toString().includes(this.filtroId);
      const coincideNombre = t.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideTipo = !this.filtroTipo || t.tipo_usuario === this.filtroTipo;

      return coincideId && coincideNombre && coincideTipo;
    });
  }

  evaluarEstado(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'ACTIVO': return 'activo';
      case 'INACTIVO': return 'inactivo';
      case 'DESHABILITADO': return 'deshabilitado';
      default: return 'pendiente';
    }
  }

  crearTrabajador(): void { this.router.navigate(['/trabajadores/crear']); }
  verTrabajador(id: number): void { this.router.navigate(['/trabajadores/ver', id]); }
}