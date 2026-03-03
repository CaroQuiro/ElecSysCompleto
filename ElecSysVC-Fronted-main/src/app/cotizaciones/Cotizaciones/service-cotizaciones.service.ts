import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EntidadCotizaciones } from './entidad-cotizaciones';
import { RequestCotizacion } from './request-cotizacion';
import { EntidadDetalleCotizacion } from './entidad-detalleCotizacion';

@Injectable({
  providedIn: 'root'
})
export class ServiceCotizacionesService {

  private url_base = 'http://localhost:8080/api/cotizaciones';
  private clienthttp = inject(HttpClient);

  listarCotizaciones(): Observable<EntidadCotizaciones[]> {
    return this.clienthttp.get<EntidadCotizaciones[]>(`${this.url_base}/listar`);
  }

  crearCotizacion(solicitud: RequestCotizacion){
    return this.clienthttp.post(`${this.url_base}/agregar`, solicitud, {responseType: 'blob'});
  }

  obtenerCotizacionPorId(id: number){
    return this.clienthttp.get<EntidadCotizaciones>(`${this.url_base}/buscar/${id}`);
  }

  obtenerDetalleCotizacionPorId(id: number){
    return this.clienthttp.get<EntidadDetalleCotizacion[]>(`${this.url_base}/${id}/detalles`);
  }

  borrarCotizacion(id: number){
    return this.clienthttp.delete<String>(`${this.url_base}/borrar/${id}`);
  }

  actualizarCotizacion(id: number, dto: any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${id}`, dto , {responseType: 'text' });
  }

  borrarDetalleCotizacion(idCot: number, idDetalle: number){
    return this.clienthttp.delete(`${this.url_base}/borrar/${idCot}/detalle/${idDetalle}` , {responseType: 'text' });
  }

  actualizarDetalleCotizacion(idCot: number, idDetalle: number, dto: any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${idCot}/detalle/${idDetalle}`, dto , {responseType: 'text' });
  }

  crearDetalleCotizacion(idCot: number, dto: any) {
    return this.clienthttp.post(`${this.url_base}/${idCot}/detalle`, dto , {responseType: 'text' });
  }

  obtenerProbabilidad(id: number): Observable<any> {
    return this.clienthttp.get<any>(`${this.url_base}/${id}/probabilidad`);
  }

}
