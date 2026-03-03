import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-vertical',
  imports: [NgIf],
  templateUrl: './menu-vertical.component.html',
  styleUrl: './menu-vertical.component.css'
})
export class MenuVerticalComponent {

  constructor(private router:Router){}

cotizacionesOpen = false;
clientesOpen = false;
lugaresOpen = false;
notificacionesOpen = false;
visitasOpen = false;
trabajosOpen = false;

toggleCotizaciones() {
  this.cotizacionesOpen = !this.cotizacionesOpen;
}

  Irinicio(){
    this.router.navigate(['Menu']);
  }

  GestionCotizaciones(){
    this.router.navigate(['\cotizaciones'])
  }

  CrearCotizacion(){
    this.router.navigate(['cotizaciones/crear']);
  }

  barraClientes(){
    this.clientesOpen = !this.clientesOpen;
  }

  GestionClientes(){
    this.router.navigate(['clientes']);
  }

  barraLugares(){
    this.lugaresOpen = !this.lugaresOpen;
  }

  GestionLugares(){
    this.router.navigate(['lugares']);
  }

  toggleNotificaciones() {
    this.notificacionesOpen = !this.notificacionesOpen;
  }

  GestionNotificaciones() {
    this.router.navigate(['/notificaciones']);
  }

  CrearNotificacionMenu() {
    this.router.navigate(['/notificaciones/crear']);
  }

  toggleVisitas() {
    this.visitasOpen = !this.visitasOpen;
  }

  GestionVisitas() {
    this.router.navigate(['/ordenes-visita']);
  }

  CrearVisita() {
    this.router.navigate(['/ordenes-visita/crear']);
  }

  toggleTrabajos() {
  this.trabajosOpen = !this.trabajosOpen;
}

GestionTrabajos() {
  this.router.navigate(['/ordenes-trabajo']);
}

CrearTrabajoMenu() {
  this.router.navigate(['/ordenes-trabajo/crear']);
}

}
