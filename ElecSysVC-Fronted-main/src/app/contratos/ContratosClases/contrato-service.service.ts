import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ContratoEntidad, TrabajadorEntidad } from './contrato-entidad';
import { Observable } from 'rxjs';
import { requestContrato } from './contrato-request';

@Injectable({
  providedIn: 'root'
})
export class ContratoServiceService {

  private url_base = 'http://localhost:8080/api/contratos';
  private clienthttp = inject(HttpClient);

  listarContratos(): Observable<ContratoEntidad[]> {
    return this.clienthttp.get<ContratoEntidad[]>(`${this.url_base}/listar`);
  }

  buscarTrabajadorPorID(id:number){
    return this.clienthttp.get<TrabajadorEntidad>(`http://localhost:8080/api/trabajador/buscar/${id}`);
  }

   listarTrabajadores(): Observable<TrabajadorEntidad[]> {
    return this.clienthttp.get<TrabajadorEntidad[]>(`http://localhost:8080/api/trabajador/listar`);
  }

  crearContrato(solicitud: requestContrato){
     return this.clienthttp.post(`${this.url_base}/agregar`, solicitud, {responseType: 'blob'});
  }

  buscarContratoPorID(id: number): Observable<any>{
    return this.clienthttp.get(`${this.url_base}/buscar/${id}`);
  }

}
