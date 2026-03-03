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

  /**
   * Envía un correo manual con archivo adjunto (Usado en VerCotizacion)
   */
  enviarCorreo(formData: FormData): Observable<any> {
    return this.http.post(`${this.URL}/enviar-correo`, formData);
  }

  /**
   * Obtiene todas las notificaciones registradas
   */
  listar(): Observable<NotificacionDTO[]> {
    return this.http.get<NotificacionDTO[]>(`${this.URL}/listar`);
  }

  /**
   * Crea una notificación programada con sus destinatarios
   */
  programar(request: ProgramacionRequest): Observable<string> {
    return this.http.post(`${this.URL}/programar`, request, { 
      responseType: 'text' 
    });
  }

  /**
   * Cambia el estado de la notificación a INACTIVA
   */
  desactivar(id: number): Observable<string> {
    return this.http.put(`${this.URL}/desactivar/${id}`, {}, { 
      responseType: 'text' 
    });
  }

  /**
   * Elimina permanentemente una notificación
   */
  borrar(id: number): Observable<string> {
    return this.http.delete(`${this.URL}/borrar/${id}`, { 
      responseType: 'text' 
    });
  }

  /**
   * Busca una notificación por su ID
   */
  buscarNotificacion(id: number): Observable<NotificacionDTO> {
    return this.http.get<NotificacionDTO>(`${this.URL}/buscar/${id}`);
  }
}