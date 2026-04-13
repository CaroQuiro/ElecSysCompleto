import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EntidadCuentasPagar } from './CuentaPagar-Entidad';
import { CrearCuentaRequest } from './request-cuentadecobro';
import { EntidadDetalleCuenta } from './DetalleCuentaEntidad';

@Injectable({
  providedIn: 'root'
})
export class CuentaServiceService {

  private url_base = 'http://localhost:8080/api/cuentas_pagar';
  private clienthttp = inject(HttpClient);

  listarCuentasPorPagar(): Observable<EntidadCuentasPagar[]> {
    return this.clienthttp.get<EntidadCuentasPagar[]>(`${this.url_base}/listar`);
  }

  crearCuentaCobrar(solicitud: CrearCuentaRequest) {
    return this.clienthttp.post(`${this.url_base}/agregar`, solicitud, { responseType: 'blob' });
  }

  obtenerCuentaPagarID(id: number){
    return this.clienthttp.get<EntidadCuentasPagar>(`${this.url_base}/buscar/${id}`);
  }
  
  obtenerDetalleCuentaPorId(id: number){
    return this.clienthttp.get<EntidadDetalleCuenta[]>(`${this.url_base}/${id}/detallesCuentas`);
  }

  actualizarDetalleCuenta(idCuenta: number, idDetalle: number, dto: any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${idCuenta}/detalle-cuenta/${idDetalle}`, dto, {responseType: 'text'});
  }

  descargarpdf(id: number){
    return this.clienthttp.get(`${this.url_base}/descargar-pdf/${id}` , {responseType: 'blob'});
  }

  crearDetalleCuenta(idCuenta: number, dto: any){
    return this.clienthttp.post(`${this.url_base}/Crear/${idCuenta}/detalle`, dto, {responseType: 'text'});
  }

  borrarDetalleCotizacion(idCuenta: number, idDetalle: number){
    return this.clienthttp.delete(`${this.url_base}/borrar/${idCuenta}/detalle/${idDetalle}`, {responseType: 'text'});
  }

  actualizarCuentaPagar(idCuenta: number, dto:any){
    return this.clienthttp.put(`${this.url_base}/actualizar/${idCuenta}`, dto , {responseType: 'text'});
  }
  
  borrarCuentaPagar(id: number) {
  return this.clienthttp.delete(`${this.url_base}/borrar/${id}`, { responseType: 'text' });
}
}
