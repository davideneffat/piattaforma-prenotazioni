from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from .database import get_db
from .models import User

router = APIRouter()

@router.get("/users/by-username/{username}")
def get_user_by_username(username: str, db: Session = Depends(get_db)):
    """
    Ritorna le informazioni base di un utente dato lo username.
    Esempio di risposta:
    {
        "id": 1,
        "username": "mario",
        "email": "mario@example.com"
    }
    """
    user = db.query(User).filter(User.username == username).first()

    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    return {
        "id": user.id,
        "username": user.username,
        "email": user.email
    }
