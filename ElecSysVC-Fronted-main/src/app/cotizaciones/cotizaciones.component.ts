import { Component, inject, OnInit } from '@angular/core';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { ActivatedRoute, Router } from '@angular/router';
import { EntidadCotizaciones } from './Cotizaciones/entidad-cotizaciones';
import { ServiceCotizacionesService } from './Cotizaciones/service-cotizaciones.service';
import { CommonModule } from '@angular/common';
import { ServiceClienteService } from '../cliente/service-cliente.service';
import { EntidadCliente } from '../cliente/entidad-cliente';
import { ServiceLugarService } from '../lugar/service-lugar.service';
import { EntidadLugar } from '../lugar/entidad-lugar';

@Component({
  selector: 'app-cotizaciones',
  imports: [CommonModule ,HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './cotizaciones.component.html',
  styleUrl: './cotizaciones.component.css'
})
export class CotizacionesComponent implements OnInit{

  private cotizacionservice = inject(ServiceCotizacionesService);
  private clienteService = inject(ServiceClienteService);
  private lugarService = inject(ServiceLugarService);

  cotizaciones: EntidadCotizaciones[] = [];
  clientes: EntidadCliente[] = [];
  lugares: EntidadLugar[] = [];

  clietesMap = new Map<number, EntidadCliente>();
  lugaresMap = new Map<number, EntidadLugar>();

  constructor(private route: Router, private ruta: ActivatedRoute){}

  ngOnInit(){
    this.obtenerCotizaciones();
    this.obtenerClientes();
    this.obtenerLugares();
  }

  obtenerCotizaciones() : void{
    this.cotizacionservice.listarCotizaciones().subscribe(
      {
        next: (datos) => {
          this.cotizaciones = datos;
        }, 
        error: (error) => {
          console.error("Error al obtener la informacion", error);
        }
      }
    );
  }

  obtenerClientes(): void{
    this.clienteService.listarClientes().subscribe(
      {
        next: (datos) => {
          this.clientes = datos;

          datos.forEach(cliente => {
            this.clietesMap.set(cliente.id_cliente, cliente);
          });
        },
        error: (error) => {
          console.error("Error al obtener la informacion de clientes", error);
        }
      }
    )
  }

  obtenerLugares(): void{
    this.lugarService.listarLugares().subscribe(
      {
        next: (datos) => {
          this.lugares = datos;

          datos.forEach(lugar => {
            this.lugaresMap.set(lugar.idLugar, lugar);
          });
        },
        error: (error) => {
          console.error("Error al obtener la informacion de lugares", error);
        }
      }
    )
  }

  evaluarEstado(estado: string): string{
    switch (estado) {
      case 'PENDIENTE':
        return 'badge pendiente';
      case 'ACTIVO':
        return 'badge completado';
      case 'RECHAZADO':
        return 'badge cancelado';
      default:
        return 'badge';
    }
  }

  calcularTotal(campo: keyof EntidadCotizaciones): number{
    return this.cotizaciones.reduce(
      (total, cot) => total + Number(cot[campo] ?? 0), 0
    );
  }

  crearCotizacion(){
    this.route.navigate(['/cotizaciones/crear'])
  }

  verCotizacion(id: number){
    this.route.navigate(['cotizaciones/ver', id])
  }
}
