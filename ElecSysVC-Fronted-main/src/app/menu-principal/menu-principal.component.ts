import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importante para el *ngIf
import { FooterUsuarioComponent } from "../footer-usuario/footer-usuario.component";
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { Router } from '@angular/router';
import { ModalContratoService } from '../contratos/ContratosClases/modal-contrato.service';
import { AuthService } from '../servicios/auth.service';

@Component({
  selector: 'app-menu-principal',
  standalone: true,
  imports: [CommonModule, FooterUsuarioComponent, HeaderUsuarrioComponent],
  templateUrl: './menu-principal.component.html',
  styleUrl: './menu-principal.component.css'
})
export class MenuPrincipalComponent implements OnInit {

  nombreUsuario: string = '';
  esAdmin: boolean = false;

  constructor(
    private router: Router, 
    private flotanteServiceContrato: ModalContratoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Obtenemos los datos del servicio de autenticación
    this.nombreUsuario = this.authService.getUserName();
    this.esAdmin = this.authService.getUserRole() === 'ADMIN';
  }

  // Métodos de Navegación
  GestionCotizaciones() { this.router.navigate(['cotizaciones']); }
  CrearCotizacion() { this.router.navigate(['cotizaciones/crear']); }

  GestionCuentasPagar() { this.router.navigate(['cuentaspagar']); }
  CrearCuenta() { this.router.navigate(['cuentaspagar/crear']); }

  GestionVisitas() { this.router.navigate(['/ordenes-visita']); }
  CrearVisita() { this.router.navigate(['/ordenes-visita/crear']); }

  GestionTrabajos() { this.router.navigate(['/ordenes-trabajo']); }
  CrearTrabajo() { this.router.navigate(['/ordenes-trabajo/crear']); }

  GestionarContratos() { this.router.navigate(['contratos']); }
  CrearContrato() {
    this.router.navigate(['contratos']).then(() => {
      setTimeout(() => { this.flotanteServiceContrato.abrirFlotante(); }, 100);
    });
  }
}