import { useState, Fragment, useEffect } from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { locationService } from '../api/locationService'
import { TransportationType, type Transportation, type TransportationRequest } from '../api/transportationService'
import toast from 'react-hot-toast'

interface TransportationModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: TransportationRequest) => Promise<void>
  initialData?: Transportation
  mode: 'create' | 'edit'
}

export function TransportationModal({ isOpen, onClose, onSubmit, initialData, mode }: TransportationModalProps) {
  const [locations, setLocations] = useState<Array<{ id: number; name: string }>>([])
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [formData, setFormData] = useState<TransportationRequest>({
    name: '',
    type: TransportationType.FLIGHT,
    fromLocationId: 0,
    toLocationId: 0,
    price: null,
    durationInMinutes: null
  })

  useEffect(() => {
    // Fetch locations for dropdowns
    const fetchLocations = async () => {
      try {
        const response = await locationService.getAllLocations(0, 100)
        setLocations(response.content)
      } catch (error) {
        toast.error('Failed to load locations')
      }
    }
    
    if (isOpen) {
      fetchLocations()
    }
  }, [isOpen])

  useEffect(() => {
    if (initialData && mode === 'edit') {
      setFormData({
        name: initialData.name,
        type: initialData.type,
        fromLocationId: initialData.fromLocation.id,
        toLocationId: initialData.toLocation.id,
        price: initialData.price,
        durationInMinutes: initialData.durationInMinutes
      })
    } else {
      setFormData({
        name: '',
        type: TransportationType.FLIGHT,
        fromLocationId: 0,
        toLocationId: 0,
        price: null,
        durationInMinutes: null
      })
    }
  }, [initialData, mode])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    
    try {
      await onSubmit(formData)
      onClose()
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Transition appear show={isOpen} as={Fragment}>
      <Dialog as="div" className="relative z-10" onClose={onClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black bg-opacity-25" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-300"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="ease-in duration-200"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-lg bg-white p-6 shadow-xl transition-all">
                <Dialog.Title as="h3" className="text-lg font-medium leading-6 text-gray-900 mb-4">
                  {mode === 'create' ? 'Add New Transportation' : 'Edit Transportation'}
                </Dialog.Title>

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                      Name
                    </label>
                    <input
                      type="text"
                      id="name"
                      value={formData.name}
                      onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label htmlFor="type" className="block text-sm font-medium text-gray-700">
                      Type
                    </label>
                    <select
                      id="type"
                      value={formData.type}
                      onChange={(e) => setFormData(prev => ({ ...prev, type: e.target.value as TransportationType }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      required
                    >
                      {Object.values(TransportationType).map(type => (
                        <option key={type} value={type}>{type}</option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label htmlFor="fromLocation" className="block text-sm font-medium text-gray-700">
                      From Location
                    </label>
                    <select
                      id="fromLocation"
                      value={formData.fromLocationId}
                      onChange={(e) => setFormData(prev => ({ ...prev, fromLocationId: Number(e.target.value) }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      required
                    >
                      <option value="">Select location</option>
                      {locations.map(location => (
                        <option key={location.id} value={location.id}>{location.name}</option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label htmlFor="toLocation" className="block text-sm font-medium text-gray-700">
                      To Location
                    </label>
                    <select
                      id="toLocation"
                      value={formData.toLocationId}
                      onChange={(e) => setFormData(prev => ({ ...prev, toLocationId: Number(e.target.value) }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      required
                    >
                      <option value="">Select location</option>
                      {locations.map(location => (
                        <option key={location.id} value={location.id}>{location.name}</option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label htmlFor="price" className="block text-sm font-medium text-gray-700">
                      Price
                    </label>
                    <input
                      type="number"
                      id="price"
                      step="0.01"
                      min="0"
                      value={formData.price ?? ''}
                      onChange={(e) => setFormData(prev => ({ 
                        ...prev, 
                        price: e.target.value ? Number(e.target.value) : null 
                      }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label htmlFor="duration" className="block text-sm font-medium text-gray-700">
                      Duration (minutes)
                    </label>
                    <input
                      type="number"
                      id="duration"
                      min="0"
                      value={formData.durationInMinutes ?? ''}
                      onChange={(e) => setFormData(prev => ({ 
                        ...prev, 
                        durationInMinutes: e.target.value ? Number(e.target.value) : null 
                      }))}
                      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                    />
                  </div>

                  <div className="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      onClick={onClose}
                      className="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      disabled={isSubmitting}
                      className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
                    >
                      {isSubmitting 
                        ? (mode === 'create' ? 'Adding...' : 'Saving...') 
                        : (mode === 'create' ? 'Add Transportation' : 'Save Changes')}
                    </button>
                  </div>
                </form>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition>
  )
} 