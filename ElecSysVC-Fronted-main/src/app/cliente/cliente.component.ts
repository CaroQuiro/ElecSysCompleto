import { Component } from '@angular/core';
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { Router } from '@angular/router';
import { ServiceClienteService } from './service-cliente.service';
import { EntidadCliente } from './entidad-cliente';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-cliente',
  imports: [MenuVerticalComponent, HeaderUsuarrioComponent, CommonModule, FormsModule],
  templateUrl: './cliente.component.html',
  styleUrl: './cliente.component.css'
})
export class ClienteComponent{

  mostrarFormularioCrear: boolean = false;

  constructor(private route: Router, private clienteService: ServiceClienteService){}

  clientes: EntidadCliente[] = [];

  ngOnInit(){
    this.obtenerClientes();
  }

  obtenerClientes(): void{
    this.clienteService.listarClientes().subscribe({
      next: (datos) => {
        this.clientes = datos;
      },
      error: (error) => {
        console.error("Error al Obtener la informacion de clientes", error);
      }
    })
  }

  evaluarEstado(estado?: string): string{
    switch (estado) {
      case 'ACTIVO':
        return 'badge completado';
      case 'INACTIVO':
        return 'badge cancelado';
      default:
        return 'badge';
    }
  }

  mostrarFormulario(){
    this.mostrarFormularioCrear = !this.mostrarFormularioCrear;
  }


  // ============== Crear Cliente =====================
  clienteCrear: EntidadCliente = {
    id_cliente: 0,
    nombre: '',
    telefono: '',
    direccion: '',
    correo: '',
    estado: 'ACTIVO'
  };

    crearCliente(){
    this.clienteService.crearCliente(this.clienteCrear).subscribe({
      next: (msg) => {
        alert('Cliente Creado Exitosamente');
        this.obtenerClientes();
        this.limpiarCampos();
        this.mostrarFormularioCrear = false;

      },
      error: error => {
        alert('Error al crear el Cliente');
      }
    })
  }

  limpiarCampos(){
    this.clienteCrear = {
      id_cliente: 0,
      nombre: '',
      telefono: '',
      direccion: '',
      correo: '',
      estado: 'ACTIVO'
    };
  }

  // ========================Actualizar Cliente ===============
  
  idEditando: number | null = null;
  clienteEditar: EntidadCliente = {
      id_cliente: 0,
      nombre: '',
      telefono: '',
      direccion: '',
      correo: '',
      estado: ''
  };

  buscarClienteID(id: number){
    this.clienteService.buscarClientePorId(id).subscribe({
      next: (datos) => {
        this.clienteEditar = {...datos};
        this.idEditando = id;
      },
      error: (err) => {
        console.log(err);
        alert('Error al encontrar el cliente');
      }
    })
  }

  actualizarCliente(){
    this.clienteService.actualizarCliente(this.clienteEditar).subscribe({
      next: () => {
        alert('Cliente Actualizado Correctamente');
        this.obtenerClientes();
        this.cancelarEdicion();
      }, 
      error: () => {
        alert('Error al actualizar');
      }
    })
  }

  cancelarEdicion(){
    this.idEditando = null;
     this.clienteEditar = {      
      id_cliente: 0,
      nombre: '',
      telefono: '',
      direccion: '',
      correo: '',
      estado: ''}
  }

}
