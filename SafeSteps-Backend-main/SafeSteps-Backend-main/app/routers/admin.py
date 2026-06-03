from fastapi import APIRouter
from datetime import datetime
from app.utils.logger import Logger

router = APIRouter(tags=["Admin & Monitoring"])


@router.api_route("/health", methods=["GET", "HEAD"])
async def get_health():
    from app.database import get_motor_client
    try:
        client = get_motor_client()
        await client.admin.command("ping")
        db_status = "healthy"
    except Exception as e:
        Logger.error(f"Health: MongoDB ping failed: {e}")
        db_status = "unhealthy"
    return {"status": "healthy", "database": db_status, "timestamp": datetime.utcnow().isoformat()}


@router.get("/metrics")
async def get_metrics():
    from app.models.user import User
    from app.models.session import EmergencySession
    import psutil
    
    total_users = await User.count()
    total_sessions = await EmergencySession.count()
    active_sessions = await EmergencySession.find(EmergencySession.status == "active").count()
    
    return {
        "cpu_usage_pct": psutil.cpu_percent(interval=0.1), 
        "memory_usage_mb": psutil.virtual_memory().used / (1024 * 1024),
        "total_users": total_users,
        "total_historical_sessions": total_sessions,
        "active_live_sessions": active_sessions,
    }


@router.get("/version")
def get_version():
    return {"app_name": "Safe Steps AI Backend", "version": "2.0.0", "api_environment": "production"}
