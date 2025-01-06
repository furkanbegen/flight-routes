import React, { useState } from 'react'
import { routeService, type Route } from '../api/routeService'
import { locationService, type Location } from '../api/locationService'
import { TransportationType } from '../api/transportationService'
import { SearchableSelect } from './SearchableSelect'
import toast from 'react-hot-toast'

export function Routes() {
  const [routes, setRoutes] = useState<Route[]>([])
  const [loading, setLoading] = useState(false)
  const [selectedRoute, setSelectedRoute] = useState<Route | null>(null)
  const [fromLocation, setFromLocation] = useState<Location | null>(null)
  const [toLocation, setToLocation] = useState<Location | null>(null)

  const handleSearch = async () => {
    if (!fromLocation || !toLocation) {
      toast.error('Please select both locations')
      return
    }

    setLoading(true)
    try {
      const routes = await routeService.findRoutes(fromLocation.id, toLocation.id)
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
            <SearchableSelect
              label="From"
              value={fromLocation}
              onChange={setFromLocation}
              onSearch={(query) => locationService.searchLocations(query)}
              placeholder="Search departure location..."
            />

            <SearchableSelect
              label="To"
              value={toLocation}
              onChange={setToLocation}
              onSearch={(query) => locationService.searchLocations(query)}
              placeholder="Search destination location..."
            />

            <div className="flex items-end">
              <button
                onClick={handleSearch}
                disabled={loading}
                className="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? 'Searching...' : 'Search Routes'}
              </button>
            </div>
          </div>
        </div>

        {/* Routes List */}
        <div className="space-y-4">
          {routes.map((route, idx) => (
            <div
              key={idx}
              onClick={() => setSelectedRoute(route)}
              className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                selectedRoute === route
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-blue-300'
              }`}
            >
              <div className="flex justify-between items-start">
                <div>
                  <div className="font-medium">
                    {route.transportations[0].fromLocation.name} →{' '}
                    {route.transportations[route.transportations.length - 1].toLocation.name}
                  </div>
                  <div className="text-sm text-gray-600">
                    {route.transportations.length} stops • {route.totalDuration} min •{' '}
                    ${route.totalPrice}
                  </div>
                </div>
                <button
                  onClick={(e) => {
                    e.stopPropagation()
                    setSelectedRoute(route)
                  }}
                  className="text-blue-600 hover:text-blue-800"
                >
                  View Details
                </button>
              </div>
            </div>
          ))}

          {routes.length === 0 && !loading && (
            <div className="text-center py-8 text-gray-500">
              No routes found. Try different locations.
            </div>
          )}
        </div>
      </div>

      {/* Route Details Sidebar */}
      {selectedRoute && (
        <div className="w-96 border-l border-slate-200 bg-white p-6 overflow-y-auto">
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