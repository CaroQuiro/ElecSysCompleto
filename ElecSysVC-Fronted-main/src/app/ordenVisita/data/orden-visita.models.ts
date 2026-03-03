export interface DetalleOrdenVisitaDTO {
  idDetalleVisita?: number;
  idVisita?: number;      
  actividad: string;
  observaciones: string;
  duracion: string;        
}

export interface OrdenDeVisitaDTO {
  idVisita?: number;      
  idLugar: number;
  idCliente: number;
  idTrabajador: number;
  fechaRealizacion: string;
  descripcion: string;
  estado: string;
}

export interface OrdenDeVisitaRequest {
  orden: OrdenDeVisitaDTO;
  detalles: DetalleOrdenVisitaDTO[];
}

export interface DetalleVisitaUI extends DetalleOrdenVisitaDTO {
  editando: boolean;
  esNuevo?: boolean;
}