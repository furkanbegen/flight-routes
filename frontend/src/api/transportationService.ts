import { api } from './client'
import type { Location } from './locationService'

export enum TransportationType {
  FLIGHT = 'FLIGHT',
  OTHER = 'OTHER'
}

export interface Transportation {
  id: number
  name: string
  type: TransportationType
  fromLocation: Location
  toLocation: Location
  price: number | null
  durationInMinutes: number | null
}

export interface TransportationRequest {
  name: string
  type: TransportationType
  fromLocationId: number
  toLocationId: number
  price?: number | null
  durationInMinutes?: number | null
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const transportationService = {
  getAllTransportations: async (page = 0, size = 10) => {
    const response = await api.get<PageResponse<Transportation>>(`transportations?page=${page}&size=${size}`)
    return response.data
  },

  getTransportationById: async (id: number) => {
    const response = await api.get<Transportation>(`transportations/${id}`)
    return response.data
  },

  createTransportation: async (transportation: TransportationRequest) => {
    const response = await api.post<Transportation>('transportations', transportation)
    return response.data
  },

  updateTransportation: async (id: number, transportation: TransportationRequest) => {
    const response = await api.put<Transportation>(`transportations/${id}`, transportation)
    return response.data
  },

  deleteTransportation: async (id: number) => {
    await api.delete(`transportations/${id}`)
  }
} 