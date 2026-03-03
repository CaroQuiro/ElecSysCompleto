import { Component } from '@angular/core';
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { EntidadCliente } from '../entidad-cliente';
import { Router } from '@angular/router';
import { ServiceClienteService } from '../service-cliente.service';
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-crear-cliente',
  imports: [MenuVerticalComponent, HeaderUsuarrioComponent, FormsModule],
  templateUrl: './crear-cliente.component.html',
  styleUrl: './crear-cliente.component.css'
})
export class CrearClienteComponent {

  cliente: EntidadCliente = {
    id_cliente: 0,
    nombre: '',
    telefono: '',
    direccion: '',
    correo: '',
    estado: 'ACTIVO'
  }

  constructor(private route: Router, private clienteService: ServiceClienteService){}
  
  crearCliente(){
    this.clienteService.crearCliente(this.cliente).subscribe({
      next: (msg) => {
        alert('Cliente Creado Exitosamente');
        this.limpiarCampos();
        this.route.navigate(['clientes']);

      },
      error: error => {
        console.error(error);
      }
    })
  }

  limpiarCampos(){
    this.cliente = {
      id_cliente: 0,
      nombre: '',
      telefono: '',
      direccion: '',
      correo: '',
      estado: 'ACTIVO'
    };
  }

  irGestionclientes(){
    this.route.navigate(['clientes']);
  }
}
