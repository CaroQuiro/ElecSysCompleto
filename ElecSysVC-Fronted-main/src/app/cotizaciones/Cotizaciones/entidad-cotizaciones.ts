export class EntidadCotizaciones {
    id_cotizacion!: number;
    id_trabajador!: number;
    id_cliente!: number;
    id_lugar!: number;
    fecha_realizacion!: string;
    referencia!: string;
    valor_total!: number;
    estado!: string;
    administracion!: number;
    imprevistos!: number;
    utilidad!: number;
    iva!: number;
    total_pagar!: number;
    tiene_aiu?: boolean;
}
