from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import pandas as pd
import joblib
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import StandardScaler

app = FastAPI()

try:
    model = joblib.load('modelo_logistico.pkl')
    scaler = joblib.load('escalador.pkl')
except:
    model = None
    scaler = None


class Cotizacion(BaseModel):
    total: float
    materiales: int
    items: int
    es_nuevo: str
    tiene_tramites: str


class CotizacionHistorica(BaseModel):
    total: float
    materiales: int
    items: int
    es_nuevo: str
    tiene_tramites: str
    estado: str


@app.post("/predict")
def predict_acceptance(data: Cotizacion):
    if model is None or scaler is None:
        return {"error": "El modelo no ha sido entrenado aún."}

    nuevo_enc = 1 if data.es_nuevo.lower() == 'si' else 0
    tramites_enc = 1 if data.tiene_tramites.lower() == 'si' else 0

    columnas = ['TotalAPagarConTodo', 'CantidadMaterial/manoDeObra', 'CantidadItems', 'SiEsClienteNuevo_Enc',
                'IncluyeTramites_Enc']
    df_input = pd.DataFrame([[data.total, data.materiales, data.items, nuevo_enc, tramites_enc]], columns=columnas)

    datos_escalados = scaler.transform(df_input)
    probabilidad = model.predict_proba(datos_escalados)[0][1]

    return {"probabilidad_aceptacion": round(probabilidad * 100, 2)}


@app.post("/train")
def train_model(data: List[CotizacionHistorica]):
    global model, scaler

    datos_dict = [item.dict() for item in data]
    df = pd.DataFrame(datos_dict)

    df['SiEsClienteNuevo_Enc'] = df['es_nuevo'].apply(lambda x: 1 if x.lower() == 'si' else 0)
    df['IncluyeTramites_Enc'] = df['tiene_tramites'].apply(lambda x: 1 if x.lower() == 'si' else 0)
    df['Target'] = df['estado'].apply(lambda x: 1 if x.lower() == 'aceptado' else 0)

    X = df[['total', 'materiales', 'items', 'SiEsClienteNuevo_Enc', 'IncluyeTramites_Enc']]
    y = df['Target']

    nuevo_scaler = StandardScaler()
    X_scaled = nuevo_scaler.fit_transform(X)

    nuevo_model = LogisticRegression()
    nuevo_model.fit(X_scaled, y)

    joblib.dump(nuevo_model, 'modelo_logistico.pkl')
    joblib.dump(nuevo_scaler, 'escalador.pkl')



    model = nuevo_model
    scaler = nuevo_scaler

    return {
        "status": "Modelo re-entrenado con éxito",
        "registros_procesados": len(data)
    }