import { Component, OnInit, inject } from '@angular/core';
import { HeaderUsuarrioComponent } from "../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../menu-vertical/menu-vertical.component";
import { ContratoServiceService } from './ContratosClases/contrato-service.service';
import { Router } from '@angular/router';
import { ContratoEntidad, TrabajadorEntidad } from './ContratosClases/contrato-entidad';
import { CommonModule } from '@angular/common';
import { requestContrato } from './ContratosClases/contrato-request';
import { FormsModule } from '@angular/forms';
import { ModalContratoService } from './ContratosClases/modal-contrato.service';
import { VerContratosComponent } from "./ver-contratos/ver-contratos.component";
import { AuthService } from '../servicios/auth.service';

@Component({
  selector: 'app-contratos',
  standalone: true,
  imports: [HeaderUsuarrioComponent, MenuVerticalComponent, CommonModule, FormsModule, VerContratosComponent],
  templateUrl: './contratos.component.html',
  styleUrl: './contratos.component.css'
})
export class ContratosComponent implements OnInit {

  contratos: ContratoEntidad[] = [];
  contratosFiltrados: ContratoEntidad[] = [];
  trabajadores: TrabajadorEntidad[] = [];
  trabajadorMap = new Map<number, TrabajadorEntidad>();

  // Estados de Modales
  mostrarFlotanteContrato = false; 
  mostrarVerContrato = false;      
  contratoSeleccionado: any = null;

  // Filtros y Contadores
  filtroId: string = '';
  filtroTrabajador: string = '';
  filtroFecha: string = '';
  filtroActivo: string = 'TODAS';
  cantTodas = 0; cantActivos = 0; cantVencidos = 0;

  nuevoContrato: requestContrato = this.inicializarContrato();

  constructor(
    private contratoService: ContratoServiceService,
    private route: Router,
    private ModalFlotanteService: ModalContratoService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.obtenerContratos();
    this.obtenerTrabajadores();
    this.escucharPeticionesDeMenu();
  }

  private inicializarContrato(): requestContrato {
    return {
      contrato: {
        id_contrato: 0,
        id_trabajador: 0,
        sueldo: 0,
        fecha_expedicion: null,
        fecha_iniciacion: null,
        id_trabajador_encargado: 0,
        cargo: '',
        tipo_contrato: '',
        estado: 'ACTIVO'
      },
      fecha_nacimiento: null,
      lugar_nacimiento: '',
      edad: null as any, // Evitamos que sea 0 por defecto
      estadoCivil: ''
    };
  }

  get esAdmin(): boolean {
    return this.authService.getUserRole().toUpperCase() === 'ADMIN';
  }

  obtenerContratos(): void {
    this.contratoService.listarContratos().subscribe({
      next: (datos) => {
        this.contratos = datos;
        this.aplicarFiltros();
      }
    });
  }

  obtenerTrabajadores(): void {
    this.contratoService.listarTrabajadores().subscribe({
      next: (datos) => {
        this.trabajadores = datos.filter(t => t.estado?.toUpperCase() === 'ACTIVO');
        datos.forEach(t => this.trabajadorMap.set(t.id_trabajador, t));
      }
    });
  }

  aplicarFiltros(): void {
    this.contratosFiltrados = this.contratos.filter(c => {
      const coincidePestana = this.filtroActivo === 'TODAS' || c.estado === this.filtroActivo;
      const coincideId = c.id_contrato.toString().includes(this.filtroId);
      const nombreTrab = this.trabajadorMap.get(c.id_trabajador)?.nombre.toLowerCase() || '';
      const coincideTrabajador = nombreTrab.includes(this.filtroTrabajador.toLowerCase());
      
      let coincideFecha = true;
      if (this.filtroFecha) {
        const fStr = c.fecha_expedicion ? new Date(c.fecha_expedicion).toISOString().split('T')[0] : '';
        coincideFecha = fStr === this.filtroFecha;
      }
      return coincidePestana && coincideId && coincideTrabajador && coincideFecha;
    });
    this.actualizarContadores();
  }

  actualizarContadores() {
    this.cantTodas = this.contratos.length;
    this.cantActivos = this.contratos.filter(c => c.estado === 'ACTIVO').length;
    this.cantVencidos = this.contratos.filter(c => c.estado === 'VENCIDO').length;
  }

  filtrarEstado(estado: string): void {
    this.filtroActivo = estado;
    this.aplicarFiltros();
  }

  abrirFlotanteContrato() {
    if (!this.esAdmin) {
      alert("Acceso denegado: Se requiere rol ADMIN.");
      return;
    }
    this.nuevoContrato = this.inicializarContrato();
    // ASIGNACIÓN AUTOMÁTICA DEL RESPONSABLE
    this.nuevoContrato.contrato.id_trabajador_encargado = Number(this.authService.getUserId());
    this.mostrarFlotanteContrato = true;
  }

  cerrarFlotanteContrato() { this.mostrarFlotanteContrato = false; }

  guardarContrato(): void {
    if (!this.nuevoContrato.contrato.id_trabajador || !this.nuevoContrato.edad || !this.nuevoContrato.estadoCivil) {
      alert("Error: Verifique que la edad sea mayor a 18 y todos los campos estén llenos.");
      return;
    }

    this.contratoService.crearContrato(this.nuevoContrato).subscribe({
      next: (pdf) => {
        alert("Contrato creado exitosamente.");
        this.obtenerContratos();
        this.cerrarFlotanteContrato();
        window.open(window.URL.createObjectURL(pdf));
      },
      error: (err) => {
        console.error("Error al guardar:", err);
        alert("Error 400: El servidor rechazó los datos. Revise fechas y edad.");
      }
    });
  }

  verContrato(id: number) {
    this.contratoService.buscarContratoPorID(id).subscribe({
      next: (data: any) => {
        const tObj = this.trabajadorMap.get(data.id_trabajador);
        this.contratoSeleccionado = {
          ...data,
          trabajador_nombre: tObj ? tObj.nombre : 'Cargando...',
          fecha_nacimiento: data.fecha_nacimiento || 'N/A',
          lugar_nacimiento: data.lugar_nacimiento || 'N/A',
          edad: data.edad || 'N/A',
          estadoCivil: data.estadoCivil || 'N/A'
        };
        this.mostrarVerContrato = true;
      }
    });
  }

  evaluarEstado(estado: string): string {
    return estado === 'ACTIVO' ? 'badge completado' : 'badge pendiente';
  }

  formatearSueldo(valor: number): string {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(valor);
  }

  actualizarSueldo(event: any) {
    let valor = event.target.value.replace(/[^0-9]/g, '');
    this.nuevoContrato.contrato.sueldo = Number(valor);
  }

  escucharPeticionesDeMenu() {
    this.ModalFlotanteService.abrirFlotante$.subscribe(() => this.abrirFlotanteContrato());
  }

  crearTrabajador() { this.route.navigate(['/trabajadores/crear']); }
}