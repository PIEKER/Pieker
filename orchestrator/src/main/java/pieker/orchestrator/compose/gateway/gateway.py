import httpx
import os
from fastapi import FastAPI, Request, Response, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy import text
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, AsyncEngine
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
DB_PASS = os.getenv("DB_PASS", "testpass")
DB_HOST = os.getenv("DB_HOST", "127.0.0.1")
DB_PORT = os.getenv("DB_PORT", "5432")

engine_cache: dict[str, AsyncEngine] = {}


def get_db_url(db_name: str) -> str:
    return f"postgresql+asyncpg://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}/{db_name}"


def get_session(db_name: str):
    if db_name not in engine_cache:
        engine_cache[db_name] = create_async_engine(get_db_url(db_name), echo=True, future=True)
    engine = engine_cache[db_name]
    return sessionmaker(bind=engine, class_=AsyncSession, expire_on_commit=False)


@app.post("/db/{db_name}/query")
async def run_query(db_name: str, request: Request):
    try:
        body = await request.json()
        query = body.get("query")
        if not query:
            raise HTTPException(status_code=400, detail="Missing 'query' in request body.")
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Invalid JSON body: {str(e)}")

    SessionLocal = get_session(db_name)

    try:
        async with SessionLocal() as session:
            result = await session.execute(text(query))
            if query.strip().lower().startswith("select"):
                rows = result.fetchall()
                keys = result.keys()

                # aggregate values per key
                aggregated = {key: [] for key in keys}
                for row in rows:
                    for key, value in zip(keys, row):
                        aggregated[key].append(value)

                return {"result": aggregated}
            else:
                await session.commit()
                return {"status": "success"}
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
