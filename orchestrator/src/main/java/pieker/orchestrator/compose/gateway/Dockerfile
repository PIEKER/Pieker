FROM python:3.13-slim

WORKDIR /app
COPY gateway.py .

RUN pip install fastapi[all] httpx uvicorn sqlalchemy asyncpg

EXPOSE 42690

CMD ["uvicorn", "gateway:app", "--host", "0.0.0.0", "--port", "42690"]
