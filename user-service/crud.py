from sqlalchemy.orm import Session
from models import User
from passlib.context import CryptContext
from schemas import UserCreate

pwd_context = CryptContext(schemes=["argon2"], deprecated="auto")

def create_user(db: Session, user: UserCreate):
    hashed = pwd_context.hash(user.password)
    db_user = User(username=user.username, email=user.email, hashed_password=hashed)
    db.add(db_user)
    db.commit()
    db.refresh(db_user) 
    return db_user
