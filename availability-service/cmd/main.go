package main

import (
	"availability-service/internal/api"
	"availability-service/internal/database"
)

func main() {
	database.ConnectDB()
	
	router := api.SetupRouter()
	router.Run(":8082") // Porta
}