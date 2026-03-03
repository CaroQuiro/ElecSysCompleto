import { Component } from '@angular/core';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { Router } from '@angular/router';
import { ServiceLugarService } from './service-lugar.service';
import { EntidadLugar } from './entidad-lugar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lugar',
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, CommonModule, FormsModule],
  templateUrl: './lugar.component.html',
  styleUrl: './lugar.component.css'
})
export class LugarComponent {

  mostrarFormularioCrear: boolean = false;
  idEditando: number | null = null;

  constructor(private route: Router, private lugarService: ServiceLugarService) { }

  lugares: EntidadLugar[] = [];

  LugarCrear: EntidadLugar = {
    idLugar: 0,
    nombreLugar: '',
    direccion: ''
  };

  LugarEditar: EntidadLugar = {
    idLugar: 0,
    nombreLugar: '',
    direccion: ''
  };

  ngOnInit() {
    this.obtenerLugares();
  }

  obtenerLugares() {
    this.lugarService.listarLugares().subscribe({
      next: (datos) => {
        this.lugares = datos;
      }, error: (error) => {
        console.error("Error al Obtener la informacion de los lugares");
      }
    })
  }


  mostrarFormulario() {
    this.mostrarFormularioCrear = !this.mostrarFormularioCrear;
  }

  crearLugar() {
    this.lugarService.crearLugar(this.LugarCrear).subscribe({
      next: () => {
        alert('Lugar Creado Correctamente');
        this.obtenerLugares();
        this.limpiarCampos();
        this.mostrarFormularioCrear = false;
      },
      error: () => {
        alert('Error al crear el Lugar');
      }
    })
  }

  obtenerLugarID(id: number) {
    this.lugarService.buscarLugarPorId(id).subscribe({
      next: (datos) => {
        this.LugarEditar = { ...datos };
        this.idEditando = id;
      },
      error: () => {
        alert('Error al encontrar el lugar');
      }
    })
  }

  actualizarLugar() {
    this.lugarService.actualizarLugar(this.LugarEditar).subscribe({
      next: () => {
        alert('Lugar Actualizado Correctamente');
        this.obtenerLugares();
        this.cancelarEdicion();
      },
      error: () => {
        alert('Error al actualizar');
      }
    })
  }

  cancelarEdicion() {
    this.idEditando = null;
    this.LugarEditar = { idLugar: 0, nombreLugar: '', direccion: '' };
  }

  limpiarCampos() {
    this.LugarCrear = {
      idLugar: 0,
      nombreLugar: '',
      direccion: ''
    };
  }
}
