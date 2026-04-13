import { Component, inject, Inject } from '@angular/core';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { FormsModule } from '@angular/forms';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { EntidadLugar } from '../../lugar/entidad-lugar';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { AIUForm, CotizacionForm, DetalleCotizacionForm, RequestCotizacion } from '../Cotizaciones/request-cotizacion';
import { ServiceCotizacionesService } from '../Cotizaciones/service-cotizaciones.service';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-crear-cot',
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, FormsModule, NgIf, NgFor, CommonModule],
  templateUrl: './crear-cot.component.html',
  styleUrl: './crear-cot.component.css'
})
export class CrearCotComponent {

  textoBusquedaCliente = '';
  textoBusquedaLugar = '';
  clientes: EntidadCliente[] = [];
  lugares: EntidadLugar[] = [];
  clienteSeleccionado!: EntidadCliente;
  lugarSeleccionado!: EntidadLugar;

  detalles: DetalleCotizacionForm[] = [];

  cotizacionForm: CotizacionForm = {
    id_cotizacion: 2,
    id_trabajador: 1, // luego vendrá del login
    id_cliente: 0,
    id_lugar: 0,
    fecha_realizacion: new Date().toISOString().split('T')[0],
    referencia: '',
    estado: 'PENDIENTE'
  };

  existIva = true;

  aiuForm: AIUForm = {
    administracion: 0,
    imprevistos: 0,
    utilidad: 0
  };

  constructor(private clienteService: ServiceClienteService, private lugarService: ServiceLugarService,
    private cotizacionService: ServiceCotizacionesService, private route: Router, private login: AuthService) { }

  buscarCliente() {
    if (this.textoBusquedaCliente.length < 2) return;
    this.clienteService.buscarClienteQuery(this.textoBusquedaCliente).subscribe({
      next: resultado => {
        this.clientes = resultado.filter(c => c.estado?.toUpperCase() === 'ACTIVO');
      },
      error: () => this.clientes = []
    });
  }

  seleccionarCliente(cliente: EntidadCliente) {
    this.clienteSeleccionado = cliente;
    this.clientes = [];
    console.log(cliente.id_cliente);
  }

  buscarLugar() {
    if (this.textoBusquedaLugar.length < 2) return;
    this.lugarService.buscarLugarQuery(this.textoBusquedaLugar).subscribe({
      next: resultado =>
        this.lugares = resultado,
      error: () => this.lugares = []
    })
  }

  seleccionarLugar(lugar: EntidadLugar) {
    this.lugarSeleccionado = lugar;
    this.lugares = [];
  }

  agregarDetalle() {
    this.detalles.push({
      descripcion: '',
      cantidad: 1,
      valor_unitario: 0
    });
  }

  eliminarDetalle(index: number) {
    this.detalles.splice(index, 1);
  }

  get subtotal(): number {
    return this.detalles.reduce(
      (sum, d) => sum + (d.cantidad * d.valor_unitario), 0
    );
  }

  get ivaDirecto(): number {
    if (!this.existIva) return 0;
    return this.subtotal * 0.19;
  }

  get TotalconIva(): number {
    if (!this.existIva) return 0;
    return this.subtotal + this.ivaDirecto;
  }

  get administracion(): number {
    if (this.existIva) return 0;
    return this.subtotal * (this.aiuForm.administracion / 100);
  }

  get imprevistos(): number {
    if (this.existIva) return 0;
    return this.subtotal * (this.aiuForm.imprevistos / 100);
  }

  get utilidad(): number {
    if (this.existIva) return 0;
    return this.subtotal * (this.aiuForm.utilidad / 100);
  }

  get ivaSobreUtilidad(): number {
    if (this.existIva) return 0;
    return this.utilidad * 0.19;
  }

  get totalAIU(): number {
    if (this.existIva) return 0;
    return this.subtotal +
      this.administracion +
      this.imprevistos +
      this.utilidad +
      this.ivaSobreUtilidad;
  }

  guardarCotizacion() {
    if (!this.validarClienteYLugar()) return;
    if (!this.validarDetalles()) return;
    if (!this.validarAIU()) return;

    const request: RequestCotizacion = {
      cotizacion: {
        id_cotizacion: 2,
        id_trabajador: this.login.obtenerIDLogin() ?? 0,
        id_cliente: this.clienteSeleccionado.id_cliente,
        id_lugar: this.lugarSeleccionado.idLugar,
        fecha_realizacion: new Date().toISOString().split('T')[0],
        referencia: this.cotizacionForm.referencia,
        estado: 'PENDIENTE'
      },
      detalleCotizacionDTOS: this.detalles,
      existIva: this.existIva
    };

    if (!this.existIva) {
      request.aiudto = {
        administracion: this.aiuForm.administracion / 100,
        imprevistos: this.aiuForm.imprevistos / 100,
        utilidad: this.aiuForm.utilidad / 100
      };
    }

    this.cotizacionService.crearCotizacion(request).subscribe({
      next: (pdf) => {
        const url = window.URL.createObjectURL(pdf);
        alert('Cotizacion Creada Correctamente');
        this.route.navigate(['cotizaciones']);
        window.open(url);
      },
      error: err => console.error(err)
    });
  }


  validarClienteYLugar(): boolean {
    if (!this.clienteSeleccionado) {
      alert('Debe seleccionar un cliente');
      return false;
    }

    if (!this.lugarSeleccionado) {
      alert('Debe seleccionar un lugar');
      return false;
    }
    return true;
  }


  validarAIU(): boolean {
    if (this.existIva) return true;
    const { administracion, imprevistos, utilidad } = this.aiuForm;
    if (
      administracion <= 0 ||
      imprevistos <= 0 ||
      utilidad <= 0
    ) {
      alert('Todos los valores AIU deben ser mayores a 0');
      return false;
    }
    return true;
  }

  validarDetalles(): boolean {
    if (this.detalles.length === 0) {
      alert('Debe agregar al menos un detalle');
      return false;
    }

    for (const d of this.detalles) {
      if (!d.descripcion || d.descripcion.trim().length === 0) {
        alert('Todos los detalles deben tener descripción');
        return false;
      }

      if (d.cantidad <= 0) {
        alert('La cantidad debe ser mayor a 0');
        return false;
      }

      if (d.valor_unitario <= 0) {
        alert('El valor unitario debe ser mayor a 0');
        return false;
      }
    }
    return true;
  }

  autoResize(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
  }

  navegarCliente() {
    this.route.navigate(['clientes/crear']);
  }

  navegarLugares() {
    this.route.navigate(['lugares']);
  }
}
