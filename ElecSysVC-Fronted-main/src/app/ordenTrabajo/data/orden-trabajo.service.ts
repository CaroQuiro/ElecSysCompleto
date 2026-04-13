import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrdenDeTrabajoDTO, OrdenDeTrabajoRequest, DetalleOrdenTrabajoDTO } from './orden-trabajo.models';

@Injectable({
  providedIn: 'root'
})
export class OrdenTrabajoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/ordenes-trabajo';

  // --- GESTIÓN DE ÓRDENES (CABECERA) ---

  listarOrdenes(): Observable<OrdenDeTrabajoDTO[]> {
    return this.http.get<OrdenDeTrabajoDTO[]>(`${this.apiUrl}/listar`);
  }

  buscarPorId(id: number): Observable<OrdenDeTrabajoDTO> {
    return this.http.get<OrdenDeTrabajoDTO>(`${this.apiUrl}/buscar/${id}`);
  }

  agregarOrden(request: OrdenDeTrabajoRequest) {
    return this.http.post(`${this.apiUrl}/agregar`, request, { responseType: 'blob' });
  }

  actualizarOrden(id: number, orden: OrdenDeTrabajoDTO): Observable<string> {
    return this.http.put(`${this.apiUrl}/actualizar/${id}`, orden, { responseType: 'text' });
  }

  borrarOrden(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/borrar/${id}`, { responseType: 'text' });
  }

  descargarPDF(id: number){
    return this.http.get(`${this.apiUrl}/descargarOrden-pdf/${id}` , {responseType: 'blob'});
  }

  // --- GESTIÓN DE DETALLES (ACTIVIDADES) ---

  listarDetallesPorOrden(idOrden: number): Observable<DetalleOrdenTrabajoDTO[]> {
    return this.http.get<DetalleOrdenTrabajoDTO[]>(`${this.apiUrl}/${idOrden}/detalles`);
  }

  agregarDetalle(idOrden: number, detalle: DetalleOrdenTrabajoDTO): Observable<string> {
    return this.http.post(`${this.apiUrl}/${idOrden}/detalles/agregar`, detalle, { responseType: 'text' });
  }

  actualizarDetalle(idDetalle: number, detalle: DetalleOrdenTrabajoDTO): Observable<string> {
    return this.http.put(`${this.apiUrl}/detalles/actualizar/${idDetalle}`, detalle, { responseType: 'text' });
  }

  borrarDetalle(idDetalle: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/detalles/borrar/${idDetalle}`, { responseType: 'text' });
  }
}