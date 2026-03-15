import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ServiceClienteService } from './service-cliente.service';
import { ServiceCotizacionesService } from '../cotizaciones/Cotizaciones/service-cotizaciones.service';
import { AuthService } from '../servicios/auth.service';
import { EntidadCliente } from './entidad-cliente';
import { EntidadCotizaciones } from '../cotizaciones/Cotizaciones/entidad-cotizaciones';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";

@Component({
  selector: 'app-cliente',
  standalone: true,
  imports: [MenuVerticalComponent, HeaderUsuarrioComponent, CommonModule, FormsModule],
  templateUrl: './cliente.component.html',
  styleUrl: './cliente.component.css'
})
export class ClienteComponent implements OnInit {
  private clienteService = inject(ServiceClienteService);
  private cotizacionesService = inject(ServiceCotizacionesService);
  private authService = inject(AuthService);

  clientesOriginales: EntidadCliente[] = [];
  clientesFiltrados: EntidadCliente[] = [];
  cotizacionesGlobales: EntidadCotizaciones[] = [];

  // Filtros
  filtroId: string = '';
  filtroNombre: string = '';
  ordenCotizaciones: string = ''; // 'mas' o 'menos'

  esAdmin: boolean = false;
  mostrarFormularioCrear: boolean = false;
  idEditando: number | null = null;
  
  clienteCrear: EntidadCliente = this.inicializarCliente();
  clienteEditar: EntidadCliente = this.inicializarCliente();

  ngOnInit() {
    this.esAdmin = this.authService.getUserRole() === 'ADMIN';
    this.obtenerDatos();
  }

  obtenerDatos(): void {
    this.clienteService.listarClientes().subscribe(datos => {
      this.clientesOriginales = datos;
      this.clientesFiltrados = datos;
      this.aplicarFiltros(); // Re-aplicar por si hay filtros activos
    });

    this.cotizacionesService.listarCotizaciones().subscribe(cots => {
      this.cotizacionesGlobales = cots;
    });
  }

  // --- LÓGICA DE FILTRADO Y ORDENAMIENTO ---
  aplicarFiltros() {
    let resultado = this.clientesOriginales.filter(c => {
      const coincideId = c.id_cliente.toString().includes(this.filtroId);
      const coincideNombre = c.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      return coincideId && coincideNombre;
    });

    // Ordenar por cantidad de cotizaciones
    if (this.ordenCotizaciones === 'mas') {
      resultado.sort((a, b) => this.getCount(b.id_cliente) - this.getCount(a.id_cliente));
    } else if (this.ordenCotizaciones === 'menos') {
      resultado.sort((a, b) => this.getCount(a.id_cliente) - this.getCount(b.id_cliente));
    }

    this.clientesFiltrados = resultado;
  }

  private getCount(idCliente: number): number {
    return this.cotizacionesGlobales.filter(cot => cot.id_cliente === idCliente).length;
  }

  // --- VALIDACIONES Y ACCIONES ---
  crearCliente() {
    const duplicado = this.clientesOriginales.some(c => c.id_cliente === this.clienteCrear.id_cliente);
    if (duplicado) {
      alert(`Error: El NIT/CC ${this.clienteCrear.id_cliente} ya existe en el sistema.`);
      return;
    }

    this.clienteService.crearCliente(this.clienteCrear).subscribe({
      next: () => {
        alert('Cliente registrado correctamente');
        this.obtenerDatos();
        this.clienteCrear = this.inicializarCliente();
        this.mostrarFormularioCrear = false;
      },
      error: () => alert('Error al registrar')
    });
  }

  actualizarCliente() {
    this.clienteService.actualizarCliente(this.clienteEditar).subscribe({
      next: () => {
        alert('Datos actualizados');
        this.obtenerDatos();
        this.idEditando = null;
      },
      error: () => alert('Error en actualización')
    });
  }

  getCotizacionesPorCliente(idCliente: number): string {
    const filtradas = this.cotizacionesGlobales
      .filter(cot => cot.id_cliente === idCliente)
      .map(cot => `#${cot.id_cotizacion}`);
    return filtradas.length > 0 ? filtradas.join(', ') : 'Sin registros';
  }

  buscarClienteID(id: number) {
    this.clienteService.buscarClientePorId(id).subscribe(datos => {
      this.clienteEditar = { ...datos };
      this.idEditando = id;
    });
  }

  private inicializarCliente(): EntidadCliente {
    return { id_cliente: 0, nombre: '', telefono: '', direccion: '', correo: '', estado: 'ACTIVO' };
  }

  evaluarEstado(estado?: string): string {
    return estado === 'ACTIVO' ? 'badge activo' : 'badge inactivo';
  }

  cancelarEdicion() { this.idEditando = null; }
  mostrarFormulario() { this.mostrarFormularioCrear = !this.mostrarFormularioCrear; }
}