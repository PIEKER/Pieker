import os

import httpx
from fastapi import FastAPI, Request, Response, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy import text
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from starlette.responses import JSONResponse

app = FastAPI()

#
# --- HTTP Proxy ---
#

@app.get("/http/{target}/{path:path}")
async def proxy_http_get(target: str, path: str, request: Request):
    try:
        target_url = f"http://{target}:42690/{path}"
        async with httpx.AsyncClient() as client:
            body = await request.body()
            response = await client.request(
                method=request.method,
                url=target_url,
                headers={key: value for key, value in request.headers.items() if key.lower() != 'host'},
                content=body if body else None
            )

        # Filter out headers that are not safe to forward directly
        response_headers = {
            k: v for k, v in response.headers.items()
            if k.lower() not in ['content-encoding', 'transfer-encoding', 'connection']
        }

        return Response(
            content=response.content,
            status_code=response.status_code,
            headers=response_headers
        )
    except httpx.RequestError as e:
        return JSONResponse(status_code=502, content={"error": str(e)})

#
# --- Database Proxy ---
#

DB_USER = os.getenv("DB_USER", "testuser")
DB_PASSWORD = os.getenv("DB_PASSWORD", "testpass")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "testdb")

DB_URL = f"postgresql+asyncpg://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

engine = create_async_engine(DB_URL, echo=True, future=True)
SessionLocal = sessionmaker(bind=engine, class_=AsyncSession, expire_on_commit=False)

@app.post("/db/query")
async def run_query(request: Request):
    try:
        body = await request.json()
        query = body.get("query")
        if not query:
            raise HTTPException(status_code=400, detail="Missing 'query' in request body.")
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Invalid JSON body: {str(e)}")

    try:
        async with SessionLocal() as session:
            result = await session.execute(text(query))
            if query.strip().lower().startswith("select"):
                rows = result.fetchall()
                keys = result.keys()
                return {"result": [dict(zip(keys, row)) for row in rows]}
            else:
                await session.commit()
                return {"status": "success"}
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
