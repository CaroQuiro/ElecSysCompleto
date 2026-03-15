import { Component, OnInit, inject } from '@angular/core';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { CuentaServiceService } from '../Cuentas-Entidad/cuenta-service.service';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { ActivatedRoute, Router } from '@angular/router';
import { EntidadCuentasPagar } from '../Cuentas-Entidad/CuentaPagar-Entidad';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DetalleCuentaUI } from '../Cuentas-Entidad/request-cuentadecobro';
import { EntidadDetalleCuenta } from '../Cuentas-Entidad/DetalleCuentaEntidad';
import { NotificacionService } from '../../notificacion/notificacion.service';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-ver-cuenta',
  standalone: true,
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, FormsModule, CommonModule],
  templateUrl: './ver-cuenta.component.html',
  styleUrl: './ver-cuenta.component.css'
})
export class VerCuentaComponent implements OnInit {

  constructor(
    private cuentaService: CuentaServiceService,
    private clienteService: ServiceClienteService,
    private authService: AuthService,
    private ruta: ActivatedRoute, 
    private route: Router
  ) { }

  private notificacionService = inject(NotificacionService);

  cuentapagar!: EntidadCuentasPagar;
  cliente!: EntidadCliente;
  idCuentaPorPagar!: number;
  cuentaporpagarEditable!: EntidadCuentasPagar;
  detalleCuenta: EntidadDetalleCuenta[] = [];

  ngOnInit() {
    this.idCuentaPorPagar = this.ruta.snapshot.params['id'];
    this.cuentaService.obtenerCuentaPagarID(this.idCuentaPorPagar)
      .subscribe(cuenta => {
        this.cuentapagar = cuenta;
        this.recargarDetallesCuenta();
        this.cuentaporpagarEditable = structuredClone(cuenta);
        this.idClienteOriginal = cuenta.id_cliente;

        this.clienteService.buscarClientePorId(cuenta.id_cliente)
          .subscribe(client => { this.cliente = client });
      });
  }

  // GETTERS DE CONTROL
  get esAdmin(): boolean {
    return this.authService.getUserRole().toUpperCase() === 'ADMIN';
  }

  get esEditable(): boolean {
    // REGLA: Si está PAGADO, no es editable bajo ninguna circunstancia
    return this.cuentapagar?.estado !== 'PAGADO';
  }

  // CLIENTE
  editCliente = false;
  textoBusquedaCliente = '';
  clientes: EntidadCliente[] = [];
  idClienteOriginal!: number;

  editarCliente() {
    if (!this.esEditable) return;
    this.editCliente = true;
  }

  buscarClientes(): void {
    if (!this.esEditable || this.textoBusquedaCliente.length < 2) {
      this.clientes = [];
      return;
    }

    this.clienteService.buscarClienteQuery(this.textoBusquedaCliente).subscribe({
      next: (resultados) => {
        // REGLA: Solo buscar clientes ACTIVOS
        this.clientes = resultados.filter(c => c.estado?.toUpperCase() === 'ACTIVO');
      },
      error: () => this.clientes = []
    });
  }

  seleccionarCliente(cliente: EntidadCliente) {
    this.cuentaporpagarEditable.id_cliente = cliente.id_cliente;
    this.cliente = cliente;
    this.editCliente = false;
    this.textoBusquedaCliente = '';
  }

  clienteCambio(): boolean {
    return this.cliente?.id_cliente !== this.idClienteOriginal;
  }

  // DETALLES
  editDetalleCuenta: DetalleCuentaUI[] = [];

  recargarDetallesCuenta() {
    this.cuentaService.obtenerDetalleCuentaPorId(this.idCuentaPorPagar).subscribe(detalle => {
      this.editDetalleCuenta = detalle.map(d => ({ ...d, editando: false, esNuevo: false }));
    });
  }

  editarFila(det: DetalleCuentaUI) {
    if (this.esEditable) det.editando = true;
  }

  agregarFila() {
    if (!this.esEditable) return;
    this.editDetalleCuenta.push({ id_detalle_cuenta: 0, descripcion: '', valor: 0, editando: true, esNuevo: true });
  }

  guardarFila(det: DetalleCuentaUI) {
    const dto = { id_detalle_cuenta: det.id_detalle_cuenta, id_cuenta_pagar: this.idCuentaPorPagar, descripcion: det.descripcion, valor: det.valor };
    const request = det.esNuevo ? this.cuentaService.crearDetalleCuenta(this.idCuentaPorPagar, dto) : this.cuentaService.actualizarDetalleCuenta(this.idCuentaPorPagar, det.id_detalle_cuenta, dto);

    request.subscribe({
      next: () => {
        alert('Guardado exitoso');
        this.recargarDetallesCuenta();
      }
    });
  }

  eliminarFila(det: DetalleCuentaUI, index: number) {
    if (!this.esEditable) return;
    if (!det.esNuevo && this.editDetalleCuenta.length <= 1) {
      alert("Mínimo debe existir un detalle.");
      return;
    }
    if (det.esNuevo) { this.editDetalleCuenta.splice(index, 1); return; }
    this.cuentaService.borrarDetalleCotizacion(this.idCuentaPorPagar, det.id_detalle_cuenta).subscribe(() => this.recargarDetallesCuenta());
  }

  cancelarFila(det: DetalleCuentaUI, index: number) {
    if (det.esNuevo) this.editDetalleCuenta.splice(index, 1);
    else { det.editando = false; this.recargarDetallesCuenta(); }
  }

  get valorApagar(): number {
    return this.editDetalleCuenta.reduce((acc, d) => acc + (d.valor || 0), 0);
  }

  // ACCIONES FINALES
  actualizarCuenta(): void {
    if (!this.esEditable) return;
    const dto = { ...this.cuentapagar, ...this.cuentaporpagarEditable, monto: this.valorApagar };
    this.cuentaService.actualizarCuentaPagar(this.idCuentaPorPagar, dto).subscribe({
      next: () => {
        alert('Actualización Exitosa');
        this.route.navigate(['cuentaspagar']);
      }
    });
  }

  borrarCuentaCompleta() {
    if (!this.esAdmin || !this.esEditable) return;
    if (confirm("¿Desea eliminar esta cuenta?")) {
      this.cuentaService.borrarCuentaPagar(this.idCuentaPorPagar).subscribe(() => this.route.navigate(['cuentaspagar']));
    }
  }

enviarEmail() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf';
    input.onchange = (e: any) => {
      const file = e.target.files[0];
      if (file && this.cliente) this.procesarEnvio(file);
    };
    input.click();
  }

  procesarEnvio(archivo: File) {
  const fd = new FormData();
  
  const mensajeHtml = `
    <div style="font-family: Arial, sans-serif; max-width: 600px; border: 1px solid #ddd; border-radius: 10px; overflow: hidden;">
      <div style="background-color: #b80000; color: white; padding: 25px; text-align: center;">
        <h2 style="margin:0;">Cuenta de Cobro - ElecSys</h2>
        <p style="margin:5px 0 0; opacity: 0.8;">VC Eléctricos Construcciones S.A.S.</p>
      </div>
      <div style="padding: 30px; color: #333;">
        <h3>Estimado/a ${this.cliente?.nombre},</h3>
        <p>Le informamos que se ha generado un reporte de cobro asociado a sus servicios actuales.</p>
        <div style="background: #f9f9f9; padding: 20px; border-radius: 8px; border-left: 5px solid #b80000; margin: 20px 0;">
          <p><strong>Cuenta N°:</strong> ${this.cuentapagar.id_cuenta_pagar}</p>
          <p><strong>Monto Total:</strong> ${new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(this.valorApagar)}</p>
          <p><strong>Referencia:</strong> ${this.cuentaporpagarEditable.referencia || 'N/A'}</p>
        </div>
        <p style="font-size: 13px; color: #666;"><em>Nota: ${this.cuentaporpagarEditable.nota}</em></p>
        <p>Adjunto encontrará el documento detallado en formato PDF.</p>
      </div>
      <div style="background: #f4f4f4; padding: 15px; text-align: center; font-size: 11px; color: #999;">
        Este es un mensaje automático. Por favor no responda a esta dirección de correo.
      </div>
    </div>
  `;

  fd.append('nombreUsuario', this.cliente!.nombre);
  fd.append('correo', this.cliente!.correo);
  fd.append('asunto', `Cuenta de Cobro #${this.cuentapagar.id_cuenta_pagar} - VC Eléctricos`);
  fd.append('mensaje', mensajeHtml);
  fd.append('archivo', archivo);

  this.notificacionService.enviarCorreo(fd).subscribe({
    next: () => alert("Reporte enviado exitosamente al cliente."),
    error: () => alert("Error al conectar con el servidor de correos.")
  });
}

}
