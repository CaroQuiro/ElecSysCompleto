import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // IMPORTANTE: Agrega FormsModule
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { OrdenVisitaService } from '../data/orden-visita.service';
import { OrdenDeVisitaDTO } from '../data/orden-visita.models';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { EntidadLugar } from '../../lugar/entidad-lugar';

@Component({
  selector: 'app-ordenes-visita-listar',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent], // Agrega FormsModule aquí
  templateUrl: './ordenes-visita-listar.component.html',
  styleUrl: './ordenes-visita-listar.component.css'
})
export class OrdenesVisitaListarComponent implements OnInit {
  private ordenVisitaService = inject(OrdenVisitaService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private router = inject(Router);

  ordenesOriginales: OrdenDeVisitaDTO[] = []; // Para mantener la copia completa
  ordenesFiltradas: OrdenDeVisitaDTO[] = [];   // Esta es la que mostraremos en el HTML
  
  clientesMap = new Map<number, EntidadCliente>();
  lugaresMap = new Map<number, EntidadLugar>();

  // Variables para los filtros
  filtroId: string = '';
  filtroCliente: string = '';
  filtroFecha: string = '';
  filtroEstado: string = '';

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.clienteService.listarClientes().subscribe({
      next: (datos) => datos.forEach(c => this.clientesMap.set(c.id_cliente, c))
    });

    this.lugarService.listarLugares().subscribe({
      next: (datos) => datos.forEach(l => this.lugaresMap.set(l.idLugar, l))
    });

    this.ordenVisitaService.listarOrdenes().subscribe({
      next: (datos) => {
        this.ordenesOriginales = datos;
        this.ordenesFiltradas = datos; // Al inicio son iguales
      }
    });
  }

  // FUNCIÓN DE FILTRADO
  aplicarFiltros() {
    this.ordenesFiltradas = this.ordenesOriginales.filter(orden => {
      // Filtro por ID
      const coincideId = orden.idVisita?.toString().includes(this.filtroId);
      
      // Filtro por nombre de cliente (usando el Map)
      const nombreCliente = this.clientesMap.get(orden.idCliente)?.nombre.toLowerCase() || '';
      const coincideCliente = nombreCliente.includes(this.filtroCliente.toLowerCase());
      
      // Filtro por Fecha
      const coincideFecha = !this.filtroFecha || orden.fechaRealizacion.toString().includes(this.filtroFecha);
      
      // Filtro por Estado
      const coincideEstado = !this.filtroEstado || orden.estado === this.filtroEstado;

      return coincideId && coincideCliente && coincideFecha && coincideEstado;
    });
  }

  evaluarEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return 'badge pendiente';
      case 'PROGRAMADA': return 'badge programada';
      case 'REALIZADA': return 'badge completado';
      case 'CANCELADA': return 'badge cancelado';
      default: return 'badge';
    }
  }

  crearOrden(): void { this.router.navigate(['/ordenes-visita/crear']); }
  verOrden(id: number | undefined): void { if (id) this.router.navigate(['/ordenes-visita/ver', id]); }
}