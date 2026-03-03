import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { OrdenVisitaService } from '../data/orden-visita.service';
import { OrdenDeVisitaDTO, DetalleOrdenVisitaDTO } from '../data/orden-visita.models';
import { ServiceClienteService } from '../../cliente/service-cliente.service';
import { ServiceLugarService } from '../../lugar/service-lugar.service';
import { NotificacionService } from '../../notificacion/notificacion.service';
import { EntidadCliente } from '../../cliente/entidad-cliente';
import { EntidadLugar } from '../../lugar/entidad-lugar';
import { DetalleVisitaUI } from '../data/orden-visita.models'; // Ajusta la ruta

@Component({
  selector: 'app-ver-orden-visita',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ver-orden-visita.component.html',
  styleUrl: './ver-orden-visita.component.css'
})
export class VerOrdenVisitaComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ordenService = inject(OrdenVisitaService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);
  private notificacionService = inject(NotificacionService);

  idOrden!: number;
  orden?: OrdenDeVisitaDTO;
  editDetalles: DetalleVisitaUI[] = [];
  
  cliente?: EntidadCliente;
  lugar?: EntidadLugar;

  ngOnInit() {
    this.idOrden = Number(this.route.snapshot.params['id']);
    this.cargarDatos();
  }

  cargarDatos() {
    this.ordenService.buscarPorId(this.idOrden).subscribe(data => {
      this.orden = data;
      this.cargarRelaciones(data.idCliente, data.idLugar);
      this.recargarDetalles();
    });
  }

  cargarRelaciones(idCliente: number, idLugar: number) {
    this.clienteService.buscarClientePorId(idCliente).subscribe(c => this.cliente = c);
    this.lugarService.buscarLugarPorId(idLugar).subscribe(l => this.lugar = l);
  }

  recargarDetalles() {
    this.ordenService.listarDetallesPorOrden(this.idOrden).subscribe(res => {
      this.editDetalles = res.map(d => ({ ...d, editando: false }));
    });
  }

  // --- GESTIÓN DE FILAS (ACTIVIDADES) ---
  editarFila(d: DetalleVisitaUI) { d.editando = true; }

  agregarFila() {
    this.editDetalles.push({
      actividad: '',
      observaciones: '',
      duracion: '',
      editando: true,
      esNuevo: true,
      idVisita: this.idOrden
    });
  }

  cancelarFila(d: DetalleVisitaUI, index: number) {
    if (d.esNuevo) {
      this.editDetalles.splice(index, 1);
    } else {
      d.editando = false;
      this.recargarDetalles();
    }
  }

  guardarFila(d: DetalleVisitaUI) {
    if (d.esNuevo) {
      this.ordenService.agregarDetalle(this.idOrden, d).subscribe(() => {
        alert("Actividad agregada");
        this.recargarDetalles();
      });
    } else {
      this.ordenService.actualizarDetalle(d.idDetalleVisita!, d).subscribe(() => {
        alert("Actividad actualizada");
        d.editando = false;
      });
    }
  }

  eliminarFila(d: DetalleVisitaUI, index: number) {
    // REGLA: Debe quedar al menos 1
    if (this.editDetalles.length <= 1) {
      alert("Error: La orden de visita debe tener al menos una actividad registrada.");
      return;
    }

    if (confirm("¿Desea eliminar esta actividad?")) {
      if (d.esNuevo) {
        this.editDetalles.splice(index, 1);
      } else {
        this.ordenService.borrarDetalle(d.idDetalleVisita!).subscribe(() => {
          this.recargarDetalles();
        });
      }
    }
  }

  // --- ACCIONES FINALES ---
  actualizarOrdenCompleta() {
    if (this.orden) {
      this.ordenService.actualizarOrden(this.idOrden, this.orden).subscribe(() => {
        alert("Orden de Visita actualizada correctamente");
        this.router.navigate(['/ordenes-visita']);
      });
    }
  }

  borrarOrdenTotal() {
    if(confirm("¿Seguro que desea eliminar TODA la orden y sus detalles? Esta acción no se puede deshacer.")) {
      this.ordenService.borrarOrden(this.idOrden).subscribe(() => {
        this.router.navigate(['/ordenes-visita']);
      });
    }
  }

  enviarEmail() {
    const inputArchivo = document.createElement('input');
    inputArchivo.type = 'file';
    inputArchivo.accept = '.pdf';

    inputArchivo.onchange = (e: any) => {
      const archivo = e.target.files[0];
      if (archivo && this.cliente) {
        this.procesarEnvio(archivo);
      }
    };
    inputArchivo.click();
  }

  procesarEnvio(archivo: File) {
    const formData = new FormData();
    const mensajeHtml = `
      <div style="font-family: Arial; color: #333;">
        <h2 style="color: #a00d0d;">Reporte de Visita Técnica #${this.idOrden}</h2>
        <p>Estimado/a <strong>${this.cliente?.nombre}</strong>,</p>
        <p>Adjunto encontrará el documento técnico relacionado con la visita realizada en: <strong>${this.lugar?.nombreLugar}</strong>.</p>
        <p>Atentamente,<br>VC Eléctricos Construcciones S.A.S.</p>
      </div>
    `;

    formData.append('nombreUsuario', this.cliente!.nombre);
    formData.append('correo', this.cliente!.correo);
    formData.append('asunto', `Visita Técnica #${this.idOrden} - VC Eléctricos`);
    formData.append('mensaje', mensajeHtml);
    formData.append('archivo', archivo);

    this.notificacionService.enviarCorreo(formData).subscribe({
      next: () => alert("Correo enviado exitosamente al cliente."),
      error: () => alert("Error al enviar el correo.")
    });
  }
}