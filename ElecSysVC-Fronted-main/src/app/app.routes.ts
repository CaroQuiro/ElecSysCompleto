import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { NgModule } from '@angular/core';
import { MenuPrincipalComponent } from './menu-principal/menu-principal.component';
import { CotizacionesComponent } from './cotizaciones/cotizaciones.component';
import { CrearCotComponent } from './cotizaciones/crear-cot/crear-cot.component';
import { VerCotizacionComponent } from './cotizaciones/ver-cotizacion/ver-cotizacion.component';
import { ClienteComponent } from './cliente/cliente.component';
import { CrearClienteComponent } from './cliente/crear-cliente/crear-cliente.component';
import { LugarComponent } from './lugar/lugar.component';
import { NotificacionesComponent } from './notificacion/notificaciones/notificaciones.component';
import { CrearNotificacionComponent } from './notificacion/crear-notificacion/crear-notificacion.component';
import { OrdenesVisitaListarComponent } from './ordenVisita/ordenes-visita-listar/ordenes-visita-listar.component';
import { OrdenesVisitaCrearComponent } from './ordenVisita/ordenes-visita-crear/ordenes-visita-crear.component';
import { VerOrdenVisitaComponent } from './ordenVisita/ver-orden-visita/ver-orden-visita.component';

import { OrdenesTrabajoListarComponent } from './ordenTrabajo/ordenes-trabajo-listar/ordenes-trabajo-listar.component';
import { OrdenesTrabajoCrearComponent } from './ordenTrabajo/ordenes-trabajo-crear/ordenes-trabajo-crear.component';
import { VerOrdenTrabajoComponent } from './ordenTrabajo/ver-ordenes-trabajo/ver-orden-trabajo.component';

export const routes: Routes = [
    {path: '', component: LoginComponent},
    {path: 'Menu', component: MenuPrincipalComponent},
    
    // COTIZACIONES
    {path: 'cotizaciones', component: CotizacionesComponent},
    {path: 'cotizaciones/crear', component: CrearCotComponent},
    {path: 'cotizaciones/ver/:id', component: VerCotizacionComponent},

    // CLIENTES Y LUGARES
    {path: 'clientes', component: ClienteComponent},
    {path: 'clientes/crear', component: CrearClienteComponent},
    {path: 'lugares', component: LugarComponent},

    // ÓRDENES DE VISITA
    { path: 'ordenes-visita', component: OrdenesVisitaListarComponent },
    { path: 'ordenes-visita/crear', component: OrdenesVisitaCrearComponent },
    { path: 'ordenes-visita/ver/:id', component: VerOrdenVisitaComponent },

    // ÓRDENES DE TRABAJO (NUEVAS RUTAS)
    { path: 'ordenes-trabajo', component: OrdenesTrabajoListarComponent },
    { path: 'ordenes-trabajo/crear', component: OrdenesTrabajoCrearComponent },
    { path: 'ordenes-trabajo/ver/:id', component: VerOrdenTrabajoComponent },

    // NOTIFICACIONES
    { path: 'notificaciones', component: NotificacionesComponent },
    { path: 'notificaciones/crear', component: CrearNotificacionComponent }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)], 
    exports: [RouterModule]
})
export class AppRoutingModule {}