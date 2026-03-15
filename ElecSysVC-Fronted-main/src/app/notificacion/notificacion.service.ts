import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NotificacionDTO, ProgramacionRequest } from './notificacion.model';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private http = inject(HttpClient);
  private readonly URL = 'http://localhost:8080/api/notificaciones';

  enviarCorreo(formData: FormData): Observable<any> {
    return this.http.post(`${this.URL}/enviar-correo`, formData);
  }

  listar(): Observable<NotificacionDTO[]> {
    return this.http.get<NotificacionDTO[]>(`${this.URL}/listar`);
  }

  programar(request: ProgramacionRequest): Observable<string> {
    return this.http.post(`${this.URL}/programar`, request, { responseType: 'text' });
  }

  activar(id: number): Observable<string> {
    return this.http.put(`${this.URL}/activar/${id}`, {}, { responseType: 'text' });
  }

  desactivar(id: number): Observable<string> {
    return this.http.put(`${this.URL}/desactivar/${id}`, {}, { responseType: 'text' });
  }

  actualizar(id: number, dto: NotificacionDTO): Observable<string> {
    return this.http.put(`${this.URL}/actualizar/${id}`, dto, { responseType: 'text' });
  }

  borrar(id: number): Observable<string> {
    return this.http.delete(`${this.URL}/borrar/${id}`, { responseType: 'text' });
  }

  buscarNotificacion(id: number): Observable<NotificacionDTO> {
    return this.http.get<NotificacionDTO>(`${this.URL}/buscar/${id}`);
  }
}