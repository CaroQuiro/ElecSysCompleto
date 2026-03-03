/** DTO para las actividades individuales de la orden de trabajo */
export interface DetalleOrdenTrabajoDTO {
  idDetalleTrabajo?: number;
  idOrden?: number;
  actividad: string;
  observaciones: string;
  duracion: string; // Manejado como VARCHAR(200)
}

/** DTO para la cabecera de la orden de trabajo */
export interface OrdenDeTrabajoDTO {
  id_orden?: number;
  id_orden_visita: number | null; // Relación opcional con la visita previa
  id_lugar: number;
  id_cliente: number;
  id_trabajador: number;
  fecha_realizacion: string;
  estado: string; // PENDIENTE, EN_PROCESO, FINALIZADA
}

/** Objeto para la creación masiva (Orden + Lista de Detalles) */
export interface OrdenDeTrabajoRequest {
    orden: OrdenDeTrabajoDTO;        // Antes decía OrdenDeVisitaDTO
    detalles: DetalleOrdenTrabajoDTO[];
}

/** Interfaz extendida para la lógica de edición en la tabla del frontend */
export interface DetalleTrabajoUI extends DetalleOrdenTrabajoDTO {
  editando: boolean;
  esNuevo?: boolean;
}