import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalContratoService {

  private abrirFlotanteSource = new Subject<void>();

  abrirFlotante$ = this.abrirFlotanteSource.asObservable();

  abrirFlotante(){
    this.abrirFlotanteSource.next();
  }
}
