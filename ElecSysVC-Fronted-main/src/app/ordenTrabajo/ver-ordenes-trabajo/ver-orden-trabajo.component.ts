import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { OrdenTrabajoService } from '../data/orden-trabajo.service';
import { OrdenDeTrabajoDTO, DetalleTrabajoUI } from '../data/orden-trabajo.models';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { NotificacionService } from '../../notificacion/notificacion.service';
import { AuthService } from '../../servicios/auth.service';


@Component({
  selector: 'app-ver-orden-trabajo',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ver-orden-trabajo.component.html',
  styleUrl: './ver-orden-trabajo.component.css'
})
export class VerOrdenTrabajoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ordenService = inject(OrdenTrabajoService);
  private clienteService = inject(ServiceClienteService);
  private notificacionService = inject(NotificacionService);
  private authService = inject(AuthService);

  idOrden!: number;
  orden?: OrdenDeTrabajoDTO;
  editDetalles: DetalleTrabajoUI[] = [];
  cliente?: EntidadCliente;

  esAdmin: boolean = false;
  puedeEditar: boolean = false;

  ngOnInit() {
    this.idOrden = Number(this.route.snapshot.params['id']);
    this.cargarDatos();
  }

  cargarDatos() {
  this.esAdmin = this.authService.getUserRole() === 'ADMIN';
  
  this.ordenService.buscarPorId(this.idOrden).subscribe(data => {
    this.orden = data;
    this.puedeEditar = (data.estado !== 'REALIZADA' && data.estado !== 'CANCELADA');
    
    this.clienteService.buscarClientePorId(data.id_cliente).subscribe(c => this.cliente = c);
    this.recargarDetalles();
  });
}

  recargarDetalles() {
    this.ordenService.listarDetallesPorOrden(this.idOrden).subscribe(res => {
      this.editDetalles = res.map(d => ({ ...d, editando: false }));
    });
  }

  editarFila(d: DetalleTrabajoUI) { d.editando = true; }
  
  cancelarFila(d: DetalleTrabajoUI, index: number) {
  if (d.esNuevo) {
    this.editDetalles.splice(index, 1);
  } else {
    d.editando = false;
    this.recargarDetalles();
  }
}

  guardarFila(d: DetalleTrabajoUI) {
    if (d.esNuevo) this.ordenService.agregarDetalle(this.idOrden, d).subscribe(() => this.recargarDetalles());
    else this.ordenService.actualizarDetalle(d.idDetalleTrabajo!, d).subscribe(() => d.editando = false);
  }

  eliminarFila(d: DetalleTrabajoUI, index: number) {
    if (this.editDetalles.length <= 1) {
      alert("Debe quedar al menos una actividad.");
      return;
    }
    if (confirm("¿Eliminar actividad?")) {
      if (d.esNuevo) this.editDetalles.splice(index, 1);
      else this.ordenService.borrarDetalle(d.idDetalleTrabajo!).subscribe(() => this.recargarDetalles());
    }
  }

  actualizarOrden() {
    if (this.orden) this.ordenService.actualizarOrden(this.idOrden, this.orden).subscribe(() => {
      alert("Orden actualizada");
      this.router.navigate(['/ordenes-trabajo']);
    });
  }

  borrarOrden() {
    if (confirm("¿Borrar orden completa?")) this.ordenService.borrarOrden(this.idOrden).subscribe(() => this.router.navigate(['/ordenes-trabajo']));
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
  
  const asunto = `Reporte de Servicio Técnico - Orden de Trabajo #${this.idOrden} - VC Eléctricos`;

  // Usamos etiquetas HTML para asegurar los espacios y saltos de línea
  const mensaje = `
    <p>Estimado(a) <strong>${this.cliente!.nombre}</strong>,</p>

    <p>Reciba un cordial saludo de parte de <strong>VC Eléctricos Construcciones S.A.S.</strong></p>

    <p>Por medio del presente, hacemos entrega formal del reporte técnico detallado correspondiente a la 
    <strong>Orden de Trabajo #${this.idOrden}</strong>, la cual fue realizada en sus instalaciones.</p>

    <p>En el documento adjunto encontrará:<br>
    • El desglose de las actividades técnicas ejecutadas.<br>
    • Observaciones encontradas durante la prestación del servicio.<br>
    • Duración y estado final del trabajo.</p>

    <p>Agradecemos la confianza depositada en nuestro equipo profesional. Si requiere información adicional 
    o tiene alguna inquietud técnica sobre este reporte, no dude en contactarnos respondiendo a este correo.</p>

    <p>Atentamente,<br><br>
    <strong>Departamento Técnico</strong><br>
    VC Eléctricos Construcciones S.A.S.</p>
  `;

  fd.append('nombreUsuario', this.cliente!.nombre);
  fd.append('correo', this.cliente!.correo);
  fd.append('asunto', asunto);
  fd.append('mensaje', mensaje); // El backend recibirá este HTML
  fd.append('archivo', archivo);

  this.notificacionService.enviarCorreo(fd).subscribe(() => {
    alert("El reporte ha sido enviado exitosamente al cliente.");
  });
}

  agregarFila() {
  this.editDetalles.push({
    actividad: '',
    observaciones: '',
    duracion: '',
    editando: true,
    esNuevo: true,
    idOrden: this.idOrden // Vincula el detalle al ID de la orden actual
  });
}
}