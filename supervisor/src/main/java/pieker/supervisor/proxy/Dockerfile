FROM python:3.13-slim

WORKDIR /app
COPY supervisor-proxy.py .

RUN pip install fastapi uvicorn httpx

CMD ["uvicorn", "supervisor-proxy:app", "--host", "0.0.0.0", "--port", "8000"]
