from fastapi import FastAPI, Request, Response
import httpx
from starlette.responses import JSONResponse

app = FastAPI()

@app.api_route("/{target}/{path:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
async def proxy(target: str, path: str, request: Request):
    try:
        target_url = f"http://{target}:8000/{path}"
        client = httpx.AsyncClient()
        response = await client.request(
            method=request.method,
            url=target_url,
            headers={key: value for key, value in request.headers.items() if key != 'host'},
            content=await request.body()
        )
        return Response(
            content=response.content,
            status_code=response.status_code,
            headers=response.headers
        )
    except httpx.RequestError as e:
        return JSONResponse(status_code=502, content={"error": str(e)})
