import React, { useState, useEffect } from 'react'
import { routeService, type Route } from '../api/routeService'
import { locationService, type Location } from '../api/locationService'
import { TransportationType } from '../api/transportationService'
import toast from 'react-hot-toast'

export function Routes() {
  const [routes, setRoutes] = useState<Route[]>([])
  const [locations, setLocations] = useState<Location[]>([])
  const [loading, setLoading] = useState(false)
  const [selectedRoute, setSelectedRoute] = useState<Route | null>(null)
  const [fromLocationId, setFromLocationId] = useState<number>(0)
  const [toLocationId, setToLocationId] = useState<number>(0)

  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const response = await locationService.getAllLocations(0, 100)
        setLocations(response.content)
      } catch (error) {
        toast.error('Failed to load locations')
      }
    }
    fetchLocations()
  }, [])

  const handleSearch = async () => {
    if (!fromLocationId || !toLocationId) {
      toast.error('Please select both locations')
      return
    }

    setLoading(true)
    try {
      const routes = await routeService.findRoutes(fromLocationId, toLocationId)
      console.log('Found routes:', routes)
      setRoutes(routes)
      setSelectedRoute(null)
    } catch (error) {
      console.error('Error finding routes:', error)
      toast.error('Failed to find routes')
    } finally {
      setLoading(false)
    }
  }


  return (
    <div className="flex h-full">
      <div className="flex-1 p-6 lg:p-8">
        <div className="mb-6">
          <h1 className="text-2xl font-semibold text-slate-800 mb-4">Find Routes</h1>
          <div className="grid md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">From</label>
              <select
                value={fromLocationId}
                onChange={(e) => setFromLocationId(Number(e.target.value))}
                className="w-full p-2 border border-gray-300 rounded-md"
              >
                <option value="">Select location</option>
                {locations.map(location => (
                  <option key={location.id} value={location.id}>{location.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">To</label>
              <select
                value={toLocationId}
                onChange={(e) => setToLocationId(Number(e.target.value))}
                className="w-full p-2 border border-gray-300 rounded-md"
              >
                <option value="">Select location</option>
                {locations.map(location => (
                  <option key={location.id} value={location.id}>{location.name}</option>
                ))}
              </select>
            </div>
            <div className="flex items-end">
              <button
                onClick={handleSearch}
                disabled={loading}
                className="w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 focus:ring-4 focus:ring-blue-200 disabled:opacity-50"
              >
                {loading ? 'Searching...' : 'Search Routes'}
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-slate-200">
          {routes.length > 0 ? (
            <div className="divide-y divide-slate-200">
              {routes.map((route, index) => {
                const flight = route.transportations.find(t => t.type === TransportationType.FLIGHT)
                const transportCount = route.transportations.length
                return (
                  <div
                    key={index}
                    onClick={() => setSelectedRoute(route)}
                    className={`p-4 cursor-pointer hover:bg-slate-50 transition-colors ${
                      selectedRoute === route ? 'bg-blue-50' : ''
                    }`}
                  >
                    <div className="text-sm font-medium text-slate-900 mb-2">
                      Via {flight?.fromLocation.name}
                    </div>
                    <div className="mt-2 text-sm text-slate-600">
 {transportCount} {transportCount === 1 ? 'transport' : 'transports'} -
                      {route.totalDuration ? `${route.totalDuration} min` : 'N/A'} - 
                      {route.totalPrice ? `$${route.totalPrice}` : 'N/A'}
                    </div>
                  </div>
                )
              })}
            </div>
          ) : (
            <div className="p-8 text-center text-slate-500">
              {loading ? 'Searching for routes...' : 'No routes found'}
            </div>
          )}
        </div>
      </div>

      {/* Side Panel for Route Details */}
      {selectedRoute && (
        <div className="w-96 border-l border-slate-200 bg-white p-6 overflow-y-auto flex flex-col h-full">
          <h2 className="text-lg font-semibold text-slate-800 mb-6">Route Details</h2>
          
          <div className="flex-1">
            <div className="relative">
              {selectedRoute.transportations.map((transport, idx) => (
                <div key={idx} className="flex items-start mb-8 relative">
                  {/* Dot */}
                  <div className="absolute left-0 w-3 h-3 rounded-full bg-blue-600" />
                  
                  {/* Vertical line connecting dots - now includes the last section */}
                  <div className="absolute left-1.5 top-3 w-0.5 h-16 bg-blue-200" 
                       style={{ transform: 'translateX(-50%)' }} />
                  
                  {/* Content */}
                  <div className="ml-8">
                    <div className="font-medium text-slate-900 mb-1">
                      {transport.fromLocation.name}
                    </div>
                    <div className="text-sm text-slate-600 mb-2">
                      {transport.name} ({transport.type})
                      <br />
                      Duration: {transport.durationInMinutes} min
                      {transport.price ? ` • Price: $${transport.price}` : ''}
                    </div>
                  </div>
                </div>
              ))}
              
              {/* Final destination */}
              <div className="flex items-start">
                <div className="absolute left-0 w-3 h-3 rounded-full bg-blue-600" />
                <div className="ml-8">
                  <div className="font-medium text-slate-900">
                    {selectedRoute.transportations[selectedRoute.transportations.length - 1].toLocation.name}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Footer with totals and close button */}
          <div className="mt-6 pt-4 border-t border-slate-200">
            <div className="mb-4">
              <div className="text-sm text-slate-700">
                Total Duration: {selectedRoute.totalDuration} min
              </div>
              <div className="text-sm text-slate-700">
                Total Price: ${selectedRoute.totalPrice}
              </div>
            </div>
            
            <button
              onClick={() => setSelectedRoute(null)}
              className="w-full px-4 py-2 bg-slate-100 text-slate-700 rounded-lg hover:bg-slate-200 transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  )
} 