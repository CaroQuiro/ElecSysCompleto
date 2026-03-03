// src/app/notificacion/notificacion.module.ts

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Importa los componentes asegurándote de que las rutas sean correctas
import { NotificacionesComponent } from './notificaciones/notificaciones.component';
import { CrearNotificacionComponent } from './crear-notificacion/crear-notificacion.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NotificacionesComponent,   
    CrearNotificacionComponent
  ],
  exports: [
    NotificacionesComponent,
    CrearNotificacionComponent
  ]
})
export class NotificacionModule { }