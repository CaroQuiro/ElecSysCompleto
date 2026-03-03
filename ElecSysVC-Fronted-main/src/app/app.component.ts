import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CotizacionesComponent } from "./cotizaciones/cotizaciones.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})


export class AppComponent {
  title = 'ElecSys-VC-Frontend';

  constructor(private router:Router){}

}
