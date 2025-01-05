import { api } from './client'
import type { Transportation } from './transportationService'

export interface Route {
  transportations: Transportation[]
  totalDuration: number | null
  totalPrice: number | null
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  empty: boolean
}

export const routeService = {
  findRoutes: async (fromLocationId: number, toLocationId: number) => {
    const response = await api.get<PageResponse<Route>>(
      `routes?fromLocationId=${fromLocationId}&toLocationId=${toLocationId}`
    )
    return response.data.content // Return just the routes array
  }
} 