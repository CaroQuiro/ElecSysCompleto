import joblib
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.metrics import accuracy_score, classification_report

df = pd.read_csv('DataSetElecSys_72Acc.csv')

le_nuevo = LabelEncoder()
df['SiEsClienteNuevo_Enc'] = le_nuevo.fit_transform(df['SiEsClienteNuevo'])

le_tramites = LabelEncoder()
df['IncluyeTramites_Enc'] = le_tramites.fit_transform(df['IncluyeTramites'])


df['Target'] = df['Estado'].map({'Aceptado': 1, 'Rechazado': 0})

X = df[['TotalAPagarConTodo', 'CantidadMaterial/manoDeObra', 'CantidadItems', 'SiEsClienteNuevo_Enc',
        'IncluyeTramites_Enc']]
y = df['Target']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

model = LogisticRegression()
model.fit(X_train_scaled, y_train)

y_pred = model.predict(X_test_scaled)

y_pred = model.predict(X_test_scaled)
print(f"Precisión del modelo: {accuracy_score(y_test, y_pred) * 100:.2f}%")

def predecir_probabilidad(total, materiales, items, es_nuevo, tiene_tramites):
    nuevo_enc = 1 if es_nuevo.lower() == 'si' else 0
    tramites_enc = 1 if tiene_tramites.lower() == 'si' else 0

    columnas = ['TotalAPagarConTodo', 'CantidadMaterial/manoDeObra', 'CantidadItems', 'SiEsClienteNuevo_Enc', 'IncluyeTramites_Enc']
    datos_entrada = pd.DataFrame([[total, materiales, items, nuevo_enc, tramites_enc]], columns=columnas)

    datos_escalados = scaler.transform(datos_entrada)

    probabilidad = model.predict_proba(datos_escalados)[0][1]
    return probabilidad * 100


p = predecir_probabilidad(5000000, 50, 10, 'si', 'no')
print(f"Probabilidad de aceptación: {p:.2f}%")

joblib.dump(model, 'modelo_logistico.pkl')
joblib.dump(scaler, 'escalador.pkl')
