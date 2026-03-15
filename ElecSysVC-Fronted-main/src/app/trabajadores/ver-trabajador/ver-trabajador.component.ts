import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderUsuarrioComponent } from "../../header-usuarrio/header-usuarrio.component";
import { MenuVerticalComponent } from "../../menu-vertical/menu-vertical.component";
import { TrabajadorService } from '../data/trabajadores.service';
import { TrabajadorDTO } from '../data/trabajadores.models';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-ver-trabajador',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderUsuarrioComponent, MenuVerticalComponent],
  templateUrl: './ver-trabajador.component.html',
  styleUrl: './ver-trabajador.component.css'
})
export class VerTrabajadorComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private trabajadorService = inject(TrabajadorService);
  private authService = inject(AuthService);

  idTrabajador!: number;
  trabajador?: TrabajadorDTO;
  
  // Variables de control
  claveActualInput: string = '';
  nuevaPassword: string = '';
  
  esAdmin: boolean = false;
  esPropioPerfil: boolean = false;

  ngOnInit(): void {
    this.idTrabajador = Number(this.route.snapshot.params['id']);
    const idLogueado = Number(this.authService.getUserId());
    this.esAdmin = this.authService.getUserRole() === 'ADMIN';
    this.esPropioPerfil = (this.idTrabajador === idLogueado);

    this.cargarDatos();
  }

  cargarDatos(): void {
    this.trabajadorService.buscarPorId(this.idTrabajador).subscribe({
      next: (data) => {
        this.trabajador = data;
        this.trabajador.password = '';
      },
      error: () => this.router.navigate(['/trabajadores'])
    });
  }

  actualizarTrabajador(): void {
    if (!this.trabajador) return;

     if (this.esPropioPerfil) {
      if (this.nuevaPassword.trim() !== '' && !this.claveActualInput) {
        alert("Para cambiar su contraseña, debe ingresar su clave actual por seguridad.");
        return;
      }
    }

    if (this.nuevaPassword.trim() !== '') {
      this.trabajador.password = this.nuevaPassword;
    } else {
      this.trabajador.password = '';
    }

    this.trabajadorService.actualizarTrabajador(this.idTrabajador, this.trabajador).subscribe({
      next: (res) => {
        alert("Perfil actualizado: " + res);
        
        if (this.esPropioPerfil && this.nuevaPassword.trim() !== '') {
          alert("Su sesión expirará. Por favor, ingrese con su nueva contraseña.");
          this.authService.logout();
          this.router.navigate(['']);
        } else {
          this.router.navigate(['/trabajadores']);
        }
      },
      error: (err) => {
        console.error(err);
        alert("Error al actualizar: " + (err.error || "Asegúrese de que los datos sean correctos."));
      }
    });
  }

  regresar(): void { this.router.navigate(['/trabajadores']); }
}