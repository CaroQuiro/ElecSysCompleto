// Estructura para una notificación individual
export interface NotificacionDTO {
  idNotificacion?: number;
  titulo: string;
  mensaje: string;
  tipo: 'UNICA' | 'RECURRENTE';
  estado?: string; // ACTIVA | INACTIVA
  fechaCreacion?: string | Date;
}

// Estructura para crear una programación completa (utilizada en el formulario)
export interface ProgramacionRequest {
  notificacion: NotificacionDTO;
  frecuencia: 'DIARIA' | 'SEMANAL' | 'MENSUAL' | 'SEMESTRAL' | 'ANUAL';
  fechaInicio: string;
  fechaFin: string | null;
  idsDestinatarios: number[];
  tipoDestinatario: 'CLIENTE' | 'TRABAJADOR';
}