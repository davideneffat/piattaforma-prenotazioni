package models

import (
	"time"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Schedule definisce la disponibilità di un servizio
type Schedule struct {
	ID          primitive.ObjectID `bson:"_id,omitempty"`
	ServiceName string             `bson:"serviceName"`
	// Per semplicità, teniamo solo una lista di slot già prenotati.
	BookedSlots []time.Time `bson:"bookedSlots"`
}

// Wallet rappresenta il saldo di un utente
type Wallet struct {
	ID      primitive.ObjectID `bson:"_id,omitempty"`
	UserID  int64              `bson:"userId"` // Corrisponde all'ID utente in PostgreSQL
	Balance float64            `bson:"balance"`
}