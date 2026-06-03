import io
import wave
import requests
from app.config import settings
from app.utils.logger import Logger

# Languages Sarvam saaras:v1 supports
SUPPORTED_LANGUAGES = {
    "en-IN": "English",
    "hi-IN": "Hindi",
    "mr-IN": "Marathi",
    "bn-IN": "Bengali",
    "ta-IN": "Tamil",
    "te-IN": "Telugu",
    "kn-IN": "Kannada",
    "ml-IN": "Malayalam",
    "gu-IN": "Gujarati",
    "pa-IN": "Punjabi",
}


class STTService:
    def __init__(self):
        self.api_key = settings.SARVAM_API_KEY
        self.url = "https://api.sarvam.ai/speech-to-text"

    def pcm_to_wav(self, pcm_bytes: bytes, channels: int = 1, sampwidth: int = 2, framerate: int = 16000) -> bytes:
        """Converts raw PCM audio bytes into WAV format in-memory."""
        wav_io = io.BytesIO()
        with wave.open(wav_io, 'wb') as wav_file:
            wav_file.setnchannels(channels)
            wav_file.setsampwidth(sampwidth)
            wav_file.setframerate(framerate)
            wav_file.writeframes(pcm_bytes)
        return wav_io.getvalue()

    def transcribe(self, audio_bytes: bytes, language_code: str = "unknown") -> dict:
        """
        Transcribes raw PCM audio. Defaults to auto language detection ("unknown").
        Sarvam returns the detected language_code in the response — that flows downstream.

        Returns:
            dict: { "transcript": str, "language": str, "confidence": float }
        """
        if not audio_bytes:
            return {"transcript": "", "language": "en-IN", "confidence": 0.0}

        wav_bytes = self.pcm_to_wav(audio_bytes)

        if not self.api_key:
            Logger.info("Sarvam API key not set. Returning mock multilingual transcript.")
            import random
            mock_transcripts = [
                ("Help! Someone is following me!", "en-IN", 0.95),
                ("बचाओ! कोई मेरे पीछे आ रहा है!", "hi-IN", 0.93),
                ("वाचवा! कोणीतरी माझ्या मागे येत आहे!", "mr-IN", 0.91),
                ("Please stay back, do not touch me!", "en-IN", 0.98),
                ("मुझे छोड़ो! मदद करो!", "hi-IN", 0.96),
                ("I just got into a car accident.", "en-IN", 0.92),
                ("हादसा हो गया, मदद चाहिए!", "hi-IN", 0.90),
                ("अपघात झाला, मदत करा!", "mr-IN", 0.89),
            ]
            text, lang, conf = random.choice(mock_transcripts)
            return {"transcript": text, "language": lang, "confidence": conf}

        return self._call_sarvam(wav_bytes, language_code)

    def _call_sarvam(self, wav_bytes: bytes, language_code: str) -> dict:
        """
        Calls the Sarvam STT API. If "unknown" is rejected, retries with "en-IN".
        """
        headers = {"api-subscription-key": self.api_key}
        files = {"file": ("audio.wav", wav_bytes, "audio/wav")}

        # First attempt: with requested language_code (or "unknown" for auto-detect)
        data = {"model": "saaras:v3", "language_code": language_code}
        try:
            response = requests.post(self.url, headers=headers, files=files, data=data, timeout=15)

            # If "unknown" is unsupported, Sarvam returns 400 — retry with en-IN
            if response.status_code == 400 and language_code == "unknown":
                Logger.warn("Sarvam does not support 'unknown' language code. Retrying with en-IN.")
                data["language_code"] = "en-IN"
                files = {"file": ("audio.wav", wav_bytes, "audio/wav")}
                response = requests.post(self.url, headers=headers, files=files, data=data, timeout=15)

            if response.status_code == 200:
                res = response.json()
                detected = res.get("language_code", language_code)
                Logger.info(f"Sarvam STT success — detected language: {detected}")
                return {
                    "transcript": res.get("transcript", ""),
                    "language": detected,
                    "confidence": res.get("confidence", 0.9)
                }

            Logger.error(f"Sarvam STT failed [{response.status_code}]: {response.text}")
            raise Exception("STT API error")

        except Exception as e:
            Logger.error(f"Sarvam STT request error: {e}. Attempting Gemini fallback.")
            return self._gemini_fallback(wav_bytes)

    def _gemini_fallback(self, wav_bytes: bytes) -> dict:
        """Fallback to Google Gemini 1.5 Flash for audio transcription if Sarvam fails."""
        if not settings.GEMINI_API_KEY:
            Logger.error("Gemini API key not set for STT fallback.")
            return {"transcript": "", "language": "en-IN", "confidence": 0.0}
            
        try:
            from google import genai
            from google.genai import types
            client = genai.Client(api_key=settings.GEMINI_API_KEY)
            
            # Gemini understands audio parts natively
            prompt = "Transcribe this audio exactly. Do not add any commentary. If the language is Hindi or Marathi, transcribe it in the native script."
            response = client.models.generate_content(
                model='gemini-1.5-flash',
                contents=[
                    types.Part.from_bytes(data=wav_bytes, mime_type='audio/wav'),
                    prompt,
                ]
            )
            text = response.text.strip() if response.text else ""
            Logger.info("Gemini STT fallback success.")
            return {
                "transcript": text,
                "language": "en-IN", # We assume en-IN for downstream processing if Gemini handled it
                "confidence": 0.85 
            }
        except Exception as fallback_err:
            Logger.error(f"Gemini STT fallback also failed: {fallback_err}")
            return {
                "transcript": "",
                "language": "en-IN",
                "confidence": 0.0
            }


stt_service = STTService()
