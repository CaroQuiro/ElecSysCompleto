export interface TrabajadorDTO {
  id_trabajador: number;        // Cédula o número de identidad (PK)
  nombre: string;
  telefono?: string;            // Opcional según SQL
  direccion?: string;           // Opcional según SQL
  correo: string;
  tipo_usuario: string;         // 'ADMINISTRADOR' o 'USUARIO'
  password: string;
  estado: string;               // Ej: 'ACTIVO', 'INACTIVO'
}