import { Component } from '@angular/core';
import { FooterUsuarioComponent } from "../footer-usuario/footer-usuario.component";
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-principal',
  standalone: true,
  imports: [FooterUsuarioComponent, HeaderUsuarrioComponent],
  templateUrl: './menu-principal.component.html',
  styleUrl: './menu-principal.component.css'
})
export class MenuPrincipalComponent {

  constructor(private router: Router) {}

  // Métodos de Cotizaciones
  GestionCotizaciones() {
    this.router.navigate(['/cotizaciones']); // Corregido de \ a /
  }

  CrearCotizacion() {
    this.router.navigate(['cotizaciones/crear']);
  }

  // Métodos de Órdenes de Visita
  GestionVisitas() {
    this.router.navigate(['/ordenes-visita']);
  }

  CrearVisita() {
    this.router.navigate(['/ordenes-visita/crear']);
  }

  GestionTrabajos() {
    this.router.navigate(['/ordenes-trabajo']);
  }

  CrearTrabajo() {
    this.router.navigate(['/ordenes-trabajo/crear']);
  }
}
