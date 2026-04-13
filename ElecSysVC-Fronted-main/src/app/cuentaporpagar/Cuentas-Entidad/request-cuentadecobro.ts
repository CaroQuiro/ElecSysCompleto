import { EntidadDetalleCuenta } from "./DetalleCuentaEntidad";

export interface CuentaForm {
  id_trabajador: number;
  id_cliente: number;
  nota: string;
  fecha_realizacion: string;
  monto: number;
  estado: string;
  referencia_pdf?: string;
}

export interface CrearCuentaRequest {
  cuentaPorPagarDTO: CuentaForm;
  detalleCuentaDTOS: EntidadDetalleCuenta[];
}

export interface DetalleCuentaForm{
    descripcion: string;
    valor: number;
}

export interface DetalleCuentaUI{
  id_detalle_cuenta: number;
  id_cuenta_pagar?: number;
  descripcion: string;
  valor: number;
  editando?: boolean;
  esNuevo?: boolean;
}