import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EntidadCliente } from './entidad-cliente';

@Injectable({
  providedIn: 'root'
})
export class ServiceClienteService {

  private url_base = 'http://localhost:8080/api/clientes';
  private clienthttp = inject(HttpClient);

  listarClientes(): Observable<EntidadCliente[]>{
    return this.clienthttp.get<EntidadCliente[]>(`${this.url_base}/listar`);
  }

  buscarClienteQuery(query: string): Observable<EntidadCliente[]>{
    return this.clienthttp.get<EntidadCliente[]>(`${this.url_base}/buscar?query=${query}`);
  }

  buscarClientePorId(id: number){
    return this.clienthttp.get<EntidadCliente>(`${this.url_base}/buscar/${id}`);
  }

  crearCliente(clientedto: EntidadCliente) {
    return this.clienthttp.post(`${this.url_base}/agregar`, clientedto, {responseType: 'text'});
  } 

  actualizarCliente(clientedto: EntidadCliente){
    return this.clienthttp.put(`${this.url_base}/actualizar/${clientedto.id_cliente}`, clientedto, {responseType: 'text'});
  }

}