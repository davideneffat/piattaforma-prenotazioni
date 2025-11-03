from fastapi import Depends, FastAPI
from sqlalchemy.orm import Session
from crud import create_user
from database import SessionLocal
from schemas import UserCreate

app = FastAPI()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/register")
def register(user: UserCreate, db: Session = Depends(get_db)):
    return create_user(db, user)
