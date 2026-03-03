import { EntidadDetalleCotizacion } from "./entidad-detalleCotizacion";

export interface RequestCotizacion {
    cotizacion: CotizacionForm;
    detalleCotizacionDTOS: DetalleCotizacionForm[];
    existIva: boolean;
    aiudto?: AIUForm;
}

export interface CotizacionForm{
  id_cotizacion: number;
  id_trabajador: number;
  id_cliente: number;
  id_lugar: number;
  fecha_realizacion: string;
  referencia: string;
  estado: string;
}

export interface DetalleCotizacionForm {
  id_detalle_cotizacion?: number;
  descripcion: string;
  cantidad: number;
  valor_unitario: number;
}

export interface AIUForm{
  administracion: number; 
  imprevistos: number;    
  utilidad: number;   
}


export interface DetalleUI {
  id_detalle_cotizacion: number;
  id_cotizacion?: number;
  descripcion: string;
  cantidad: number;
  valor_unitario: number;
  subtotal: number;
  editando?: boolean;   // controla si los inputs están habilitados
  esNuevo?: boolean;    // para saber si se crea o se actualiza
}


