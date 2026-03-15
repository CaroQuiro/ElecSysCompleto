import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { Component, NgModule } from '@angular/core';
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
import { CuentaporpagarComponent } from './cuentaporpagar/cuentaporpagar.component';
import { CrearCuentaComponent } from './cuentaporpagar/crear-cuenta/crear-cuenta.component';
import { VerCuentaComponent } from './cuentaporpagar/ver-cuenta/ver-cuenta.component';
import { ContratosComponent } from './contratos/contratos.component';
import { OrdenesTrabajoListarComponent } from './ordenTrabajo/ordenes-trabajo-listar/ordenes-trabajo-listar.component';
import { OrdenesTrabajoCrearComponent } from './ordenTrabajo/ordenes-trabajo-crear/ordenes-trabajo-crear.component';
import { VerOrdenTrabajoComponent } from './ordenTrabajo/ver-ordenes-trabajo/ver-orden-trabajo.component';
import { ListarTrabajadoresComponent } from './trabajadores/listar-trabajadores/listar-trabajadores.component';
import { CrearTrabajadorComponent } from './trabajadores/crear-trabajador/crear-trabajador.component';
import { VerTrabajadorComponent } from './trabajadores/ver-trabajador/ver-trabajador.component';

// IMPORTA TU GUARD AQUÍ
import { authGuard } from './guards/auth.guard'; 

export const routes: Routes = [
    // RUTA PÚBLICA (Sin Guard)
    { path: '', component: LoginComponent },

    // RUTAS PROTEGIDAS (Todas llevan canActivate)
    { path: 'Menu', component: MenuPrincipalComponent, canActivate: [authGuard] },
    
    { path: 'cotizaciones', component: CotizacionesComponent, canActivate: [authGuard] },
    { path: 'cotizaciones/crear', component: CrearCotComponent, canActivate: [authGuard] },
    { path: 'cotizaciones/ver/:id', component: VerCotizacionComponent, canActivate: [authGuard] },

    { path: 'clientes', component: ClienteComponent, canActivate: [authGuard] },
    { path: 'clientes/crear', component: CrearClienteComponent, canActivate: [authGuard] },

    { path: 'lugares', component: LugarComponent, canActivate: [authGuard] },

    { path: 'cuentaspagar', component: CuentaporpagarComponent, canActivate: [authGuard] },
    { path: 'cuentaspagar/crear', component: CrearCuentaComponent, canActivate: [authGuard] },
    { path: 'cuentaspagar/ver/:id', component: VerCuentaComponent, canActivate: [authGuard] }, 

    { path: "contratos", component: ContratosComponent, canActivate: [authGuard] },

    // ÓRDENES DE VISITA
    { path: 'ordenes-visita', component: OrdenesVisitaListarComponent, canActivate: [authGuard] },
    { path: 'ordenes-visita/crear', component: OrdenesVisitaCrearComponent, canActivate: [authGuard] },
    { path: 'ordenes-visita/ver/:id', component: VerOrdenVisitaComponent, canActivate: [authGuard] },

    // ÓRDENES DE TRABAJO
    { path: 'ordenes-trabajo', component: OrdenesTrabajoListarComponent, canActivate: [authGuard] },
    { path: 'ordenes-trabajo/crear', component: OrdenesTrabajoCrearComponent, canActivate: [authGuard] },
    { path: 'ordenes-trabajo/ver/:id', component: VerOrdenTrabajoComponent, canActivate: [authGuard] },

    // NOTIFICACIONES
    { path: 'notificaciones', component: NotificacionesComponent, canActivate: [authGuard] },
    { path: 'notificaciones/crear', component: CrearNotificacionComponent, canActivate: [authGuard] },

    // TRABAJADORES
    { path: 'trabajadores', component: ListarTrabajadoresComponent, canActivate: [authGuard] },
    { path: 'trabajadores/crear', component: CrearTrabajadorComponent, canActivate: [authGuard] },
    { path: 'trabajadores/ver/:id', component: VerTrabajadorComponent, canActivate: [authGuard] },

    // COMODÍN: Si alguien escribe algo que no existe, al login
    { path: '**', redirectTo: '' }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)], 
    exports: [RouterModule]
})
export class AppRoutingModule {}