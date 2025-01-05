import { api } from './client'

export interface Location {
  id: number
  name: string
  latitude: number
  longitude: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const locationService = {
  getAllLocations: async (page = 0, size = 10) => {
    const response = await api.get<PageResponse<Location>>(`locations?page=${page}&size=${size}`)
    return response.data
  },

  getLocationById: async (id: number) => {
    const response = await api.get<Location>(`locations/${id}`)
    return response.data
  },

  createLocation: async (location: Omit<Location, 'id'>) => {
    const response = await api.post<Location>('locations', location)
    return response.data
  },

  updateLocation: async (id: number, location: Omit<Location, 'id'>) => {
    const response = await api.put<Location>(`locations/${id}`, location)
    return response.data
  },

  deleteLocation: async (id: number) => {
    await api.delete(`locations/${id}`)
  }
} 