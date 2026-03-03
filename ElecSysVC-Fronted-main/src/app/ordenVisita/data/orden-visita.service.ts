import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrdenDeVisitaDTO, OrdenDeVisitaRequest, DetalleOrdenVisitaDTO } from './orden-visita.models';

@Injectable({
  providedIn: 'root'
})
export class OrdenVisitaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/ordenes-visita';

  /* =====================================================
     MÉTODOS PARA ÓRDENES DE VISITA (CABECERA)
     ===================================================== */

  /** Obtiene la lista completa de órdenes */
  listarOrdenes(): Observable<OrdenDeVisitaDTO[]> {
    return this.http.get<OrdenDeVisitaDTO[]>(`${this.apiUrl}/listar`);
  }

  /** Busca una orden específica por su ID */
  buscarPorId(id: number): Observable<OrdenDeVisitaDTO> {
    return this.http.get<OrdenDeVisitaDTO>(`${this.apiUrl}/buscar/${id}`);
  }

  /** Crea una nueva orden junto con sus detalles iniciales */
  agregarOrden(request: OrdenDeVisitaRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/agregar`, request, { responseType: 'text' });
  }

  /** Actualiza los datos generales de una orden (descripción, fecha, estado) */
  actualizarOrden(id: number, orden: OrdenDeVisitaDTO): Observable<string> {
    return this.http.put(`${this.apiUrl}/actualizar/${id}`, orden, { responseType: 'text' });
  }

  /** Elimina una orden completa y todos sus detalles en cascada */
  borrarOrden(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/borrar/${id}`, { responseType: 'text' });
  }

  /* =====================================================
     MÉTODOS PARA DETALLES DE VISITA (ACTIVIDADES)
     ===================================================== */

  /** Obtiene todas las actividades asociadas a una orden específica */
  listarDetallesPorOrden(idOrden: number): Observable<DetalleOrdenVisitaDTO[]> {
    return this.http.get<DetalleOrdenVisitaDTO[]>(`${this.apiUrl}/${idOrden}/detalles`);
  }

  /** Agrega una actividad nueva a una orden ya existente */
  agregarDetalle(idOrden: number, detalle: DetalleOrdenVisitaDTO): Observable<string> {
    return this.http.post(`${this.apiUrl}/${idOrden}/detalles/agregar`, detalle, { responseType: 'text' });
  }

  /** Actualiza una actividad específica usando su ID de detalle */
  actualizarDetalle(idDetalle: number, detalle: DetalleOrdenVisitaDTO): Observable<string> {
    return this.http.put(`${this.apiUrl}/detalles/actualizar/${idDetalle}`, detalle, { responseType: 'text' });
  }

  /** Elimina una actividad individual de la tabla */
  borrarDetalle(idDetalle: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/detalles/borrar/${idDetalle}`, { responseType: 'text' });
  }
}