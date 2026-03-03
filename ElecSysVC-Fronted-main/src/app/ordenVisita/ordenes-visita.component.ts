import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { OrdenVisitaService } from './data/orden-visita.service';
import { OrdenDeVisitaDTO } from './data/orden-visita.models';
import { ServiceClienteService } from '../cliente/service-cliente.service';
import { ServiceLugarService } from '../lugar/service-lugar.service';
import { EntidadCliente } from '../cliente/entidad-cliente';
import { EntidadLugar } from '../lugar/entidad-lugar';

@Component({
  selector: 'app-ordenes-visita',
  standalone: true,
  imports: [CommonModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ordenes-visita.component.html',
  styleUrl: './ordenes-visita.component.css'
})
export class OrdenesVisitaComponent implements OnInit {
  private ordenVisitaService = inject(OrdenVisitaService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private router = inject(Router);

  ordenes: OrdenDeVisitaDTO[] = [];
  clientesMap = new Map<number, EntidadCliente>();
  lugaresMap = new Map<number, EntidadLugar>();

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    // Cargar Clientes
    this.clienteService.listarClientes().subscribe(datos => {
      datos.forEach(c => this.clientesMap.set(c.id_cliente, c));
    });

    // Cargar Lugares
    this.lugarService.listarLugares().subscribe(datos => {
      datos.forEach(l => this.lugaresMap.set(l.idLugar, l));
    });

    // Cargar Órdenes
    this.ordenVisitaService.listarOrdenes().subscribe({
      next: (datos) => this.ordenes = datos,
      error: (e) => console.error("Error al cargar órdenes", e)
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

  crearOrden() {
    this.router.navigate(['/ordenes-visita/crear']);
  }

  verOrden(id: number | undefined) {
    if(id) this.router.navigate(['/ordenes-visita/ver', id]);
  }
}