import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { NotificacionService } from '../../notificacion/notificacion.service';
import { TrabajadorService } from '../../trabajadores/data/trabajadores.service'; 
import { AuthService } from '../../servicios/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ver-contratos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ver-contratos.component.html',
  styleUrl: './ver-contratos.component.css',
  providers: [CurrencyPipe]
})
export class VerContratosComponent {

  constructor(
    private notificacionService: NotificacionService,
    private trabajadorService: TrabajadorService,
    private authService: AuthService,
    private route: Router,
    private currencyPipe: CurrencyPipe
  ) { }

  @Input() contrato: any;
  @Input() mostrar: boolean = false;
  @Output() cerrarModal = new EventEmitter<void>();

  // Verificamos si el usuario logueado es ADMIN
  get esAdmin(): boolean {
    return this.authService.getUserRole().toUpperCase() === 'ADMIN';
  }

  cerrar() {
    this.cerrarModal.emit();
  }

  enviarEmail() {
    // Protección extra por código
    if (!this.esAdmin) {
      alert("No tienes permisos para enviar correos corporativos.");
      return;
    }

    const inputArchivo = document.createElement('input');
    inputArchivo.type = 'file';
    inputArchivo.accept = '.pdf';

    inputArchivo.onchange = (e: any) => {
      const archivo = e.target.files[0];
      if (archivo) {
        this.procesarEnvio(archivo);
      }
    };
    inputArchivo.click();
  }

  procesarEnvio(archivo: File) {
    const idTrabajador = this.contrato.id_trabajador;

    if (!idTrabajador) {
      alert("Error: El ID del trabajador no está presente en el contrato.");
      return;
    }

    // BUSCAMOS AL TRABAJADOR POR ID
    this.trabajadorService.buscarPorId(idTrabajador).subscribe({
      next: (trabajador) => {
        if (!trabajador.correo) {
          alert("Este trabajador no tiene un correo registrado en el sistema.");
          return;
        }

        const formData = new FormData();
        const sueldoFormateado = this.currencyPipe.transform(this.contrato.sueldo, 'COP', 'symbol', '1.0-0');

        const mensajeHtml = `
          <div style="font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; max-width: 600px;">
            <div style="background-color: #b80000; color: white; padding: 20px; text-align: center;">
              <h2 style="margin: 0;">VC Eléctricos - Contrato Laboral</h2>
            </div>
            <div style="padding: 30px; color: #333;">
              <h3>Estimado/a ${trabajador.nombre},</h3>
              <p>Adjunto encontrarás la copia formal de tu contrato laboral gestionado a través de nuestro sistema <strong>ElecSys</strong>.</p>
              <div style="background: #f4f4f4; padding: 15px; border-radius: 5px; margin: 20px 0;">
                <p><strong>Cargo:</strong> ${this.contrato.cargo}</p>
                <p><strong>Sueldo:</strong> ${sueldoFormateado}</p>
                <p><strong>Tipo:</strong> ${this.contrato.tipo_contrato}</p>
              </div>
              <p>Por favor, descarga y guarda este documento para tus registros.</p>
            </div>
          </div>
        `;

        formData.append('nombreUsuario', trabajador.nombre);
        formData.append('correo', trabajador.correo);
        formData.append('asunto', `Contrato Laboral Digital - ${trabajador.nombre}`);
        formData.append('mensaje', mensajeHtml);
        formData.append('archivo', archivo);

        this.notificacionService.enviarCorreo(formData).subscribe({
          next: () => alert("Correo enviado exitosamente al trabajador."),
          error: (err) => alert("Error al conectar con el servidor de correos.")
        });
      },
      error: () => alert("No se pudo encontrar la ficha del trabajador en la base de datos.")
    });
  }
}