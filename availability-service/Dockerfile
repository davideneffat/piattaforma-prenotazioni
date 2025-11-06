FROM golang:1.25-alpine AS builder

WORKDIR /app

# Dipendenze
COPY go.mod go.sum ./
RUN go mod download

# Codice sorgente
COPY . .

# Compila l'applicazione
RUN CGO_ENABLED=0 GOOS=linux go build -o /availability-server ./cmd/main.go

FROM alpine:latest

WORKDIR /

# Copia il binario compilato
COPY --from=builder /availability-server /availability-server

EXPOSE 8082

ENTRYPOINT ["/availability-server"]