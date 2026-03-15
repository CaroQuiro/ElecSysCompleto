import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { OrdenTrabajoService } from '../data/orden-trabajo.service';
import { OrdenVisitaService } from '../../ordenVisita/data/orden-visita.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { EntidadLugar } from '../../lugar/entidad-lugar';
import { OrdenDeTrabajoDTO, DetalleOrdenTrabajoDTO, OrdenDeTrabajoRequest } from '../data/orden-trabajo.models';
import { OrdenDeVisitaDTO } from '../../ordenVisita/data/orden-visita.models';

@Component({
  selector: 'app-ordenes-trabajo-crear',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ordenes-trabajo-crear.component.html',
  styleUrl: './ordenes-trabajo-crear.component.css'
})
export class OrdenesTrabajoCrearComponent implements OnInit {
  // Inyección de Servicios
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private ordenTrabajoService = inject(OrdenTrabajoService);
  private ordenVisitaService = inject(OrdenVisitaService);
  private router = inject(Router);

  // Datos para listas desplegables y búsquedas
  ordenesVisita: OrdenDeVisitaDTO[] = [];
  clientes: EntidadCliente[] = [];
  lugares: EntidadLugar[] = [];

  // Variables de estado de selección
  textoBusquedaCliente = '';
  textoBusquedaLugar = '';
  clienteSeleccionado?: EntidadCliente;
  lugarSeleccionado?: EntidadLugar;

  // Formulario principal inicializado profesionalmente
  ordenForm: OrdenDeTrabajoDTO = {
    id_lugar: 0,
    id_cliente: 0,
    id_trabajador: 1, // Nota: Este ID debería venir del servicio de autenticación/usuario
    fecha_realizacion: new Date().toISOString().split('T')[0],
    estado: 'PENDIENTE',
    id_orden_visita: null // Inicializado en null para evitar error de llave foránea (0)
  };

  // Detalles iniciales de la labor
  detalles: DetalleOrdenTrabajoDTO[] = [
    { actividad: '', observaciones: '', duracion: '' }
  ];

  ngOnInit(): void {
    this.cargarVisitasDisponibles();
  }

  /**
   * Obtiene todas las órdenes de visita para la lista desplegable
   */
  cargarVisitasDisponibles(): void {
    this.ordenVisitaService.listarOrdenes().subscribe({
      next: (datos) => this.ordenesVisita = datos,
      error: (err) => console.error("Error al cargar visitas previas:", err)
    });
  }

  /**
   * Detecta el cambio en el selector de visitas y auto-completa Cliente y Lugar
   */
  onVisitaChange(): void {
    if (!this.ordenForm.id_orden_visita) {
      // Si se selecciona "Sin visita", reseteamos la selección actual para manual
      this.clienteSeleccionado = undefined;
      this.lugarSeleccionado = undefined;
      return;
    }

    // Buscamos la visita en nuestra lista local
    const visitaRef = this.ordenesVisita.find(v => v.idVisita === Number(this.ordenForm.id_orden_visita));
    
    if (visitaRef) {
      // 1. Cargamos y asignamos el Cliente automáticamente
      this.clienteService.buscarClientePorId(visitaRef.idCliente).subscribe(c => {
        this.clienteSeleccionado = c;
        this.ordenForm.id_cliente = c.id_cliente;
      });

      // 2. Cargamos y asignamos el Lugar automáticamente
      this.lugarService.buscarLugarPorId(visitaRef.idLugar).subscribe(l => {
        this.lugarSeleccionado = l;
        this.ordenForm.id_lugar = l.idLugar;
      });
    }
  }

  // --- LÓGICA DE BÚSQUEDA MANUAL (Si no hay visita de referencia) ---

  buscarCliente(): void {
  if (this.textoBusquedaCliente.length < 2) {
    this.clientes = [];
    return;
  }
  this.clienteService.buscarClienteQuery(this.textoBusquedaCliente).subscribe(res => {
    this.clientes = res.filter(c => c.estado === 'ACTIVO');
  });
}

  seleccionarCliente(c: EntidadCliente): void {
    this.clienteSeleccionado = c;
    this.ordenForm.id_cliente = c.id_cliente;
    this.clientes = [];
    this.textoBusquedaCliente = '';
  }

  buscarLugar(): void {
    if (this.textoBusquedaLugar.length < 2) {
      this.lugares = [];
      return;
    }
    this.lugarService.buscarLugarQuery(this.textoBusquedaLugar).subscribe(res => this.lugares = res);
  }

  seleccionarLugar(l: EntidadLugar): void {
    this.lugarSeleccionado = l;
    this.ordenForm.id_lugar = l.idLugar;
    this.lugares = [];
    this.textoBusquedaLugar = '';
  }

  // --- GESTIÓN DE TABLA DE ACTIVIDADES ---

  agregarDetalle(): void {
    this.detalles.push({ actividad: '', observaciones: '', duracion: '' });
  }

  eliminarDetalle(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.splice(index, 1);
    } else {
      alert("La orden de trabajo debe contener al menos una labor técnica.");
    }
  }

  // --- ACCIONES FINALES ---

  guardarOrden(): void {
    // Validaciones de integridad
    if (!this.clienteSeleccionado || !this.lugarSeleccionado) {
      alert("Error: Debe seleccionar un cliente y una ubicación válida.");
      return;
    }

    // Asegurar que id_orden_visita viaje como null si no hay selección (evita error FK 0 en Postgres)
    if (!this.ordenForm.id_orden_visita || this.ordenForm.id_orden_visita === 0) {
        this.ordenForm.id_orden_visita = null;
    }

    const request: OrdenDeTrabajoRequest = { 
      orden: this.ordenForm, 
      detalles: this.detalles 
    };

    this.ordenTrabajoService.agregarOrden(request).subscribe({
      next: (res) => {
        alert(res); // Mensaje de éxito del backend
        this.router.navigate(['/ordenes-trabajo']);
      },
      error: (err) => {
        console.error("Error crítico al guardar la orden de trabajo:", err);
        alert("Hubo un error al procesar la solicitud. Verifique los datos.");
      }
    });
  }

  // --- NAVEGACIÓN ---

  navegarCliente(): void {
    this.router.navigate(['clientes/crear']);
  }

  navegarLugar(): void {
    this.router.navigate(['lugares']);
  }
}