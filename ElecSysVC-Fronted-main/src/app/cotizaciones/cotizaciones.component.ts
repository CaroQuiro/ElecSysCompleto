import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { Router } from '@angular/router';
import { EntidadCotizaciones } from './Cotizaciones/entidad-cotizaciones';
import { ServiceCotizacionesService } from './Cotizaciones/service-cotizaciones.service';
import { CommonModule } from '@angular/common';
import { ServiceClienteService } from '../cliente/service-cliente.service';
import { EntidadCliente } from '../cliente/entidad-cliente';
import { ServiceLugarService } from '../lugar/service-lugar.service';
import { EntidadLugar } from '../lugar/entidad-lugar';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cotizaciones',
  standalone: true,
  imports: [CommonModule, HeaderUsuarrioComponent, MenuVerticalComponent, FormsModule],
  templateUrl: './cotizaciones.component.html',
  styleUrl: './cotizaciones.component.css'
})
export class CotizacionesComponent implements OnInit, OnDestroy {

  private cotizacionservice = inject(ServiceCotizacionesService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private suscription!: Subscription;

  cotizaciones: EntidadCotizaciones[] = [];
  cotizacionesFiltradas: EntidadCotizaciones[] = [];
  clientes: EntidadCliente[] = [];
  lugares: EntidadLugar[] = [];

  clietesMap = new Map<number, EntidadCliente>();
  lugaresMap = new Map<number, EntidadLugar>();

  filtroActivo: string = 'TODAS';
  filtroId: string = '';
  filtroCliente: string = '';
  filtroFecha: string = '';
  filtroEstadoSelect: string = '';

  constructor(private route: Router){}

  ngOnInit(){
    // 1. Cargar datos frescos al entrar
    this.cargarTodo();

    // 2. Escuchar por si hay cambios mientras la pantalla está abierta
    this.suscription = this.cotizacionservice.refreshNeeded$.subscribe(() => {
      this.obtenerCotizaciones();
    });
  }

  ngOnDestroy(): void {
    if (this.suscription) this.suscription.unsubscribe();
  }

  cargarTodo() {
    this.obtenerCotizaciones();
    this.obtenerClientes();
    this.obtenerLugares();
  }

  obtenerCotizaciones() : void {
    // Limpiamos opcionalmente para indicar carga
    this.cotizacionservice.listarCotizaciones().subscribe({
      next: (datos) => {
        this.cotizaciones = [...datos]; // Clonamos el array para asegurar reactividad
        this.filtrarEstado(this.filtroActivo);
        console.log("ElecSys: Lista sincronizada con éxito");
      }, 
      error: (error) => console.error("Error al obtener cotizaciones", error)
    });
  }

  aplicarFiltros(): void {
    this.cotizacionesFiltradas = this.cotizaciones.filter(cot => {
      // 1. Filtro por Pestaña superior
      const coincidePestana = this.filtroActivo === 'TODAS' || cot.estado === this.filtroActivo;
      
      // 2. Filtro por Dropdown de Estado (Si se selecciona uno)
      const coincideSelect = !this.filtroEstadoSelect || cot.estado === this.filtroEstadoSelect;

      // 3. Filtro por ID
      const coincideId = cot.id_cotizacion.toString().includes(this.filtroId);

      // 4. Filtro por Cliente
      const nombreClie = this.clietesMap.get(cot.id_cliente)?.nombre.toLowerCase() || '';
      const coincideCliente = nombreClie.includes(this.filtroCliente.toLowerCase());

      // 5. Filtro por Fecha
      const coincideFecha = !this.filtroFecha || cot.fecha_realizacion === this.filtroFecha;

      // Se deben cumplir todas las condiciones
      return coincidePestana && coincideSelect && coincideId && coincideCliente && coincideFecha;
    });
  }


  filtrarEstado(estado: string): void {
    this.filtroActivo = estado;
    this.filtroEstadoSelect = ''; 
    this.aplicarFiltros();
  }

  obtenerClientes(): void {
    this.clienteService.listarClientes().subscribe({
      next: (datos) => {
        this.clientes = datos;
        datos.forEach(c => this.clietesMap.set(c.id_cliente, c));
      }
    });
  }

  obtenerLugares(): void {
    this.lugarService.listarLugares().subscribe({
      next: (datos) => {
        this.lugares = datos;
        datos.forEach(l => this.lugaresMap.set(l.idLugar, l));
      }
    });
  }

  evaluarEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return 'badge pendiente';
      case 'ACTIVO': return 'badge completado';
      case 'RECHAZADO': return 'badge cancelado';
      default: return 'badge';
    }
  }

  calcularTotal(campo: keyof EntidadCotizaciones): number {
    return this.cotizacionesFiltradas.reduce((total, cot) => total + Number(cot[campo] ?? 0), 0);
  }

  crearCotizacion() {
    this.route.navigate(['/cotizaciones/crear']);
  }

  verCotizacion(id: number) {
    this.route.navigate(['cotizaciones/ver', id]);
  }

}