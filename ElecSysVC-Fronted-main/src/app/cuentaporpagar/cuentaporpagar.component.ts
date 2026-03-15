import { Component, OnInit, inject } from '@angular/core';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { CuentaServiceService } from './Cuentas-Entidad/cuenta-service.service';
import { EntidadCuentasPagar } from './Cuentas-Entidad/CuentaPagar-Entidad';
import { ServiceClienteService } from '../cliente/service-cliente.service';
import { Router } from '@angular/router';
import { EntidadCliente } from '../cliente/entidad-cliente';
import { CommonModule, NgClass } from "@angular/common";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cuentaporpagar',
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, NgClass, CommonModule, FormsModule],
  templateUrl: './cuentaporpagar.component.html',
  styleUrl: './cuentaporpagar.component.css'
})
export class CuentaporpagarComponent {

  constructor(private cuentaService: CuentaServiceService,
    private clienteService: ServiceClienteService,
    private route: Router) { }

  cuentasporpagar: EntidadCuentasPagar[] = [];
  cuentasFiltradas: EntidadCuentasPagar[] = [];
  clientes: EntidadCliente[] = [];

  clientesMap = new Map<number, EntidadCliente>();

  filtroId: string = '';
  filtroCliente: string = '';
  filtroFecha: string = '';
  filtroActivo: string = 'TODAS';

  totalCuentas = 0;
  totalPendiente = 0;
  totalPagado = 0;
  cantidadPendiente = 0;
  cantidadPagado = 0;
  cantidadTodas: number = 0;

  ngOnInit() {
    this.obtenerClientes();
    this.obtenerCuentas();
  }

  obtenerClientes(): void {
    this.clienteService.listarClientes().subscribe(
      {
        next: (datos) => {
          this.clientes = datos;

          datos.forEach(cliente => {
            this.clientesMap.set(cliente.id_cliente, cliente);
          });
        },
        error: (error) => {
          console.error("Error al obtener la informacion de clientes", error);
        }
      }
    );
  }

  obtenerCuentas(): void {
    this.cuentaService.listarCuentasPorPagar().subscribe(
      {
        next: (datos) => {
          this.cuentasporpagar = datos;
          this.cuentasFiltradas = datos;
          this.calcularTotales();
        },
        error: (err) => {
          console.error("Error al obtener la informacion", err);
        }
      }
    );
  }

  calcularTotales(): void {

    this.totalCuentas = 0;
    this.totalPendiente = 0;
    this.totalPagado = 0;

    this.cantidadTodas = this.cuentasporpagar.length;
    this.cantidadPendiente = 0;
    this.cantidadPagado = 0;

    this.cuentasporpagar.forEach(cuenta => {

      this.totalCuentas += cuenta.monto;

      if (cuenta.estado === 'PENDIENTE') {
        this.totalPendiente += cuenta.monto;
        this.cantidadPendiente++;
      }

      if (cuenta.estado === 'PAGADO') {
        this.totalPagado += cuenta.monto;
        this.cantidadPagado++;
      }

    });

  }

  //Seccion de filtro de informacion por categoria

  aplicarFiltros(): void {
    this.cuentasFiltradas = this.cuentasporpagar.filter(cuenta => {
      // 1. Filtro por Estado (Pestañas)
      const coincideEstado = this.filtroActivo === 'TODAS' || cuenta.estado === this.filtroActivo;
      
      // 2. Filtro por ID
      const coincideId = cuenta.id_cuenta_pagar.toString().includes(this.filtroId);

      // 3. Filtro por Nombre de Cliente (Buscamos en el Map)
      const nombreCliente = this.clientesMap.get(cuenta.id_cliente)?.nombre.toLowerCase() || '';
      const coincideCliente = nombreCliente.includes(this.filtroCliente.toLowerCase());

      // 4. Filtro por Fecha
      const coincideFecha = !this.filtroFecha || cuenta.fecha_realizacion === this.filtroFecha;

      return coincideEstado && coincideId && coincideCliente && coincideFecha;
    });

    this.recalcularTotales();
  }

  filtrarEstado(estado: string): void {
    this.filtroActivo = estado;
    this.aplicarFiltros();
  }

  recalcularTotales(): void {
    this.totalCuentas = 0;
    this.totalPendiente = 0;
    this.totalPagado = 0;
    this.cantidadPendiente = 0;
    this.cantidadPagado = 0;

    // Calculamos totales sobre la lista GENERAL para que los contadores de las pestañas sean correctos
    this.cuentasporpagar.forEach(c => {
      this.totalCuentas += c.monto;
      if (c.estado === 'PENDIENTE') {
        this.totalPendiente += c.monto;
        this.cantidadPendiente++;
      }
      if (c.estado === 'PAGADO') {
        this.totalPagado += c.monto;
        this.cantidadPagado++;
      }
    });
  }

  evaluarEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return 'badge pendiente';
      case 'PAGADO': return 'badge completado';
      case 'EN_PROCESO': return 'badge cancelado';
      default: return 'badge';
    }
  }

  navegarCrearCuenta(){
    this.route.navigate(['cuentaspagar/crear']);
  }

  verCuentaPagar(id: number){
    this.route.navigate(['cuentaspagar/ver', id]);
  }
}
