import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header-usuarrio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header-usuarrio.component.html',
  styleUrl: './header-usuarrio.component.css'
})
export class HeaderUsuarrioComponent {
  nombre: string = '';
  cargo: string = '';

  constructor(private authService: AuthService, private router: Router) {
    // Obtenemos los datos guardados
    this.nombre = this.authService.getUserName();
    this.cargo = this.authService.getUserRole();
  }

  cerrarSesion() {
    this.authService.logout(); // Limpia localStorage
    this.router.navigate(['/']); // Vuelve al login
  }
}