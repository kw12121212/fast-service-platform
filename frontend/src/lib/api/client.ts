const SERVICE_ROOT = '/service'

type SearchParamValue = string | number | boolean | null | undefined

export type SearchParams = Record<string, SearchParamValue>

type ResponseMode = 'json' | 'auto'

async function requestService<T>(
  servicePath: string,
  params?: SearchParams,
  responseMode: ResponseMode = 'json',
) {
  const url = new URL(`${SERVICE_ROOT}/${servicePath}`, window.location.origin)

  if (params) {
    for (const [key, value] of Object.entries(params)) {
      if (value !== null && value !== undefined) {
        url.searchParams.set(key, String(value))
      }
    }
  }

  const response = await fetch(url, {
    headers: {
      Accept: 'application/json',
    },
  })

  const body = await response.text()

  if (!response.ok) {
    const detail = body.trim()
    throw new Error(
      detail.length > 0
        ? `Backend request failed with ${response.status}: ${detail}`
        : `Backend request failed with ${response.status}`,
    )
  }

  if (responseMode === 'auto') {
    if (body.length === 0) {
      return undefined as T
    }

    try {
      return JSON.parse(body) as T
    } catch {
      return body as T
    }
  }

  try {
    return JSON.parse(body) as T
  } catch (error) {
    throw new Error(
      error instanceof Error
        ? `Backend returned invalid JSON: ${error.message}`
        : 'Backend returned invalid JSON',
    )
  }
}

export async function getJson<T>(servicePath: string, params?: SearchParams) {
  return requestService<T>(servicePath, params, 'json')
}

export async function invokeService<T>(
  servicePath: string,
  params?: SearchParams,
) {
  return requestService<T>(servicePath, params, 'auto')
}
