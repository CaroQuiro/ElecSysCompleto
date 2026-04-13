import { Component } from '@angular/core';
import { EntidadCotizaciones } from '../Cotizaciones/entidad-cotizaciones';
import { EntidadDetalleCotizacion } from '../Cotizaciones/entidad-detalleCotizacion';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { ServiceCotizacionesService } from '../Cotizaciones/service-cotizaciones.service';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { EntidadLugar } from '../../lugar/entidad-lugar';
import { CommonModule, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DetalleCotizacionForm, DetalleUI, AIUForm, CotizacionForm } from '../Cotizaciones/request-cotizacion';
import { NotificacionService } from '../../notificacion/notificacion.service';
import { AuthService } from '../../servicios/auth.service';


@Component({
  selector: 'app-ver-cotizacion',
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, NgForOf, CommonModule, FormsModule],
  templateUrl: './ver-cotizacion.component.html',
  styleUrl: './ver-cotizacion.component.css'
})
export class VerCotizacionComponent {

  cotizacion!: EntidadCotizaciones;
  cliente!: EntidadCliente;
  lugar!: EntidadLugar;
  detallecotizacion: EntidadDetalleCotizacion[] = [];

  cotizacionEditable!: EntidadCotizaciones;

  probabilidadCalculada: number = 0;

  constructor(private ruta: ActivatedRoute,
    private serviceCotizacion: ServiceCotizacionesService,
    private serviceCliente: ServiceClienteService,
    private serviceLugar: ServiceLugarService,
    private notificacionService: NotificacionService,
    private authService: AuthService,
    private route: Router) { }

  idCotizacion!: number;

  aiuForm: AIUForm = {
    administracion: 0,
    imprevistos: 0,
    utilidad: 0
  };

  ngOnInit() {
    this.idCotizacion = Number(this.ruta.snapshot.params['id']);

    this.serviceCotizacion.obtenerCotizacionPorId(this.idCotizacion).subscribe(cot => {
      this.cotizacion = cot;
      this.recargarDetalles();
      this.consultarProbabilidad();

      this.cotizacionEditable = structuredClone(cot);
      this.idClienteOriginal = cot.id_cliente;
      this.idlugarOriginal = cot.id_lugar;


      this.existIva = !(
        cot.administracion > 0 ||
        cot.imprevistos > 0 ||
        cot.utilidad > 0
      );
      this.serviceCliente
        .buscarClientePorId(cot.id_cliente)
        .subscribe(client => {
          this.cliente = client;
        });

      this.serviceLugar
        .buscarLugarPorId(cot.id_lugar)
        .subscribe(lugar => {
          this.lugar = lugar;
        });
    });
  }

  // Actualizar Seccion Cliente
  editCliente = false;
  textoBusquedaCliente = '';
  clientes: EntidadCliente[] = [];
  clienteSeleccionado?: EntidadCliente;
  idClienteOriginal!: number;

  editarCliente() {
    this.editCliente = true;
  }

  buscarCliente() {
    if (this.textoBusquedaCliente.length < 2) {
      this.clientes = [];
      return;
    }

    this.serviceCliente.buscarClienteQuery(this.textoBusquedaCliente).subscribe(res => {
      this.clientes = res;
    });
  }

  seleccionarCliente(cliente: EntidadCliente) {
    this.clienteSeleccionado = cliente;
    this.clientes = [];
  }

  clienteCambio(): boolean {
    return !!this.clienteSeleccionado &&
      this.clienteSeleccionado.id_cliente !== this.idClienteOriginal;
  }

  guardarCliente() {
    if (!this.clienteSeleccionado) return;
    this.cotizacionEditable.id_cliente = this.clienteSeleccionado.id_cliente;
    this.cliente = this.clienteSeleccionado;
    this.idClienteOriginal = this.cliente.id_cliente;


    this.editCliente = false;
    this.textoBusquedaCliente = '';
    this.clientes = [];
    this.clienteSeleccionado = undefined;
  }

  cancelarCliente() {
    this.editCliente = false;
    this.clienteSeleccionado = undefined;
    this.textoBusquedaCliente = '';
    this.clientes = [];
  }


  //Actualizar Seccion Lugar
  editLugar = false;
  textoBusquedaLugar = '';
  lugares: EntidadLugar[] = [];
  lugarSeleccionado?: EntidadLugar;
  idlugarOriginal!: number;

  editarLugar() {
    this.editLugar = true;
  }

  buscarLugar() {
    if (this.textoBusquedaLugar.length < 2) {
      this.lugares = [];
      return;
    }

    this.serviceLugar.buscarLugarQuery(this.textoBusquedaLugar).subscribe(res => {
      this.lugares = res;
    });
  }

  seleccionarLugar(lugar: EntidadLugar) {
    this.lugarSeleccionado = lugar;
    this.lugares = [];
  }

  lugarCambio(): boolean {
    return !!this.lugarSeleccionado &&
      this.lugarSeleccionado.idLugar !== this.idlugarOriginal;
  }

  guardarLugar() {
    if (!this.lugarSeleccionado) return;
    this.cotizacionEditable.id_lugar = this.lugarSeleccionado.idLugar;
    this.lugar = this.lugarSeleccionado;
    this.idlugarOriginal = this.lugar.idLugar;


    this.editLugar = false;
    this.textoBusquedaLugar = '';
    this.lugares = [];
    this.lugarSeleccionado = undefined;
  }

  cancelarLugar() {
    this.editLugar = false;
    this.textoBusquedaLugar = '';
    this.lugares = [];
    this.lugarSeleccionado = undefined;
  }

  //Actualizar Seccion de Detalles
  editDetalle = false;
  editdetallecotizacion: DetalleUI[] = [];

  editarFila(det: DetalleUI) {
    det.editando = true;
  }

  agregarFila() {
    this.editdetallecotizacion.push({
      id_detalle_cotizacion: 0,
      descripcion: '',
      cantidad: 1,
      valor_unitario: 0,
      subtotal: 0,
      editando: true,
      esNuevo: true
    });
  }

  recargarDetalles() {
    if (!this.cotizacion?.id_cotizacion) return;

    this.serviceCotizacion
      .obtenerDetalleCotizacionPorId(this.idCotizacion)
      .subscribe(detalle => {
        this.detallecotizacion = detalle;

        this.editdetallecotizacion = detalle.map(d => ({
          ...d,
          editando: false,
          esNuevo: false
        }));
        this.cargarAIUDesdeCotizacion();
      });
  }


  guardarFila(det: DetalleUI) {
    const dto: DetalleCotizacionForm = {
      descripcion: det.descripcion,
      cantidad: det.cantidad,
      valor_unitario: det.valor_unitario
    };

    const peticion = det.esNuevo
      ? this.serviceCotizacion.crearDetalleCotizacion(this.idCotizacion, dto)
      : this.serviceCotizacion.actualizarDetalleCotizacion(this.idCotizacion, det.id_detalle_cotizacion, dto);

    peticion.subscribe({
      next: () => {
        alert(det.esNuevo ? 'Detalle Creado' : 'Detalle Actualizado');
        det.editando = false;
        det.esNuevo = false;

        // 1. Recargamos los detalles para tener los IDs nuevos
        this.recargarDetalles();

        // 2. IMPORTANTE: Llamamos a actualizar la cotización global 
        // para que el TOTAL_PAGAR en la BD se refresque con los nuevos ítems
        this.actualizarCotizacionSilenciosa();
      }
    });
  }

  // Nuevo método para actualizar totales sin saltar a la lista
  private actualizarCotizacionSilenciosa(): void {
    const esAIU = !this.existIva;
    const dto: EntidadCotizaciones = {
      ...this.baseCotizacion(),
      tiene_aiu: esAIU,
      iva: esAIU ? this.ivaSobreUtilidad : this.ivaDirecto,
      total_pagar: esAIU ? this.totalAIU : this.TotalconIva,
      administracion: esAIU ? this.administracion : 0,
      imprevistos: esAIU ? this.imprevistos : 0,
      utilidad: esAIU ? this.utilidad : 0
    };

    this.serviceCotizacion.actualizarCotizacion(this.idCotizacion, dto).subscribe();
  }

  eliminarFila(det: DetalleUI, index: number) {

    if (det.esNuevo) {
      this.editdetallecotizacion.splice(index, 1);
      return;
    }

    this.serviceCotizacion
      .borrarDetalleCotizacion(this.idCotizacion, det.id_detalle_cotizacion)
      .subscribe(() => {
        this.editdetallecotizacion.splice(index, 1);
        alert('Detalle Eliminado Exitosamente');
        this.editdetallecotizacion = [];
        this.recargarDetalles();
        this.actualizarCotizacionSilenciosa();
      });
  }


  cancelarFila(det: DetalleUI, index: number) {

    // si era nuevo → lo quitamos
    if (det.esNuevo) {
      this.editdetallecotizacion.splice(index, 1);
    } else {
      det.editando = false;
      this.recargarDetalles();
    }
  }


  existIva = true;

  private cargarAIUDesdeCotizacion(): void {
    if (!this.cotizacion) return;

    const subtotal = this.subtotal;

    if (subtotal <= 0) {
      this.aiuForm = { administracion: 0, imprevistos: 0, utilidad: 0 };
      return;
    }

    this.aiuForm = {
      administracion: (this.cotizacion.administracion / subtotal) * 100,
      imprevistos: (this.cotizacion.imprevistos / subtotal) * 100,
      utilidad: (this.cotizacion.utilidad / subtotal) * 100
    };
  }

  get subtotal(): number {
    return this.editdetallecotizacion.reduce(
      (sum, d) => sum + (d.cantidad * d.valor_unitario), 0
    );
  }

  get administracion(): number {
    if (this.existIva) return 0;
    return this.subtotal * (this.aiuForm.administracion / 100);
  }

  get ivaDirecto(): number {
    if (!this.existIva) return 0;
    return this.subtotal * 0.19;
  }

  get TotalconIva(): number {
    if (!this.existIva) return 0;
    return this.subtotal + this.ivaDirecto;
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

  private baseCotizacion(): EntidadCotizaciones {
    return {
      id_cotizacion: this.cotizacion.id_cotizacion,
      id_trabajador: this.cotizacion.id_trabajador,
      id_cliente: this.cotizacionEditable.id_cliente,
      id_lugar: this.cotizacionEditable.id_lugar,
      fecha_realizacion: new Date().toISOString().split('T')[0],
      referencia: this.cotizacion.referencia,
      valor_total: this.subtotal,
      estado: this.cotizacionEditable.estado,
      administracion: 0,
      imprevistos: 0,
      utilidad: 0,
      iva: 0,
      total_pagar: 0,
      tiene_aiu: false
    };
  }

  actualizarCotizacion(): void {
    const esAIU = !this.existIva;

    const dto: EntidadCotizaciones = {
      ...this.baseCotizacion(),
      tiene_aiu: esAIU
    };

    if (esAIU) {
      dto.administracion = this.administracion;
      dto.imprevistos = this.imprevistos;
      dto.utilidad = this.utilidad;
      dto.iva = this.ivaSobreUtilidad;
      dto.total_pagar = this.totalAIU;
    } else {
      dto.administracion = 0;
      dto.imprevistos = 0;
      dto.utilidad = 0;
      dto.iva = this.ivaDirecto;
      dto.total_pagar = this.TotalconIva;
    }

    this.serviceCotizacion
      .actualizarCotizacion(this.idCotizacion, dto)
      .subscribe({
        next: () => {
          alert('Cotización Actualizada correctamente');
          this.cotizacion = { ...this.cotizacion, ...dto };
          this.descargarPDF(this.idCotizacion);
          this.route.navigate(['cotizaciones']);
        },
        error: err => {
          console.error(err);
          alert('Error al guardar la cotización');
        }
      });
  }

  autoResize(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
  }


  descargarPDF(id: number): void {
    this.serviceCotizacion.descargarpdf(id).subscribe({
      next: (pdf) => {
        const url = window.URL.createObjectURL(pdf);
        window.open(url);
      }, error: err => {
        console.error('Error al descargar el PDF:', err);
        alert('No se pudo generar el PDF en este momento.');
      }
    });
  }

  enviarEmail() {
    // 1. Crear un input de tipo file invisible para que el usuario elija el archivo
    const inputArchivo = document.createElement('input');
    inputArchivo.type = 'file';
    inputArchivo.accept = '.pdf, .doc, .docx'; // Opcional: limitar extensiones

    inputArchivo.onchange = (e: any) => {
      const archivo = e.target.files[0];
      if (archivo) {
        this.procesarEnvio(archivo);
      }
    };

    inputArchivo.click();
  }

  procesarEnvio(archivo: File) {
    const formData = new FormData();

    const mensajeHtml = `
      <div style="font-family: Arial, sans-serif; color: #333; line-height: 1.6; max-width: 600px;">
        <h2 style="color: #0056b3;">Estimado/a ${this.cliente.nombre},</h2>
        <p>Es un gusto saludarle.</p>
        <p>Adjunto a este correo encontrará la <strong>Cotización #${this.cotizacion.id_cotizacion}</strong> detallada, 
        realizada por <strong>VC Eléctricos Construcciones S.A.S.</strong> de acuerdo con lo solicitado.</p>
        
        <div style="background-color: #f8f9fa; padding: 15px; border-left: 4px solid #0056b3; margin: 20px 0;">
          <p style="margin: 0;"><strong>Referencia del proyecto:</strong> ${this.cotizacion.referencia}</p>
          <p style="margin: 0;"><strong>Valor Total:</strong> ${new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(this.TotalconIva)}</p>
        </div>

        <p>Si tiene alguna duda o requiere un ajuste adicional, no dude en contactarnos a los siguientes numeros: 3284892384.</p>
        
        <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0;">
        <p style="font-size: 12px; color: #777;">
          Atentamente,<br>
          <strong>Departamento Comercial</strong><br>
          VC Eléctricos Construcciones S.A.S.<br>
          <em>Este es un correo automático, por favor no responda directamente si no es para temas de la cotización.</em>
        </p>
      </div>
    `;

    // Datos extraídos de los objetos 'cliente' y 'cotizacion' que ya tienes en el componente
    formData.append('nombreUsuario', this.cliente.nombre);
    formData.append('correo', this.cliente.correo);
    formData.append('asunto', `Cotización #${this.cotizacion.id_cotizacion} - VC Eléctricos Construcciones S.A.S.`);
    formData.append('mensaje', mensajeHtml);
    formData.append('archivo', archivo);

    this.notificacionService.enviarCorreo(formData).subscribe({
      next: (res: any) => {
        alert(res.mensaje); // Ahora usamos el mensaje que viene del JSON
      },
      error: (err) => {
        // Si sigue saliendo error, imprime esto en consola para saber QUÉ es
        console.log("Status del error:", err.status);
        console.log("Cuerpo del error:", err.error);
        alert('Error al enviar el correo. Revisa la consola.');
      }
    });
  }

  consultarProbabilidad() {
    this.serviceCotizacion.obtenerProbabilidad(this.idCotizacion).subscribe({
      next: (res) => {
        this.probabilidadCalculada = res.probabilidad_aceptacion || 0.01;
      },
      error: (err) => {
        console.error('El usuario no tiene permiso para ver la IA:', err);
        this.probabilidadCalculada = 0;
      }
    });
  }

  get esAdmin(): boolean {
    return this.authService.getUserRole().toUpperCase() === 'ADMIN';
  }

  get esEditable(): boolean {
    return this.cotizacion?.estado === 'PENDIENTE';
  }

  borrarCotizacionCompleta() {
    if (!this.esAdmin) {
      alert("No tienes permisos de administrador para realizar esta acción.");
      return;
    }

    if (confirm(`¿Estás seguro de que deseas eliminar la cotización #${this.idCotizacion}? Esta acción no se puede deshacer.`)) {
      this.serviceCotizacion.borrarCotizacion(this.idCotizacion).subscribe({
        next: () => {
          alert("Cotización eliminada exitosamente.");
          this.route.navigate(['/cotizaciones']);
        },
        error: (err) => {
          console.error(err);
          alert("Error al intentar eliminar la cotización.");
        }
      });
    }
  }

}
