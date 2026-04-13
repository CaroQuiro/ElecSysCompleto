export class ContratoEntidad {
    id_contrato!: number;
    id_trabajador!: number;
    sueldo!: number;
    fecha_expedicion!: Date | null;
    fecha_iniciacion!: Date | null;
    id_trabajador_encargado!: number;
    cargo!: string;
    tipo_contrato!: string;
    estado!: string;
    fecha_nacimiento!: Date | null;
    lugar_nacimiento!: string;
    edad!: number;
    estadoCivil!: string;
}

export class TrabajadorEntidad{
    id_trabajador!: number;
    nombre!: string;
    telefono!: string;
    direccion!: string;
    correo!: string;
    tipo_usuario!: string;
    password!: string;
    estado!: string;
}
