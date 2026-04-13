import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { EntidadCotizaciones } from './entidad-cotizaciones';
import { RequestCotizacion } from './request-cotizacion';
import { EntidadDetalleCotizacion } from './entidad-detalleCotizacion';

@Injectable({
  providedIn: 'root'
})
export class ServiceCotizacionesService {

  private url_base = 'http://localhost:8080/api/cotizaciones';
  private clienthttp = inject(HttpClient);

  private _refreshNeeded$ = new Subject<void>();

  get refreshNeeded$() {
    return this._refreshNeeded$;
  }

  /**
   * Listar Cotizaciones con Cache-Buster
   * Añadimos un timestamp para forzar al navegador a pedir datos nuevos siempre.
   */
  listarCotizaciones(): Observable<EntidadCotizaciones[]> {
    const timestamp = new Date().getTime();
    return this.clienthttp.get<EntidadCotizaciones[]>(`${this.url_base}/listar?t=${timestamp}`);
  }

  crearCotizacion(solicitud: RequestCotizacion){
    return this.clienthttp.post(`${this.url_base}/agregar`, solicitud, {responseType: 'blob'});
  }

  obtenerCotizacionPorId(id: number){
    const timestamp = new Date().getTime();
    return this.clienthttp.get<EntidadCotizaciones>(`${this.url_base}/buscar/${id}?t=${timestamp}`);
  }

  obtenerDetalleCotizacionPorId(id: number){
    return this.clienthttp.get<EntidadDetalleCotizacion[]>(`${this.url_base}/${id}/detalles`);
  }

  borrarCotizacion(id: number){
   return this.clienthttp.delete(`${this.url_base}/borrar/${id}`, { responseType: 'text' })
    .pipe(tap(() => this._refreshNeeded$.next()));
  }

  actualizarCotizacion(id: number, dto: any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${id}`, dto , {responseType: 'text' })
    .pipe(tap(() => this._refreshNeeded$.next()));
  }

  descargarpdf(id: number){
    return this.clienthttp.get(`${this.url_base}/descargar-pdf/${id}` , {responseType: 'blob'});
  }

  borrarDetalleCotizacion(idCot: number, idDetalle: number){
    return this.clienthttp.delete(`${this.url_base}/borrar/${idCot}/detalle/${idDetalle}` , {responseType: 'text' })
    .pipe(tap(() => this._refreshNeeded$.next()));
  }

  actualizarDetalleCotizacion(idCot: number, idDetalle: number, dto: any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${idCot}/detalle/${idDetalle}`, dto , {responseType: 'text' })
    .pipe(tap(() => this._refreshNeeded$.next()));
  }

  crearDetalleCotizacion(idCot: number, dto: any) {
    return this.clienthttp.post(`${this.url_base}/${idCot}/detalle`, dto , {responseType: 'text' })
    .pipe(tap(() => this._refreshNeeded$.next()));
  }

  obtenerProbabilidad(id: number): Observable<any> {
    return this.clienthttp.get<any>(`${this.url_base}/${id}/probabilidad`);
  }
}