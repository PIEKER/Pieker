import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'

// Create the Axios instance
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8079', // fallback base URL
  timeout: 10000, // 10 seconds timeout
  headers: {
    'Content-Type': 'application/json',
  },
})

// Optional: Add a request interceptor
/*axiosInstance.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)*/

// Optional: Add a response interceptor
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle global errors here
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

const api = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
    axiosInstance.get<T>(url, config),

  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
    axiosInstance.post<T>(url, data, config),

  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
    axiosInstance.put<T>(url, data, config),

  patch: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
    axiosInstance.patch<T>(url, data, config),

  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> =>
    axiosInstance.delete<T>(url, config),
}

export default api
