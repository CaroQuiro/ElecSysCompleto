import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TrabajadorDTO } from './trabajadores.models';

@Injectable({
  providedIn: 'root'
})
export class TrabajadorService {
  private http = inject(HttpClient);
  // URL base apuntando al controlador de Java
  private apiUrl = 'http://localhost:8080/api/trabajador';

  /** * Obtiene la lista de todos los trabajadores registrados 
   */
  listarTrabajadores(): Observable<TrabajadorDTO[]> {
    return this.http.get<TrabajadorDTO[]>(`${this.apiUrl}/listar`);
  }

  /** * Busca un trabajador específico por su ID (Cédula)
   */
  buscarPorId(id: number): Observable<TrabajadorDTO> {
    return this.http.get<TrabajadorDTO>(`${this.apiUrl}/buscar/${id}`);
  }

  /** * Registra un nuevo trabajador en el sistema
   */
  agregarTrabajador(trabajador: TrabajadorDTO): Observable<string> {
    return this.http.post(`${this.apiUrl}/agregar`, trabajador, { responseType: 'text' });
  }

  /** * Actualiza los datos de un trabajador existente
   */
  actualizarTrabajador(id: number, trabajador: TrabajadorDTO): Observable<string> {
    return this.http.put(`${this.apiUrl}/actualizar/${id}`, trabajador, { responseType: 'text' });
  }

  /** * Deshabilita o borra un trabajador por su ID
   */
  borrarTrabajador(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/borrar/${id}`, { responseType: 'text' });
  }
}