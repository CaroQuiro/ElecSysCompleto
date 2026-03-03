import { inject, Injectable } from '@angular/core';
import { EntidadLugar } from './entidad-lugar';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ServiceLugarService {

  private url_base = 'http://localhost:8080/api/lugares-trabajo';
  private clienthttp = inject(HttpClient);

  listarLugares(): Observable<EntidadLugar[]>{
    return this.clienthttp.get<EntidadLugar[]>(`${this.url_base}/listar`);
  }

  buscarLugarQuery(query: string): Observable<EntidadLugar[]>{
    return this.clienthttp.get<EntidadLugar[]>(`${this.url_base}/buscar?query=${query}`);
  }

  buscarLugarPorId(id: number){
    return this.clienthttp.get<EntidadLugar>(`${this.url_base}/buscar/${id}`);
  }

  crearLugar(lugardto: EntidadLugar){
    return this.clienthttp.post(`${this.url_base}/agregar`, lugardto, {responseType: 'text'});
  }

  actualizarLugar(lugardto: EntidadLugar){
    return this.clienthttp.put(`${this.url_base}/actualizar/${lugardto.idLugar}`, lugardto, {responseType: 'text'});
  }
}
