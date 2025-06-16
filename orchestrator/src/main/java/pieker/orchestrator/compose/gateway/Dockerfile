FROM python:3.13-slim

WORKDIR /app
COPY orchestrator-proxy.py .

RUN pip install fastapi[all] httpx uvicorn sqlalchemy asyncpg

EXPOSE 42690

CMD ["uvicorn", "orchestrator-proxy:app", "--host", "0.0.0.0", "--port", "42690"]
