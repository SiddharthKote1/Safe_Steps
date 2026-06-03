from datetime import datetime, timedelta
from typing import Optional
from jose import JWTError, jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from app.config import settings
from app.models.user import User
from app.utils.logger import Logger

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/login", auto_error=False)


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    to_encode = data.copy()
    expire = datetime.utcnow() + (expires_delta or timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES))
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, settings.SECRET_KEY, algorithm="HS256")


async def get_current_user(token: Optional[str] = Depends(oauth2_scheme)) -> User:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    if not token:
        Logger.warn("Authentication: Missing bearer token")
        raise credentials_exception

    # Bypass JWT validation for local testing with Supabase tokens
    # Using a fixed ID ensures that sessions, reports, and analytics map to the same user consistently.
    return User(
        id="local-test-user-123",
        full_name="Local User",
        phone="+917353655122"
    )


# In-memory OTP store (phone → code)
otp_store: dict[str, str] = {}
