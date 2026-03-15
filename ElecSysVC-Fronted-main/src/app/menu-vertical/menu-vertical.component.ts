import { NgIf, CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
import { ModalContratoService } from '../contratos/ContratosClases/modal-contrato.service';

@Component({
  selector: 'app-menu-vertical',
  standalone: true,
  imports: [CommonModule, NgIf],
  templateUrl: './menu-vertical.component.html',
  styleUrl: './menu-vertical.component.css'
})
export class MenuVerticalComponent implements OnInit {

  esAdmin: boolean = false;

  // Estados de los desplegables
  cotizacionesOpen = false;
  clientesOpen = false;
  lugaresOpen = false;
  cuentasporpagaropen = false;
  notificacionesOpen = false;
  visitasOpen = false;
  trabajosOpen = false;
  trabajadoresOpen = false;
  contratosOpen = false; // Nuevo

  constructor(
    private router: Router, 
    private authService: AuthService,
    private flotanteServiceContrato: ModalContratoService
  ) {}

  ngOnInit(): void {
    // Validamos el rol al iniciar el componente
    this.esAdmin = this.authService.getUserRole() === 'ADMIN';
  }

  // --- MÉTODOS DE CONTROL ---
  Irinicio() { this.router.navigate(['Menu']); }

  toggleCotizaciones() { this.cotizacionesOpen = !this.cotizacionesOpen; }
  toggleVisitas() { this.visitasOpen = !this.visitasOpen; }
  toggleTrabajos() { this.trabajosOpen = !this.trabajosOpen; }
  barraCuentasPagar() { this.cuentasporpagaropen = !this.cuentasporpagaropen; }
  barraClientes() { this.clientesOpen = !this.clientesOpen; }
  barraLugares() { this.lugaresOpen = !this.lugaresOpen; }
  toggleNotificaciones() { this.notificacionesOpen = !this.notificacionesOpen; }
  toggleTrabajadores() { this.trabajadoresOpen = !this.trabajadoresOpen; }
  toggleContratos() { this.contratosOpen = !this.contratosOpen; }

  // --- NAVEGACIÓN ---
  CrearCotizacion() { this.router.navigate(['cotizaciones/crear']); }
  GestionCotizaciones() { this.router.navigate(['cotizaciones']); }
  
  CrearVisita() { this.router.navigate(['/ordenes-visita/crear']); }
  GestionVisitas() { this.router.navigate(['/ordenes-visita']); }

  CrearTrabajoMenu() { this.router.navigate(['/ordenes-trabajo/crear']); }
  GestionTrabajos() { this.router.navigate(['/ordenes-trabajo']); }

  GestionarCuentas() { this.router.navigate(['cuentaspagar']); }
  GestionClientes() { this.router.navigate(['clientes']); }
  GestionLugares() { this.router.navigate(['lugares']); }

  CrearNotificacionMenu() { this.router.navigate(['/notificaciones/crear']); }
  GestionNotificaciones() { this.router.navigate(['/notificaciones']); }

  GestionTrabajadores() { this.router.navigate(['/trabajadores']); }
  CrearTrabajadorMenu() { this.router.navigate(['/trabajadores/crear']); }

  // Contratos
  GestionarContratos() { this.router.navigate(['contratos']); }
  CrearContrato() {
    this.router.navigate(['contratos']).then(() => {
      setTimeout(() => { this.flotanteServiceContrato.abrirFlotante(); }, 100);
    });
  }

  irAHistorial() { 
    this.router.navigate(['/historial']); 
  }
}