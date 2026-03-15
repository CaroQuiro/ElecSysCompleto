import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class HistorialService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/internal/historial-actividad';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/listar`);
  }

  buscarDetalles(idHistorial: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/detalle/listar/${idHistorial}`);
  }
}