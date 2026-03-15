import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HistorialService } from './historial.service';
import { AuthService } from '../servicios/auth.service';
import { MenuVerticalComponent } from '../menu-vertical/menu-vertical.component';
import { HeaderUsuarrioComponent } from '../header-usuarrio/header-usuarrio.component';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuVerticalComponent, HeaderUsuarrioComponent],
  templateUrl: './historial.component.html',
  styleUrl: './historial.component.css'
})
export class HistorialComponent implements OnInit {
  private service = inject(HistorialService);
  private auth = inject(AuthService);
  private router = inject(Router);

  logs: any[] = [];
  logsFiltrados: any[] = [];
  
  filtroModulo = '';
  filtroTrabajador = '';

  ngOnInit() {
    if (this.auth.getUserRole() !== 'ADMIN') {
      alert("Acceso denegado: Solo personal administrativo puede ver la auditoría.");
      this.router.navigate(['/Menu']);
      return;
    }
    this.cargarLogs();
  }

  cargarLogs() {
    this.service.listar().subscribe({
      next: (data) => {
        this.logs = data;
        this.logsFiltrados = data;
      },
      error: (e) => console.error("Error cargando historial", e)
    });
  }

  aplicarFiltros() {
    this.logsFiltrados = this.logs.filter(log => {
      const matchMod = log.moduloSistema.toLowerCase().includes(this.filtroModulo.toLowerCase());
      const matchTrab = log.idTrabajador.toString().includes(this.filtroTrabajador);
      return matchMod && matchTrab;
    });
  }
}