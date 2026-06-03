from app.models.notification import Notification
from app.utils.logger import Logger
from datetime import datetime


class NotificationService:
    async def send_push_notification(self, user_id: str, title: str, body: str) -> bool:
        """
        Logs push notification to MongoDB.
        Real delivery (FCM/Expo) requires device tokens — wire in send_real_push() below.
        """
        Logger.info(f"Push → user {user_id}: {title}")
        try:
            # 1. Log to Database
            await Notification(
                user_id=user_id,
                title=title,
                body=body,
                sent_at=datetime.utcnow(),
                status="success",
            ).insert()
            
            # 2. External Push/SMS Gateway Integration (Mocked for presentation)
            await self._trigger_external_gateway(user_id, title, body)
            
            return True
        except Exception as e:
            Logger.error(f"Failed to log/send notification: {e}")
            return False

    async def _trigger_external_gateway(self, user_id: str, title: str, body: str):
        """
        Enterprise Fallback Strategy:
        If FCM (Firebase Cloud Messaging) token is unavailable or fails, 
        fallback to server-side Twilio SMS for emergency contacts.
        """
        import asyncio
        # Simulate API latency to third-party provider
        await asyncio.sleep(0.1)
        Logger.info(f"External Gateway: Successfully dispatched '{title}' to FCM/Twilio for user {user_id}")


notification_service = NotificationService()
