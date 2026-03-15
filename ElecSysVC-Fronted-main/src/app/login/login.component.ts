import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginData = { correo: '', password: '' };
  codigoVerificacion = '';
  pasoVerificacion = false;
  cargando = false; // Nueva variable de control

  constructor(private authService: AuthService, private router: Router) {}

  /**
 * Realiza el proceso de autenticación inicial del usuario.
 * Controla el estado de carga, envía las credenciales al servidor y,
 * según la respuesta, permite avanzar al paso de verificación de código.
 */
entrar() {
  if (this.cargando) return; 
  this.cargando = true;

  this.authService.login(this.loginData).subscribe({
    next: (mensaje) => {
      if (mensaje === "Código enviado al correo") {
        alert(mensaje);
        this.pasoVerificacion = true;
      } else {
        alert(mensaje);
        this.pasoVerificacion = false; 
      }
      this.cargando = false; 
    },
    error: (e) => {
      alert("Error: " + (e.error || 'No se pudo procesar la solicitud'));
      this.cargando = false; 
    }
  });
}

  confirmarCodigo() {
    if (this.cargando) return;
    this.cargando = true;

    this.authService.verificarCodigo({
      correo: this.loginData.correo,
      codigo: this.codigoVerificacion
    }).subscribe({
      next: (res) => {
        this.router.navigate(['Menu']);
      },
      error: (err) => {
        alert("Código inválido o expirado");
        this.cargando = false;
      }
    });
  }
}