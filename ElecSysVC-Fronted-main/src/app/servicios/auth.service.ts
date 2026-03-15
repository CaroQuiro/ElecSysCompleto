import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode'; // Opcional: para leer el correo dentro del token

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/aut'; 
  
  // Este Subject guarda el correo o datos del usuario actual
  private currentUserSubject = new BehaviorSubject<string | null>(localStorage.getItem('user_email'));

  constructor(private http: HttpClient) {}

  // Paso 1: Enviar credenciales
  login(credentials: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/login`, credentials, { responseType: 'text' });
  }

  // Paso 2: Verificar código y guardar sesión
  verificarCodigo(datos: any): Observable<any> {
  return this.http.post<any>(`${this.apiUrl}/verificar`, datos).pipe(
    tap(res => {
      if (res.token) {
        sessionStorage.setItem('token_elecsys', res.token);
        sessionStorage.setItem('user_id', res.idTrabajador.toString());
        sessionStorage.setItem('user_name', res.nombre);
        sessionStorage.setItem('user_role', res.cargo);
        sessionStorage.setItem('user_email', datos.correo);
        this.currentUserSubject.next(datos.correo);
      }
    })
  );
}

// Añade estos métodos para obtener los datos fácilmente
getUserName(): string { return sessionStorage.getItem('user_name') || 'Usuario'; }
getUserRole(): string { return sessionStorage.getItem('user_role') || 'Trabajador'; }
getUserId(): string | null { return sessionStorage.getItem('user_id'); }

  // Obtener el correo del usuario logueado en cualquier parte del app
  getUsuarioActual() {
    return this.currentUserSubject.value;
  }

  logout() {
    sessionStorage.clear();
    this.currentUserSubject.next(null);
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe();
  }

}