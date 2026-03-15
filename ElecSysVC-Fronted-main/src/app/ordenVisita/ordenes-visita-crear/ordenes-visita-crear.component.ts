import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { OrdenVisitaService } from '../data/orden-visita.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { EntidadLugar } from '../../lugar/entidad-lugar';
import { OrdenDeVisitaDTO, DetalleOrdenVisitaDTO, OrdenDeVisitaRequest } from '../data/orden-visita.models';

@Component({
  selector: 'app-ordenes-visita-crear',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ordenes-visita-crear.component.html',
  styleUrl: './ordenes-visita-crear.component.css'
})
export class OrdenesVisitaCrearComponent {
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private ordenService = inject(OrdenVisitaService);
  private router = inject(Router);

  // Búsqueda y selección
  textoBusquedaCliente = '';
  textoBusquedaLugar = '';
  clientes: EntidadCliente[] = [];
  lugares: EntidadLugar[] = [];
  clienteSeleccionado?: EntidadCliente;
  lugarSeleccionado?: EntidadLugar;

  // Formulario principal
  ordenForm: OrdenDeVisitaDTO = {
    idLugar: 0,
    idCliente: 0,
    idTrabajador: 1, // Simulado, debería venir del login
    fechaRealizacion: new Date().toISOString().split('T')[0],
    descripcion: '',
    estado: 'PENDIENTE'
  };

  detalles: DetalleOrdenVisitaDTO[] = [
    { actividad: '', observaciones: '', duracion: '' }
  ];

  buscarCliente(): void {
  if (this.textoBusquedaCliente.length < 2) {
    this.clientes = [];
    return;
  }
  this.clienteService.buscarClienteQuery(this.textoBusquedaCliente).subscribe(res => {
    this.clientes = res.filter(c => c.estado === 'ACTIVO');
  });
}

  seleccionarCliente(c: EntidadCliente) {
    this.clienteSeleccionado = c;
    this.ordenForm.idCliente = c.id_cliente;
    this.clientes = [];
  }

  buscarLugar() {
    if (this.textoBusquedaLugar.length < 2) return;
    this.lugarService.buscarLugarQuery(this.textoBusquedaLugar).subscribe({
      next: res => this.lugares = res,
      error: () => this.lugares = []
    });
  }

  seleccionarLugar(l: EntidadLugar) {
    this.lugarSeleccionado = l;
    this.ordenForm.idLugar = l.idLugar;
    this.lugares = [];
  }

  agregarDetalle() {
    this.detalles.push({ actividad: '', observaciones: '', duracion: '' });
  }

  eliminarDetalle(index: number) {
    this.detalles.splice(index, 1);
  }

  guardarOrden() {
    if (!this.clienteSeleccionado || !this.lugarSeleccionado) {
      alert("Debe seleccionar cliente y lugar");
      return;
    }

    const request: OrdenDeVisitaRequest = {
      orden: this.ordenForm,
      detalles: this.detalles
    };

    this.ordenService.agregarOrden(request).subscribe({
      next: (res) => {
        alert(res);
        this.router.navigate(['/ordenes-visita']);
      },
      error: (err) => console.error("Error al guardar:", err)
    });
  }

  navegarCliente() { this.router.navigate(['clientes/crear']); }
  navegarLugar() { this.router.navigate(['lugares']); }
}