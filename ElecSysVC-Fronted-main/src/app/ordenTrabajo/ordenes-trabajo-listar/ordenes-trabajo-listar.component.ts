import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { OrdenTrabajoService } from '../data/orden-trabajo.service';
import { OrdenDeTrabajoDTO } from '../data/orden-trabajo.models';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { EntidadLugar } from '../../lugar/entidad-lugar';

@Component({
  selector: 'app-ordenes-trabajo-listar',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ordenes-trabajo-listar.component.html',
  styleUrl: './ordenes-trabajo-listar.component.css'
})
export class OrdenesTrabajoListarComponent implements OnInit {
  private ordenTrabajoService = inject(OrdenTrabajoService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private router = inject(Router);

  ordenesOriginales: OrdenDeTrabajoDTO[] = [];
  ordenesFiltradas: OrdenDeTrabajoDTO[] = [];
  
  clientesMap = new Map<number, EntidadCliente>();
  lugaresMap = new Map<number, EntidadLugar>();

  // Filtros
  filtroId = '';
  filtroCliente = '';
  filtroEstado = '';

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.clienteService.listarClientes().subscribe(datos => {
      datos.forEach(c => this.clientesMap.set(c.id_cliente, c));
    });

    this.lugarService.listarLugares().subscribe(datos => {
      datos.forEach(l => this.lugaresMap.set(l.idLugar, l));
    });

    this.ordenTrabajoService.listarOrdenes().subscribe(datos => {
      this.ordenesOriginales = datos;
      this.ordenesFiltradas = datos;
    });
  }

  aplicarFiltros() {
    this.ordenesFiltradas = this.ordenesOriginales.filter(orden => {
      const coincideId = orden.id_orden?.toString().includes(this.filtroId);
      const nombreC = this.clientesMap.get(orden.id_cliente)?.nombre.toLowerCase() || '';
      const coincideCliente = nombreC.includes(this.filtroCliente.toLowerCase());
      const coincideEstado = !this.filtroEstado || orden.estado === this.filtroEstado;

      return coincideId && coincideCliente && coincideEstado;
    });
  }

  evaluarEstado(estado: string): string {
  switch (estado) {
    case 'PENDIENTE': return 'pendiente';
    case 'EN_PROCESO': return 'proceso';
    case 'REALIZADA': return 'realizada'; // Verde Esmeralda
    case 'CANCELADA': return 'cancelada'; // Rojo Granate
    default: return '';
  }
}

  crearOrden() { this.router.navigate(['/ordenes-trabajo/crear']); }
  verOrden(id: number | undefined) { if (id) this.router.navigate(['/ordenes-trabajo/ver', id]); }
}