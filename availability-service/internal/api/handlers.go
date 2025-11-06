package api

import (
	"context"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"

	"availability-service/internal/database"
	"availability-service/internal/models"
)

// --- Structs per le richieste JSON ---
type CheckRequest struct {
	ServiceName string    `json:"serviceName"`
	BookingTime time.Time `json:"bookingTime"`
}

type ChargeRequest struct {
	UserID int64   `json:"userId"`
	Amount float64 `json:"amount"`
}


func CheckAvailability(c *gin.Context) {
	var req CheckRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	collection := database.Client.Database("availabilitydb").Collection("schedules")
	
	var schedule models.Schedule
	err := collection.FindOne(context.Background(), bson.M{"serviceName": req.ServiceName}).Decode(&schedule)
	
	if err != nil {
		// Se non esiste una schedule, consideriamo il servizio non disponibile. TODO: crearlo?
		c.JSON(http.StatusNotFound, gin.H{"available": false, "reason": "Service schedule not found"})
		return
	}

	// Controlla se lo slot è già nella lista dei prenotati
	for _, slot := range schedule.BookedSlots {
		if slot.Equal(req.BookingTime) {
			c.JSON(http.StatusOK, gin.H{"available": false, "reason": "Slot already booked"})
			return
		}
	}
	
	c.JSON(http.StatusOK, gin.H{"available": true})
}


func ChargeWallet(c *gin.Context) {
	var req ChargeRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	
	walletsCollection := database.Client.Database("availabilitydb").Collection("wallets")
	schedulesCollection := database.Client.Database("availabilitydb").Collection("schedules")
	
	// --- Logica Transazionale Semplificata ---
	// 1. Controlla il saldo
	var wallet models.Wallet
	// Se il wallet non esiste, lo creiamo con un saldo iniziale (es. 100) per test
	opts := options.FindOneAndUpdate().SetUpsert(true).SetReturnDocument(options.After)
	err := walletsCollection.FindOneAndUpdate(
		context.Background(),
		bson.M{"userId": req.UserID},
		bson.M{"$setOnInsert": bson.M{"balance": 100.0}},
		opts,
	).Decode(&wallet)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not retrieve or create wallet"})
		return
	}

	if wallet.Balance < req.Amount {
		c.JSON(http.StatusPaymentRequired, gin.H{"success": false, "reason": "Insufficient funds"})
		return
	}
	
	// 2. Addebita il saldo e prenota lo slot (in modo non atomico per semplicità)
	// In un sistema reale, questa operazione dovrebbe essere una transazione MongoDB.
	
	// Addebita
	_, err = walletsCollection.UpdateOne(
		context.Background(),
		bson.M{"userId": req.UserID},
		bson.M{"$inc": bson.M{"balance": -req.Amount}},
	)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to charge wallet"})
		return
	}

	// Aggiungi lo slot prenotato
	// NOTA: il corpo della richiesta dovrebbe contenere anche serviceName e bookingTime
	// per ora lo omettiamo per semplicità, ma andrebbe aggiunto.
	
	c.JSON(http.StatusOK, gin.H{"success": true})
}

// Funzione helper per creare le rotte
func SetupRouter() *gin.Engine {
	r := gin.Default()
	
	r.POST("/availability/check", CheckAvailability)
	r.POST("/payments/charge", ChargeWallet)

	return r
}