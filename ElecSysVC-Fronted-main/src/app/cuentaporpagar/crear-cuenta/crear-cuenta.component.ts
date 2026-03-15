import { Component } from '@angular/core';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CrearCuentaRequest, CuentaForm, DetalleCuentaForm } from '../Cuentas-Entidad/request-cuentadecobro';
import { EntidadDetalleCuenta } from '../Cuentas-Entidad/DetalleCuentaEntidad';
import { CuentaServiceService } from '../Cuentas-Entidad/cuenta-service.service';

@Component({
  selector: 'app-crear-cuenta',
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, CommonModule, FormsModule],
  templateUrl: './crear-cuenta.component.html',
  styleUrl: './crear-cuenta.component.css'
})
export class CrearCuentaComponent {

  constructor(private clienteService: ServiceClienteService,
    private cuentaService: CuentaServiceService,
    private route: Router) { }

  cuentaform: CuentaForm = {
    id_trabajador: 1,
    id_cliente: 0,
    nota: 'Favor consignar a la cuenta de ahorros banco Davivienda #477700032311 a nombre de VC ELECTRICOS CONSTRUCCIONES SAS con NIT. 900820830-1',
    fecha_realizacion: new Date().toISOString().split('T')[0],
    monto: 0,
    estado: 'PENDIENTE'
  }


  textoBusqueda = '';
  clientesFiltrados: EntidadCliente[] = [];
  clienteSeleccionado!: EntidadCliente;
  clientes: EntidadCliente[] = [];

  buscarClientes(): void {
  if (this.textoBusqueda.length < 2) {
    this.clientesFiltrados = [];
    return;
  }

  this.clienteService.buscarClienteQuery(this.textoBusqueda).subscribe({
    next: (resultados) => {
      this.clientesFiltrados = resultados.filter(c => c.estado?.toUpperCase() === 'ACTIVO');
    },
    error: () => this.clientesFiltrados = []
  });
}

  seleccionarCliente(cliente: EntidadCliente) {
    this.clienteSeleccionado = cliente;
    this.textoBusqueda = cliente.nombre;
    this.cuentaform.id_cliente = cliente.id_cliente;
    this.clientesFiltrados = [];
  }

  limpiarCliente() {
    this.clienteSeleccionado = undefined!;
    this.textoBusqueda = '';
  }

  referencia: string = '';

  detalles: DetalleCuentaForm[] = [];

  calcularTotal(): number {
    return this.detalles.reduce((sum, d) => sum + (d.valor || 0), 0);
  }


  agregarDetalle() {
    this.detalles.push({
      descripcion: '',
      valor: 0
    });
  }

  eliminarDetalle(index: number) {
    this.detalles.splice(index, 1);
  }

  private mapearDetalles(): EntidadDetalleCuenta[]{
    return this.detalles.map( det => ({
      id_detalle_cuenta: 0,
      id_cuenta_pagar: 0,
      descripcion: det.descripcion,
      valor: det.valor
    }))
  }

  guardarCuenta() {

  if (!this.cuentaform.id_cliente) {
    alert('Debe seleccionar un cliente');
    return;
  }

  if (this.detalles.length === 0) {
    alert('Debe agregar al menos un detalle');
    return;
  }
  const total = this.calcularTotal();
  this.cuentaform.monto = total;

  const request: CrearCuentaRequest = {
    cuentaPorPagarDTO: this.cuentaform,
    referencia: this.referencia,
    detalleCuentaDTOS: this.mapearDetalles()
  };

  console.log('REQUEST →', request);

  this.cuentaService.crearCuentaCobrar(request).subscribe({
    next: (pdf) => {
      const url = window.URL.createObjectURL(pdf);
      alert('Cuenta creada correctamente');
      this.route.navigate(['/cuentaspagar']);
      window.open(url);
    },
    error: (err) => {
      console.error(err);
      alert('Error al crear la cuenta');
    }
  });
}

  navegarCliente(){
    this.route.navigate(['clientes/crear']);
  }


}
