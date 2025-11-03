# 1. Scegli un'immagine di base Python ufficiale e leggera
FROM python:3.11-slim

# 2. Imposta la directory di lavoro all'interno del container
WORKDIR /app

# 3. Copia prima il file delle dipendenze
#    Questo passaggio viene messo in cache da Docker. Se non cambi le dipendenze,
#    Docker non le reinstallerà ogni volta, rendendo il build molto più veloce.
COPY requirements.txt .

# 4. Installa le dipendenze
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copia tutto il resto del codice della tua applicazione
COPY . .

# 6. Esponi la porta su cui gira FastAPI (Uvicorn di default usa la 8000)
EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]