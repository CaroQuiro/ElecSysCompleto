import { ContratoEntidad } from "./contrato-entidad";

export class requestContrato{
    contrato!: ContratoEntidad;
    fecha_nacimiento!: Date | null;
    lugar_nacimiento!: string;
    edad!: number;
    estadoCivil!: string;
}